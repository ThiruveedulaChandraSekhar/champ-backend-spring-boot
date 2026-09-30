package com.capstone.champ.service;

import com.capstone.champ.exception.AadhaarNotFoundException;
import com.capstone.champ.exception.UserDetailsNotFoundException;
import com.capstone.champ.model.*;
import com.capstone.champ.payload.*;
import com.capstone.champ.payload.doctordetails.DoctorDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsDTO;
import com.capstone.champ.payload.userdetails.UserDetailsRequest;
import com.capstone.champ.payload.userdetails.UserDetailsResponse;
import com.capstone.champ.exception.InvalidInputException;
import com.capstone.champ.exception.MedicineNotFoundException;
import com.capstone.champ.repository.MedicineRepository;
import com.capstone.champ.repository.PrescriptionRepository;
import com.capstone.champ.repository.UserDetailsRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDetailsRepository userDetailsRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PrescriptionRepository prescriptionRepository;
    private final VisitRepository visitRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineSafetyService medicineSafetyService;
    private final PatientHistorySummaryService patientHistorySummaryService;

    @Override
    public UserDetailsResponse addUserDetails(String aadhaarNumber, UserDetailsRequest userDetailsRequest) {
        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new AadhaarNotFoundException(aadhaarNumber));
        requirePatient(user);
        UserDetails userDetails = modelMapper.map(userDetailsRequest, UserDetails.class);
        userDetails.setUser(user);
        user.setUserDetails(userDetails);
        userRepository.save(user);
        return new UserDetailsResponse(true, "User details added successfully");
    }

    @Override
    public UserDetailsResponse updateUserDetails(UserDetailsDTO dto) {

        UserDetails userDetails = userDetailsRepository.findById(dto.getId())
                .orElseThrow(() -> new UserDetailsNotFoundException());
        requirePatient(userDetails.getUser());
        if (dto.getAccountIdentifier() == null || !dto.getAccountIdentifier().equals(userDetails.getUser().getAadhaarNumber()))
            throw new InvalidInputException("Patient account does not own this profile");

        userDetails.setFullName(dto.getFullName());
        userDetails.setGender(dto.getGender());
        userDetails.setEmergencyContact(dto.getEmergencyContact());
        userDetails.setEmail(dto.getEmail());
        userDetails.setGuardian(dto.getGuardian());
        userDetails.setGuardianContact(dto.getGuardianContact());
        userDetails.setDateOfBirth(dto.getDateOfBirth());
        userDetails.setBloodGroup(dto.getBloodGroup());
        User user = userDetails.getUser();
        if (dto.getMobileNumber() != null) user.setMobileNumber(dto.getMobileNumber());

        if (dto.getDoorNumber() != null || dto.getStreet() != null || dto.getCity() != null
                || dto.getState() != null || dto.getPinCode() != null) {
            Address address = userDetails.getAddress();
            if (address == null) address = new Address();
            if (dto.getDoorNumber() != null) address.setDoorNumber(dto.getDoorNumber());
            if (dto.getStreet() != null) address.setStreet(dto.getStreet());
            if (dto.getCity() != null) address.setCity(dto.getCity());
            if (dto.getState() != null) address.setState(dto.getState());
            if (dto.getPinCode() != null) address.setPinCode(dto.getPinCode());
            address.setUserDetails(userDetails);
            userDetails.setAddress(address);
        }

        userDetails.setLastUpdated(LocalDateTime.now());

        userDetailsRepository.save(userDetails);
        userRepository.save(user);

        return new UserDetailsResponse(true, "User details updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsDTO getUserDetails(String aadhaarNumber) {
        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new AadhaarNotFoundException(aadhaarNumber));
        UserDetails userDetails = user.getUserDetails();
        if (userDetails == null) throw new UserDetailsNotFoundException();
        requirePatient(user);
        UserDetailsDTO dto = modelMapper.map(userDetails, UserDetailsDTO.class);
        dto.setAccountIdentifier(user.getAadhaarNumber());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setVerificationStatus(user.getVerificationStatus());
        if (userDetails.getAddress() != null) {
            dto.setDoorNumber(userDetails.getAddress().getDoorNumber()); dto.setStreet(userDetails.getAddress().getStreet());
            dto.setCity(userDetails.getAddress().getCity()); dto.setState(userDetails.getAddress().getState());
            dto.setPinCode(userDetails.getAddress().getPinCode());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public VisitResponse getVisits(String input) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        requirePatient(user);
        List<Visit> visits = visitRepository.findByUserId(user.getId());
        if (!visits.isEmpty()) {
            List<Long> visitIds = visits.stream().map(Visit::getId).toList();
            visitRepository.findWithMedicines(user.getId(), visitIds);
            visitRepository.findWithAllergies(user.getId(), visitIds);
        }
            return toVisitResponse(visits, true);
            }

            @Override
            @Transactional(readOnly = true)
            public VisitResponse getDoctorOwnVisits(String doctorInput, String patientInput) {
            User doctor = userRepository.findByAadhaarNumber(doctorInput)
                .orElseThrow(() -> new AadhaarNotFoundException(doctorInput));
            User patient = userRepository.findByAadhaarNumber(patientInput)
                .orElseThrow(() -> new AadhaarNotFoundException(patientInput));
            if (!"DOCTOR".equals(doctor.getRole())) throw new InvalidInputException("Account is not a doctor");
            requirePatient(patient);
            List<Visit> visits = visitRepository.findByUserIdAndDoctorDetailsUserId(patient.getId(), doctor.getId());
            return toVisitResponse(visits, false);
            }

            private VisitResponse toVisitResponse(List<Visit> visits, boolean includeProtectedDetails) {
        List<VisitDTO> visitsDTO = new ArrayList<>();
        for(Visit visit : visits) {
            VisitDTO temp = new VisitDTO();
            temp.setId(visit.getId());
            temp.setReason(visit.getReason());
            temp.setIssueDate(visit.getIssueDate());
            temp.setRecoveredDate(visit.getRecoveredDate());
            temp.setPredictedRecoveryDays(visit.getPredictedRecoveryDays());
            temp.setPredictedRecoveryDate(visit.getPredictedRecoveryDate());
            temp.setRecoveryPredictionStatus(visit.getRecoveryPredictionStatus());
                List<MedicineDTO> medicineDTO = new ArrayList<>();
                for(Prescription prescription : includeProtectedDetails && visit.getMedicines() != null
                    ? visit.getMedicines() : Collections.<Prescription>emptyList()) {
                MedicineDTO dto = new MedicineDTO();
                dto.setId(prescription.getId());
                dto.setMedicineId(prescription.getMedicine() == null ? prescription.getMedicineId() : prescription.getMedicine().getId());
                dto.setMedicineName(prescription.getMedicine() == null ? prescription.getMedicineName() : prescription.getMedicine().getMedicineName());
                dto.setDosage(prescription.getDosage());
                dto.setIsInjection(prescription.getIsInjection());
                dto.setDuration(prescription.getDuration());
                dto.setTakeMorning(prescription.getTakeMorning());
                dto.setTakeAfternoon(prescription.getTakeAfternoon());
                dto.setTakeEvening(prescription.getTakeEvening());
                dto.setUserFeedback(prescription.getUserFeedback());
                dto.setNote(prescription.getNote());
                medicineDTO.add(dto);
            }
            temp.setMedicines(medicineDTO);
            if (visit.getDiagnosis() != null) {
                temp.setDiagnosisId(visit.getDiagnosis().getId());
                temp.setDiagnosisCode(visit.getDiagnosis().getDiagnosisCode());
                temp.setDiagnosisName(visit.getDiagnosis().getDiagnosisName());
            }
            temp.setRecoveryStatus(visit.getRecoveryStatus());
            temp.setRecoveryConfirmedAt(visit.getRecoveryConfirmedAt());
            temp.setOutcomeSource(visit.getOutcomeSource());
            List<AllergyDTO> allergyDTOS = new ArrayList<>();
                for(Allergy allergy : includeProtectedDetails && visit.getAllergies() != null
                    ? visit.getAllergies() : Collections.<Allergy>emptyList())
                allergyDTOS.add(modelMapper.map(allergy, AllergyDTO.class));
            temp.setAllergies(allergyDTOS);
            if (visit.getDoctorDetails() != null)
                temp.setDoctorDetails(modelMapper.map(visit.getDoctorDetails(), DoctorDetailsDTO.class));
            visitsDTO.add(temp);
        }
        return new VisitResponse(true, "Successfully got visit details", visitsDTO);
    }

    @Override
    @Transactional
    public GeneralResponse updateRecovery(String input, Long visitId, RecoveryUpdateRequest request) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        requirePatient(user);
        Visit visit = visitRepository.findById(visitId)
                .filter(candidate -> candidate.getUser() != null && candidate.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new InvalidInputException("visitId"));
        if (request == null || request.getRecoveryStatus() == null)
            throw new InvalidInputException("recoveryStatus");
        if (visit.getRecoveryStatus() == RecoveryStatus.RECOVERED && request.getRecoveryStatus() != RecoveryStatus.RECOVERED)
            throw new InvalidInputException("Recovered visits cannot be reopened as active treatment");
        visit.setRecoveryStatus(request.getRecoveryStatus());
        visit.setRecoveredDate(request.getRecoveredDate());
        visit.setRecoveryConfirmedAt(LocalDateTime.now());
        visit.setOutcomeSource(request.getOutcomeSource() == null || request.getOutcomeSource().isBlank() ? "PATIENT" : request.getOutcomeSource());
        if (visit.getRecoveryStatus() == RecoveryStatus.RECOVERED && visit.getRecoveredDate() == null)
            visit.setRecoveredDate(java.time.LocalDate.now());
        if (visit.getIssueDate() != null && visit.getRecoveredDate() != null && visit.getRecoveredDate().isBefore(visit.getIssueDate()))
            throw new InvalidInputException("recoveredDate");
        visitRepository.save(visit);
        return new GeneralResponse(true, "Recovery status updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public AllergyResponse getAllergy(String input) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        requirePatient(user);
        List<Allergy> allergies = user.getAllergies() == null ? Collections.emptyList() : user.getAllergies();
        List<AllergyDTO> allergyDTOS = new ArrayList<>();
        for(Allergy allergy : allergies) {
            AllergyDTO allergyDTO = modelMapper.map(allergy, AllergyDTO.class);
            if (allergy.getDoctorDetails() != null)
                allergyDTO.setDoctorDetails(modelMapper.map(allergy.getDoctorDetails(), DoctorDetailsDTO.class));
            allergyDTOS.add(allergyDTO);
        }
        return new AllergyResponse(true, "Successfully got allergy details", allergyDTOS);
    }

    @Override
    public MedicineSafetyCheckResponse checkMedicineSafety(String input, MedicineSafetyRequest request) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        requirePatient(user);

        Medicine medicine = null;
        if (request != null && request.getMedicineId() != null) {
            medicine = medicineRepository.findById(request.getMedicineId())
                    .orElseThrow(() -> new MedicineNotFoundException(request.getMedicineId()));
        } else if (request != null && request.getMedicineName() != null && !request.getMedicineName().isBlank()) {
            medicine = new Medicine();
            medicine.setMedicineName(request.getMedicineName());
            medicine.setActiveIngredient(request.getActiveIngredient());
        }

        return medicineSafetyService.evaluateMedicineSafety(user, medicine, request != null ? request.getMedicineName() : null);
    }

    @Override
    public PatientHistorySummaryResponse getHistorySummary(String input) {
        User user = userRepository.findByAadhaarNumber(input)
                .orElseThrow(() -> new AadhaarNotFoundException(input));
        requirePatient(user);
        return patientHistorySummaryService.buildSummary(user);
    }

    @Override
        @Transactional(readOnly = true)
        public MedicineFeedBackResponse getMedicineFeedback(String aadhaarNumber, String medicineName) {
        if (medicineName == null || medicineName.isBlank())
            throw new InvalidInputException("medicineName");

        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
            .orElseThrow(() -> new AadhaarNotFoundException(aadhaarNumber));
        requirePatient(user);

        Medicine medicine = medicineRepository.findByNormalizedMedicineName(medicineName.trim())
                .orElseThrow(() -> new MedicineNotFoundException(medicineName.trim()));
        List<Prescription> prescriptions = prescriptionRepository.findFeedbackByMedicineAndPatient(
            user.getId(), medicine.getId(), medicine.getMedicineName());

        MedicineFeedBackResponse medicineFeedBackResponse = new MedicineFeedBackResponse();
        medicineFeedBackResponse.setStatus(true);
        medicineFeedBackResponse.setMessage("Medicine details retrieved successfully");
        medicineFeedBackResponse.setFeedbacks(new ArrayList<>());
        for (Prescription prescription : prescriptions) {
            if (prescription.getUserFeedback() != null && !prescription.getUserFeedback().isBlank())
                medicineFeedBackResponse.getFeedbacks().add(new MedicineWithFeedback(
                    medicine.getMedicineName(), prescription.getUserFeedback(), user.getUserDetails() == null
                    ? "Patient" : user.getUserDetails().getFullName()));
        }
        return medicineFeedBackResponse;
    }

            @Override
            @Transactional(readOnly = true)
            public MedicineFeedBackResponse getAllMedicineFeedback(Long medicineId) {
            Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new MedicineNotFoundException(medicineId));
            List<Prescription> prescriptions = prescriptionRepository.findAllFeedbackByMedicine(
                medicine.getId(), medicine.getMedicineName());
            List<MedicineWithFeedback> feedbacks = prescriptions.stream()
                .map(prescription -> new MedicineWithFeedback(
                    prescription.getMedicine() == null ? medicine.getMedicineName() : prescription.getMedicine().getMedicineName(),
                    prescription.getUserFeedback(),
                    prescription.getVisit() == null || prescription.getVisit().getUser() == null
                        || prescription.getVisit().getUser().getUserDetails() == null
                        ? "Patient" : prescription.getVisit().getUser().getUserDetails().getFullName()))
                .toList();
            return new MedicineFeedBackResponse("Medicine feedback retrieved successfully", true, feedbacks);
            }

    @Override
    public MedicineFeedbackSubmitResponse addMedicineFeedback(String aadhaarNumber, MedicineFeedbackRequest request) {
        if (request == null || request.getPrescriptionId() == null || request.getMedicineId() == null
                || request.getFeedback() == null || request.getFeedback().isBlank())
            throw new InvalidInputException("prescriptionId, medicineId and feedback");

        User user = userRepository.findByAadhaarNumber(aadhaarNumber)
                .orElseThrow(() -> new AadhaarNotFoundException(aadhaarNumber));
        requirePatient(user);
        Prescription prescription = prescriptionRepository
                .findByIdAndVisitUserIdAndMedicine_Id(request.getPrescriptionId(), user.getId(), request.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException(request.getMedicineId()));
        prescription.setUserFeedback(request.getFeedback().trim());
        prescriptionRepository.save(prescription);
        String medicineName = prescription.getMedicine() == null ? prescription.getMedicineName() : prescription.getMedicine().getMedicineName();
        return new MedicineFeedbackSubmitResponse(true, "Medicine feedback added successfully", prescription.getId(),
                request.getMedicineId(), medicineName, prescription.getUserFeedback());
    }

    private void requirePatient(User user) {
        if (user == null || !"USER".equals(user.getRole()))
            throw new InvalidInputException("Account is not a patient");
    }
}
