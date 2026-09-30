package com.capstone.champ.service;

import com.capstone.champ.model.Allergy;
import com.capstone.champ.model.Prescription;
import com.capstone.champ.model.User;
import com.capstone.champ.model.Visit;
import com.capstone.champ.payload.PatientHistorySummaryResponse;
import com.capstone.champ.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PatientHistorySummaryService {

    private final VisitRepository visitRepository;

    public PatientHistorySummaryResponse buildSummary(User patient) {
        if (patient == null) {
            return new PatientHistorySummaryResponse();
        }

        List<Visit> visits = visitRepository.findByUserId(patient.getId());
        visits.sort(Comparator.comparing(Visit::getIssueDate, Comparator.nullsLast(Comparator.reverseOrder())));

        PatientHistorySummaryResponse summary = new PatientHistorySummaryResponse();
        summary.setPatientId(patient.getId());
        summary.setPatientName(resolvePatientName(patient));
        summary.setTotalVisits(visits.size());
        summary.setDiagnoses(extractDiagnoses(visits));
        summary.setMedicines(extractMedicines(visits));
        summary.setAllergies(extractAllergies(patient));
        summary.setRecentVisits(visits.stream().limit(5).map(this::toRecentVisitSummary).toList());
        if (!visits.isEmpty()) {
            summary.setLatestVisit(toRecentVisitSummary(visits.get(0)));
        }
        summary.setImportantInformation(buildImportantInformation(visits, patient));
        return summary;
    }

    private String resolvePatientName(User patient) {
        if (patient.getUserDetails() != null && patient.getUserDetails().getFullName() != null) {
            return patient.getUserDetails().getFullName();
        }
        return patient.getAadhaarNumber();
    }

    private List<String> extractDiagnoses(List<Visit> visits) {
        Set<String> diagnoses = new LinkedHashSet<>();
        for (Visit visit : visits) {
            if (visit.getDiagnosis() != null && visit.getDiagnosis().getDiagnosisName() != null) {
                diagnoses.add(visit.getDiagnosis().getDiagnosisName());
            }
        }
        return new ArrayList<>(diagnoses);
    }

    private List<String> extractMedicines(List<Visit> visits) {
        Set<String> medicines = new LinkedHashSet<>();
        for (Visit visit : visits) {
            if (visit.getMedicines() == null) {
                continue;
            }
            for (Prescription prescription : visit.getMedicines()) {
                String medicineName = prescription.getMedicine() != null ? prescription.getMedicine().getMedicineName() : prescription.getMedicineName();
                if (medicineName != null && !medicineName.isBlank()) {
                    medicines.add(medicineName);
                }
            }
        }
        return new ArrayList<>(medicines);
    }

    private List<String> extractAllergies(User patient) {
        List<String> allergies = new ArrayList<>();
        if (patient.getAllergies() == null) {
            return allergies;
        }
        for (Allergy allergy : patient.getAllergies()) {
            if (allergy != null && allergy.getTitle() != null) {
                allergies.add(allergy.getTitle());
            }
        }
        return allergies;
    }

    private List<String> buildImportantInformation(List<Visit> visits, User patient) {
        List<String> importantInformation = new ArrayList<>();
        importantInformation.add("Total visits: " + visits.size());
        if (patient.getAllergies() != null && !patient.getAllergies().isEmpty()) {
            importantInformation.add("Known allergies: " + patient.getAllergies().size());
        }
        if (!visits.isEmpty()) {
            Visit latest = visits.get(0);
            if (latest.getDiagnosis() != null) {
                importantInformation.add("Latest diagnosis: " + latest.getDiagnosis().getDiagnosisName());
            }
            if (latest.getPredictedRecoveryDays() != null) {
                importantInformation.add("Predicted recovery: " + latest.getPredictedRecoveryDays() + " days");
            }
        }
        return importantInformation;
    }

    private PatientHistorySummaryResponse.RecentVisitSummary toRecentVisitSummary(Visit visit) {
        PatientHistorySummaryResponse.RecentVisitSummary summary = new PatientHistorySummaryResponse.RecentVisitSummary();
        summary.setVisitId(visit.getId());
        summary.setReason(visit.getReason());
        summary.setDiagnosis(visit.getDiagnosis() != null ? visit.getDiagnosis().getDiagnosisName() : null);
        summary.setIssueDate(visit.getIssueDate());
        summary.setRecoveryStatus(visit.getRecoveryStatus() != null ? visit.getRecoveryStatus().name() : null);
        summary.setPredictedRecoveryDays(visit.getPredictedRecoveryDays());

        List<String> medicines = new ArrayList<>();
        if (visit.getMedicines() != null) {
            for (Prescription prescription : visit.getMedicines()) {
                String medicineName = prescription.getMedicine() != null ? prescription.getMedicine().getMedicineName() : prescription.getMedicineName();
                if (medicineName != null && !medicineName.isBlank()) {
                    medicines.add(medicineName);
                }
            }
        }
        summary.setPrescriptionMedicines(medicines);
        return summary;
    }
}
