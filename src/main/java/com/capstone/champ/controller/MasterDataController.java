package com.capstone.champ.controller;

import com.capstone.champ.model.Diagnosis;
import com.capstone.champ.model.Medicine;
import com.capstone.champ.payload.DiagnosisResponse;
import com.capstone.champ.payload.MedicineResponse;
import com.capstone.champ.repository.DiagnosisRepository;
import com.capstone.champ.repository.MedicineRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.capstone.champ.exception.MedicineNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Master Data")
public class MasterDataController {

    private final DiagnosisRepository diagnosisRepository;
    private final MedicineRepository medicineRepository;

    @GetMapping("/diagnoses")
    @Operation(summary = "Get seeded diagnoses", description = "Returns default diagnosis records for doctor selection.")
    public List<DiagnosisResponse> getDiagnoses() {
        return diagnosisRepository.findAll().stream()
                .map(this::toDiagnosisResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/medicines")
    @Operation(summary = "Get seeded medicines", description = "Returns default medicine catalog records for doctor selection.")
    public List<MedicineResponse> getMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::toMedicineResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponse> getMedicine(@PathVariable Long id) {
        Medicine medicine = medicineRepository.findById(id).orElseThrow(() -> new MedicineNotFoundException(id));
        return new ResponseEntity<>(toMedicineResponse(medicine), HttpStatus.OK);
    }

    private DiagnosisResponse toDiagnosisResponse(Diagnosis diagnosis) {
        DiagnosisResponse response = new DiagnosisResponse();
        response.setId(diagnosis.getId());
        response.setName(diagnosis.getDiagnosisName());
        return response;
    }

    private MedicineResponse toMedicineResponse(Medicine medicine) {
        MedicineResponse response = new MedicineResponse();
        response.setId(medicine.getId());
        response.setName(medicine.getMedicineName());
        return response;
    }
}
