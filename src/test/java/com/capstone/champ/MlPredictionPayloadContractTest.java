package com.capstone.champ;

import com.capstone.champ.model.Diagnosis;
import com.capstone.champ.model.Medicine;
import com.capstone.champ.model.Prescription;
import com.capstone.champ.model.Visit;
import com.capstone.champ.payload.fastapi.FastApiMedicineSuccessRequest;
import com.capstone.champ.service.MlPredictionService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MlPredictionPayloadContractTest {

    @Test
    void currentTreatmentRequiredFieldsAreNeverNullOrBlank() throws Exception {
        MlPredictionService service = new MlPredictionService(null, null);

        Visit visit = new Visit();
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setDiagnosisName("Fever");
        diagnosis.setDiagnosisCode("R50.9");
        visit.setDiagnosis(diagnosis);

        Prescription prescription = new Prescription();
        prescription.setMedicineName("Amoxicillin");
        prescription.setDosage("500 mg");
        prescription.setDuration(5);

        Medicine medicine = new Medicine();
        medicine.setMedicineName("Amoxicillin");
        medicine.setActiveIngredient(null);
        prescription.setMedicine(medicine);

        Method method = MlPredictionService.class.getDeclaredMethod("buildCurrentTreatment", Visit.class, Prescription.class);
        method.setAccessible(true);

        FastApiMedicineSuccessRequest.FastApiCurrentTreatment treatment =
                (FastApiMedicineSuccessRequest.FastApiCurrentTreatment) method.invoke(service, visit, prescription);

        assertNotNull(treatment.getDiagnosis());
        assertFalse(treatment.getDiagnosis().isBlank());
        assertNotNull(treatment.getMedicine());
        assertFalse(treatment.getMedicine().isBlank());
        assertNotNull(treatment.getActiveIngredient());
        assertFalse(treatment.getActiveIngredient().isBlank());
    }
}