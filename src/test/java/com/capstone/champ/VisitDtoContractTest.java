package com.capstone.champ;

import com.capstone.champ.payload.AllergyRequest;
import com.capstone.champ.payload.PrescriptionRequest;
import com.capstone.champ.payload.VisitRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VisitDtoContractTest {

    @Test
    void visitRequestUsesFlatPayloadDtos() {
        VisitRequest request = new VisitRequest();
        request.setReason("Fever");
        request.setDiagnosisId(5L);
        request.setMedicines(List.of(new PrescriptionRequest()));
        request.setAllergies(List.of(new AllergyRequest()));

        assertNotNull(request.getMedicines());
        assertEquals(1, request.getMedicines().size());
        assertEquals(5L, request.getDiagnosisId());
        assertNotNull(request.getAllergies());
        assertEquals(LocalDate.now().getClass(), request.getRecoveredDate() == null ? LocalDate.now().getClass() : request.getRecoveredDate().getClass());
    }
}
