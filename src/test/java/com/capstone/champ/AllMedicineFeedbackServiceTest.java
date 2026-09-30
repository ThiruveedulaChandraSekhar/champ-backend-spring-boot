package com.capstone.champ;

import com.capstone.champ.model.Medicine;
import com.capstone.champ.model.Prescription;
import com.capstone.champ.model.User;
import com.capstone.champ.model.UserDetails;
import com.capstone.champ.model.Visit;
import com.capstone.champ.repository.MedicineRepository;
import com.capstone.champ.repository.PrescriptionRepository;
import com.capstone.champ.repository.UserDetailsRepository;
import com.capstone.champ.repository.UserRepository;
import com.capstone.champ.repository.VisitRepository;
import com.capstone.champ.service.MedicineSafetyService;
import com.capstone.champ.service.PatientHistorySummaryService;
import com.capstone.champ.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AllMedicineFeedbackServiceTest {
    private MedicineRepository medicines;
    private PrescriptionRepository prescriptions;
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        medicines = mock(MedicineRepository.class);
        prescriptions = mock(PrescriptionRepository.class);
        service = new UserServiceImpl(mock(UserDetailsRepository.class), mock(UserRepository.class), mock(ModelMapper.class),
                prescriptions, mock(VisitRepository.class), medicines, mock(MedicineSafetyService.class),
                mock(PatientHistorySummaryService.class));
    }

    @Test
    void returnsEveryFeedbackForMedicineAcrossDifferentPatients() {
        Medicine medicine = new Medicine();
        medicine.setId(8L);
        medicine.setMedicineName("Medicine A");
        when(medicines.findById(8L)).thenReturn(Optional.of(medicine));
        when(prescriptions.findAllFeedbackByMedicine(8L, "Medicine A"))
                .thenReturn(List.of(feedbackFrom("Patient One", "Helped"), feedbackFrom("Patient Two", "No side effects")));

        var response = service.getAllMedicineFeedback(8L);

        assertEquals(2, response.getFeedbacks().size());
        assertEquals("Patient One", response.getFeedbacks().get(0).getUserName());
        assertEquals("Patient Two", response.getFeedbacks().get(1).getUserName());
        verify(prescriptions).findAllFeedbackByMedicine(8L, "Medicine A");
        verify(prescriptions, never()).findFeedbackByMedicineAndPatient(anyLong(), anyLong(), anyString());
    }

    private Prescription feedbackFrom(String patientName, String text) {
        UserDetails details = new UserDetails();
        details.setFullName(patientName);
        User patient = new User();
        patient.setUserDetails(details);
        Visit visit = new Visit();
        visit.setUser(patient);
        Prescription prescription = new Prescription();
        prescription.setVisit(visit);
        prescription.setUserFeedback(text);
        return prescription;
    }
}