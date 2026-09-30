package com.capstone.champ.service;

import com.capstone.champ.exception.DoctorDetailsNotFoundException;
import com.capstone.champ.exception.InvalidInputException;
import com.capstone.champ.model.Allergy;
import com.capstone.champ.model.AllergyType;
import com.capstone.champ.model.DoctorDetails;
import com.capstone.champ.model.Medicine;
import com.capstone.champ.model.Prescription;
import com.capstone.champ.model.RecoveryStatus;
import com.capstone.champ.model.User;
import com.capstone.champ.model.Visit;
import com.capstone.champ.payload.AllergyRequest;
import com.capstone.champ.payload.GeneralResponse;
import com.capstone.champ.payload.MedicineSafetyCheckResponse;
import com.capstone.champ.payload.PatientHistorySummaryResponse;
import com.capstone.champ.payload.PrescriptionRequest;
import com.capstone.champ.payload.VisitRequest;
import com.capstone.champ.payload.PatientDirectoryDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.doctordetails.DoctorDetailsRequest;
import com.capstone.champ.payload.doctordetails.DoctorDetailsResponse;
import com.capstone.champ.repository.DoctorDetailsRepository;
import com.capstone.champ.repository.DiagnosisRepository;
import com.capstone.champ.repository.MedicineRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService{

    private final DoctorDetailsRepository doctorDetailsRepository;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineSafetyService medicineSafetyService;
    private final MlPredictionService mlPredictionService;
    private final PatientHistorySummaryService patientHistorySummaryService;
    private final PatientAccessService patientAccessService;

    @Override
    public DoctorDetailsResponse addDoctorDetails(DoctorDetailsRequest doctorDetailsRequest, String input) {
        User doctor = authenticationService.getUser(input);
        requireDoctorAccount(doctor);
        DoctorDetails doctorDetails = modelMapper.map(doctorDetailsRequest, DoctorDetails.class);
        doctorDetails.setUser(doctor);
        doctor.setDoctorDetails(doctorDetails);
        userRepository.save(doctor);
        return new DoctorDetailsResponse(true, "Added details of the doctor");
    }

    @Override
    public DoctorDetailsResponse updateDoctorDetails(DoctorDetailsDTO doctorDetailsDTO) {

        DoctorDetails doctorDetails = doctorDetailsRepository.findById(doctorDetailsDTO.getId())
                .orElseThrow(DoctorDetailsNotFoundException::new);
        if (doctorDetailsDTO.getAccountIdentifier() == null || doctorDetails.getUser() == null
                || !doctorDetailsDTO.getAccountIdentifier().equals(doctorDetails.getUser().getAadhaarNumber()))
            throw new InvalidInputException("Doctor account does not own this profile");
        requireDoctor(doctorDetails.getUser());
        modelMapper.map(doctorDetailsDTO, doctorDetails);
        doctorDetailsRepository.save(doctorDetails);
        return new DoctorDetailsResponse(true, "Doctor details updated successfully");
    }

    @Override
    public DoctorDetailsDTO getDoctorDetails(String input) {
        User doctor = authenticationService.getUser(input);
        requireDoctor(doctor);
        if (doctor.getDoctorDetails() == null)
            throw new DoctorDetailsNotFoundException();
        DoctorDetailsDTO details = modelMapper.map(doctor.getDoctorDetails(), DoctorDetailsDTO.class);
        details.setAccountIdentifier(doctor.getAadhaarNumber());
        return details;
    }

    @Override
    public GeneralResponse addVisit(String doctor, String patient, VisitRequest visitRequest) {

        User doctorUser = authenticationService.getUser(doctor);
        User patientUser = authenticationService.getUser(patient);
        requireDoctor(doctorUser);
        if (!"USER".equals(patientUser.getRole())) throw new InvalidInputException("Account is not a patient");
        if (!patientAccessService.hasVerifiedAccess(doctor, patient)) throw new InvalidInputException("Verified patient access is required");

        Visit visit = new Visit();
        visit.setReason(visitRequest.getReason());
        visit.setRecoveredDate(visitRequest.getRecoveredDate());
        visit.setRecoveryStatus(visitRequest.getRecoveryStatus() == null ? RecoveryStatus.UNKNOWN : visitRequest.getRecoveryStatus());
        visit.setRecoveryConfirmedAt(visitRequest.getRecoveryConfirmedAt());
        visit.setOutcomeSource(visitRequest.getOutcomeSource());
        visit.setDoctorDetails(doctorUser.getDoctorDetails());
        visit.setIssueDate(LocalDate.now());
        visit.setUser(patientUser);

        if (visitRequest.getDiagnosisId() != null) {
            visit.setDiagnosis(diagnosisRepository.findById(visitRequest.getDiagnosisId())
                    .orElseThrow(() -> new InvalidInputException("diagnosisId")));
        }

        if (visitRequest.getMedicines() != null) {
            List<Prescription> prescriptions = new ArrayList<>();
            for (PrescriptionRequest prescriptionRequest : visitRequest.getMedicines()) {
                Medicine medicine = null;
                if (prescriptionRequest.getMedicineId() != null) {
                    medicine = medicineRepository.findById(prescriptionRequest.getMedicineId())
                            .orElseThrow(() -> new InvalidInputException("medicineId"));
                    MedicineSafetyCheckResponse safetyCheck = medicineSafetyService.evaluateMedicineSafety(patientUser, medicine, medicine.getMedicineName());
                    if (Boolean.FALSE.equals(safetyCheck.getSafe())) {
                        throw new InvalidInputException(safetyCheck.getMessage() == null ? "Medicine allergy conflict detected." : safetyCheck.getMessage());
                    }
                }

                Prescription prescription = new Prescription();
                prescription.setDosage(prescriptionRequest.getDosage());
                prescription.setIsInjection(prescriptionRequest.getIsInjection());
                prescription.setDuration(prescriptionRequest.getDuration());
                prescription.setTakeMorning(prescriptionRequest.getTakeMorning());
                prescription.setTakeAfternoon(prescriptionRequest.getTakeAfternoon());
                prescription.setTakeEvening(prescriptionRequest.getTakeEvening());
                prescription.setUserFeedback(prescriptionRequest.getUserFeedback());
                prescription.setNote(prescriptionRequest.getNote());
                prescription.setVisit(visit);
                prescription.setMedicine(medicine);
                prescriptions.add(prescription);
            }
            visit.setMedicines(prescriptions);
        }

        if (visitRequest.getAllergies() != null) {
            List<Allergy> allergies = new ArrayList<>();
            for (AllergyRequest allergyRequest : visitRequest.getAllergies()) {
                Allergy allergy = new Allergy();
                allergy.setTitle(allergyRequest.getTitle());
                allergy.setDescription(allergyRequest.getDescription());
                allergy.setDate(allergyRequest.getDate());
                allergy.setSeverity(allergyRequest.getSeverity());
                allergy.setAllergyType(AllergyType.resolve(allergyRequest.getTitle(), allergyRequest.getAllergyType()));
                allergy.setVisit(visit);
                allergy.setUser(patientUser);
                allergy.setDoctorDetails(doctorUser.getDoctorDetails());
                allergies.add(allergy);
            }
            visit.setAllergies(allergies);
        }

        validateRecovery(visit);
        visitRepository.save(visit);
        mlPredictionService.applyPredictions(visit, patientUser);
        visitRepository.save(visit);

        return new GeneralResponse(true, "Visit added successfully");
    }

    @Override
    public PatientHistorySummaryResponse getPatientHistorySummary(String doctorInput, String patientInput) {
        User doctor = authenticationService.getUser(doctorInput);
        User patient = authenticationService.getUser(patientInput);
        requireDoctor(doctor);
        if (!patientAccessService.hasVerifiedAccess(doctorInput, patientInput)) throw new InvalidInputException("Verified patient access is required");
        return patientHistorySummaryService.buildSummary(patient);
    }

    @Override
    public List<PatientDirectoryDTO> searchPatients(String doctorInput, String query) {
        requireDoctor(authenticationService.getUser(doctorInput));
        String term = query == null ? "" : query.trim().toLowerCase();
        return userRepository.findAll().stream().filter(user -> "USER".equals(user.getRole()))
                .filter(user -> user.getUserDetails() != null)
                .filter(user -> term.isEmpty() || (user.getUserDetails().getFullName() != null && user.getUserDetails().getFullName().toLowerCase().contains(term))
                        || (user.getAadhaarNumber() != null && user.getAadhaarNumber().contains(term))
                        || (user.getMobileNumber() != null && user.getMobileNumber().contains(term)))
                .map(user -> new PatientDirectoryDTO(user.getAadhaarNumber(), user.getUserDetails().getFullName(), user.getMobileNumber())).toList();
    }

    private void requireDoctor(User user) {
        requireDoctorAccount(user);
        if (user.getDoctorDetails() == null) throw new InvalidInputException("Doctor details are not available");
    }

    private void requireDoctorAccount(User user) {
        if (user == null || !"DOCTOR".equals(user.getRole())) throw new InvalidInputException("Account is not a doctor");
    }

    private void validateRecovery(Visit visit) {
        if (visit.getRecoveryStatus() == null)
            visit.setRecoveryStatus(RecoveryStatus.UNKNOWN);
        if (visit.getIssueDate() != null && visit.getRecoveredDate() != null
                && visit.getRecoveredDate().isBefore(visit.getIssueDate()))
            throw new InvalidInputException("recoveredDate");
        if (visit.getRecoveryStatus() == RecoveryStatus.RECOVERED && visit.getRecoveredDate() == null)
            throw new InvalidInputException("recoveryStatus");
    }




}
