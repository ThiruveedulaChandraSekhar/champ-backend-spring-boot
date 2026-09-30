package com.capstone.champ.service;

import com.capstone.champ.model.Allergy;
import com.capstone.champ.model.AllergyType;
import com.capstone.champ.model.Medicine;
import com.capstone.champ.model.User;
import com.capstone.champ.payload.MedicineSafetyCheckResponse;
import com.capstone.champ.payload.fastapi.FastApiClient;
import com.capstone.champ.payload.fastapi.FastApiSafetyRequest;
import com.capstone.champ.payload.fastapi.FastApiSafetyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineSafetyService {
    private final FastApiClient fastApiClient;

    public MedicineSafetyCheckResponse evaluateMedicineSafety(User user, Medicine medicine, String medicineNameOverride) {
        String medicineName = medicineNameOverride;
        if (medicine != null) {
            medicineName = medicineName != null ? medicineName : medicine.getMedicineName();
        }

        if (medicine == null && (medicineName == null || medicineName.isBlank())) {
            return new MedicineSafetyCheckResponse(null, false, medicineName, new ArrayList<>(), "Medicine information is required for a model safety prediction.");
        }

        String activeIngredient = medicine == null ? null : medicine.getActiveIngredient();
        List<String> patientDrugAllergies = user == null || user.getAllergies() == null
                ? new ArrayList<>()
                : user.getAllergies().stream()
                .filter(Objects::nonNull)
                .filter(allergy -> allergy.getAllergyType() == AllergyType.DRUG)
                .map(Allergy::getTitle)
                .filter(title -> title != null && !title.isBlank())
                .collect(Collectors.toList());

        FastApiSafetyRequest request = new FastApiSafetyRequest(medicineName, activeIngredient, patientDrugAllergies);
        log.info("Medicine safety check: medicine={}, activeIngredient={}, drugAllergyCount={}, allergyTypesSelected=[DRUG]",
                medicineName, activeIngredient, patientDrugAllergies.size());

        FastApiSafetyResponse modelResponse = fastApiClient.checkSafety(request);
        log.info("Medicine safety model response: medicine={}, modelPrediction={}, probability={}",
                medicineName, modelResponse.getPrediction(), modelResponse.getProbability());

        List<MedicineSafetyCheckResponse.MatchedAllergy> matches = modelResponse.getAlerts() == null
                ? new ArrayList<>()
                : modelResponse.getAlerts().stream()
                .map(alert -> new MedicineSafetyCheckResponse.MatchedAllergy(
                        alert.getAllergy(), "UNKNOWN", "ml_model"))
                .collect(Collectors.toList());

        MedicineSafetyCheckResponse response = new MedicineSafetyCheckResponse(
                modelResponse.getSafe(),
                modelResponse.getWarning(),
                medicineName,
                matches,
                modelResponse.getMessage());
        response.setPrediction(modelResponse.getPrediction());
        response.setProbability(modelResponse.getProbability());
        response.setModel(modelResponse.getModel());
        return response;
    }
}
