package com.capstone.champ.service;

import com.capstone.champ.exception.MlServiceException;
import com.capstone.champ.payload.fastapi.FastApiClient;
import com.capstone.champ.model.Prescription;
import com.capstone.champ.model.User;
import com.capstone.champ.model.UserDetails;
import com.capstone.champ.model.Visit;
import com.capstone.champ.payload.fastapi.FastApiMedicineSuccessRequest;
import com.capstone.champ.payload.fastapi.FastApiMedicineSuccessResponse;
import com.capstone.champ.payload.fastapi.FastApiRecoveryPredictionRequest;
import com.capstone.champ.payload.fastapi.FastApiRecoveryPredictionResponse;
import com.capstone.champ.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class MlPredictionService {

    private final FastApiClient fastApiClient;
    private final VisitRepository visitRepository;

    public void applyPredictions(Visit visit, User patient) {
        if (visit == null || patient == null) {
            return;
        }

        if (visit.getMedicines() != null) {
            for (Prescription prescription : visit.getMedicines()) {
                try {
                    FastApiMedicineSuccessRequest request = buildMedicineSuccessRequest(patient, visit, prescription);
                    log.info("Calling FastAPI medicine-success with request: {}", request);
                    FastApiMedicineSuccessResponse response = fastApiClient.predictMedicineSuccess(request);
                    if (response != null && response.getSuccessProbability() != null) {
                        prescription.setMedicineSuccessProbability(response.getSuccessProbability());
                        prescription.setMedicinePredictionStatus(response.getPrediction());
                        prescription.setMedicinePredictionGeneratedAt(LocalDateTime.now());
                    }
                } catch (MlServiceException exc) {
                    log.warn("Medicine success prediction failed for visit {}: {}", visit.getId(), exc.getMessage());
                }
            }
        }

        try {
            FastApiRecoveryPredictionRequest request = buildRecoveryRequest(patient, visit);
            log.info("Calling FastAPI recovery-time with request: {}", request);
            FastApiRecoveryPredictionResponse response = fastApiClient.predictRecovery(request);
            if (response != null && response.getEstimatedRecoveryDays() != null) {
                visit.setPredictedRecoveryDays(response.getEstimatedRecoveryDays());
                if (visit.getIssueDate() != null) {
                    visit.setPredictedRecoveryDate(visit.getIssueDate().plusDays(Math.round(response.getEstimatedRecoveryDays())));
                }
                visit.setRecoveryPredictionStatus("OK");
                visit.setRecoveryPredictionGeneratedAt(LocalDateTime.now());
            }
        } catch (MlServiceException exc) {
            log.warn("Recovery prediction failed for visit {}: {}", visit.getId(), exc.getMessage());
        }
    }

    private FastApiMedicineSuccessRequest buildMedicineSuccessRequest(User patient, Visit currentVisit, Prescription prescription) {
        FastApiMedicineSuccessRequest request = new FastApiMedicineSuccessRequest();
        request.setPatient(buildPatient(patient));
        request.setCurrentTreatment(buildCurrentTreatment(currentVisit, prescription));
        request.setHistory(buildHistory(patient, currentVisit));
        return request;
    }

    private FastApiRecoveryPredictionRequest buildRecoveryRequest(User patient, Visit visit) {
        FastApiRecoveryPredictionRequest request = new FastApiRecoveryPredictionRequest();
        request.setPatient(buildPatient(patient));
        request.setCurrentTreatment(buildCurrentTreatment(visit, visit.getMedicines() == null || visit.getMedicines().isEmpty() ? null : visit.getMedicines().get(0)));
        request.setHistory(buildHistory(patient, visit));
        return request;
    }

    private FastApiMedicineSuccessRequest.FastApiPatient buildPatient(User patient) {
        int age = 0;
        String gender = "UNKNOWN";
        if (patient != null && patient.getUserDetails() != null) {
            UserDetails details = patient.getUserDetails();
            if (details.getDateOfBirth() != null) {
                age = LocalDate.now().getYear() - details.getDateOfBirth().getYear();
            }
            if (details.getGender() != null) {
                gender = normalizeGender(details.getGender());
            }
        }
        return new FastApiMedicineSuccessRequest.FastApiPatient(age, gender);
    }

    private FastApiMedicineSuccessRequest.FastApiCurrentTreatment buildCurrentTreatment(Visit visit, Prescription prescription) {
        String diagnosisName = visit.getDiagnosis() != null ? visit.getDiagnosis().getDiagnosisName() : "Unknown";
        String diagnosisCode = visit.getDiagnosis() != null ? visit.getDiagnosis().getDiagnosisCode() : null;
        String medicineName = prescription != null && prescription.getMedicine() != null && prescription.getMedicine().getMedicineName() != null && !prescription.getMedicine().getMedicineName().isBlank()
            ? prescription.getMedicine().getMedicineName()
            : prescription != null && prescription.getMedicineName() != null && !prescription.getMedicineName().isBlank()
                ? prescription.getMedicineName()
                : "Unknown";
        String activeIngredient = prescription != null && prescription.getMedicine() != null && prescription.getMedicine().getActiveIngredient() != null && !prescription.getMedicine().getActiveIngredient().isBlank()
            ? prescription.getMedicine().getActiveIngredient()
            : prescription != null && prescription.getMedicineName() != null && !prescription.getMedicineName().isBlank()
                ? prescription.getMedicineName()
                : "Unknown";
        return new FastApiMedicineSuccessRequest.FastApiCurrentTreatment(
                diagnosisName,
                diagnosisCode,
                medicineName,
                activeIngredient,
                prescription != null ? prescription.getDosage() : null,
                prescription != null ? prescription.getDuration() : null
        );
    }

    private FastApiMedicineSuccessRequest.FastApiHistory buildHistory(User patient, Visit currentVisit) {
        List<Visit> previousVisits = visitRepository.findByUserId(patient.getId());
        previousVisits = previousVisits.stream()
                .filter(visit -> !visit.getId().equals(currentVisit.getId()))
                .sorted(Comparator.comparing(Visit::getIssueDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        FastApiMedicineSuccessRequest.FastApiHistory history = new FastApiMedicineSuccessRequest.FastApiHistory();
        List<FastApiMedicineSuccessRequest.FastApiPreviousMedicine> previousMedicines = new ArrayList<>();
        List<FastApiMedicineSuccessRequest.FastApiPreviousTreatment> previousTreatments = new ArrayList<>();
        List<FastApiMedicineSuccessRequest.FastApiPreviousVisit> previousVisitsPayload = new ArrayList<>();

        for (Visit previousVisit : previousVisits) {
            if (previousVisit.getDiagnosis() != null) {
                String medicineName = previousVisit.getMedicines() != null && !previousVisit.getMedicines().isEmpty() && previousVisit.getMedicines().get(0).getMedicine() != null
                        ? previousVisit.getMedicines().get(0).getMedicine().getMedicineName()
                        : previousVisit.getMedicines() != null && !previousVisit.getMedicines().isEmpty() ? previousVisit.getMedicines().get(0).getMedicineName() : null;
                previousVisitsPayload.add(new FastApiMedicineSuccessRequest.FastApiPreviousVisit(
                        previousVisit.getDiagnosis().getDiagnosisName(),
                        medicineName,
                        previousVisit.getRecoveredDate() != null && previousVisit.getIssueDate() != null
                                ? (int) (previousVisit.getRecoveredDate().toEpochDay() - previousVisit.getIssueDate().toEpochDay())
                                : 0,
                        mapRecoveryStatusToOutcome(previousVisit.getRecoveryStatus())
                ));
            }
            if (previousVisit.getMedicines() != null) {
                for (Prescription prescription : previousVisit.getMedicines()) {
                    String medicineName = prescription.getMedicine() != null ? prescription.getMedicine().getMedicineName() : prescription.getMedicineName();
                    String activeIngredient = prescription.getMedicine() != null ? prescription.getMedicine().getActiveIngredient() : null;
                    previousMedicines.add(new FastApiMedicineSuccessRequest.FastApiPreviousMedicine(
                            medicineName,
                            activeIngredient,
                            prescription.getDosage(),
                            prescription.getDuration(),
                            normalizeOutcome(prescription.getUserFeedback())
                    ));
                }
            }
            if (previousVisit.getDiagnosis() != null) {
                previousTreatments.add(new FastApiMedicineSuccessRequest.FastApiPreviousTreatment(
                        previousVisit.getDiagnosis().getDiagnosisName(),
                        previousVisit.getRecoveredDate() != null && previousVisit.getIssueDate() != null
                                ? (int) (previousVisit.getRecoveredDate().toEpochDay() - previousVisit.getIssueDate().toEpochDay())
                                : 0,
                        mapRecoveryStatusToOutcome(previousVisit.getRecoveryStatus())
                ));
            }
        }

        history.setPreviousMedicines(previousMedicines);
        history.setPreviousTreatments(previousTreatments);
        history.setPreviousVisits(previousVisitsPayload);
        return history;
    }

    private String normalizeGender(String gender) {
        if (gender == null || gender.isBlank()) {
            return "UNKNOWN";
        }
        String normalized = gender.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "MALE", "M" -> "MALE";
            case "FEMALE", "F" -> "FEMALE";
            case "OTHER", "O" -> "OTHER";
            default -> "UNKNOWN";
        };
    }

    private String normalizeOutcome(String outcome) {
        if (outcome == null || outcome.isBlank()) {
            return "UNKNOWN";
        }
        String normalized = outcome.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("EFFECTIVE") || normalized.contains("SUCCESS") || normalized.contains("IMPROVED")) {
            return "EFFECTIVE";
        }
        if (normalized.contains("INEFFECTIVE") || normalized.contains("FAIL") || normalized.contains("WORSE")) {
            return "INEFFECTIVE";
        }
        if (normalized.contains("RECOVERED")) {
            return "RECOVERED";
        }
        return "UNKNOWN";
    }

    private String mapRecoveryStatusToOutcome(com.capstone.champ.model.RecoveryStatus status) {
        if (status == null) {
            return "UNKNOWN";
        }
        return switch (status) {
            case RECOVERED -> "RECOVERED";
            case UNKNOWN -> "UNKNOWN";
            case ONGOING -> "UNKNOWN";
            case LOST_TO_FOLLOWUP -> "INEFFECTIVE";
        };
    }
}
