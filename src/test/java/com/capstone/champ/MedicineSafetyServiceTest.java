package com.capstone.champ;

import com.capstone.champ.model.Allergy;
import com.capstone.champ.model.AllergyType;
import com.capstone.champ.model.Medicine;
import com.capstone.champ.model.User;
import com.capstone.champ.payload.MedicineSafetyCheckResponse;
import com.capstone.champ.payload.fastapi.FastApiClient;
import com.capstone.champ.payload.fastapi.FastApiSafetyRequest;
import com.capstone.champ.payload.fastapi.FastApiSafetyResponse;
import com.capstone.champ.service.MedicineSafetyService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MedicineSafetyServiceTest {

    @Test
    void environmentalAllergyIsNotSentAsDrugAllergy() {
        FastApiClient client = modelClient();
        User user = userWithAllergies(allergy("Dust Allergy", AllergyType.ENVIRONMENTAL));

        new MedicineSafetyService(client).evaluateMedicineSafety(user, medicine(), null);

        verify(client).checkSafety(new FastApiSafetyRequest("Amoxicillin", "amoxicillin", List.of()));
    }

    @Test
    void seasonalAllergyIsNotSentAsDrugAllergy() {
        FastApiClient client = modelClient();
        User user = userWithAllergies(allergy("Seasonal Allergy", AllergyType.SEASONAL));

        new MedicineSafetyService(client).evaluateMedicineSafety(user, medicine(), null);

        verify(client).checkSafety(new FastApiSafetyRequest("Amoxicillin", "amoxicillin", List.of()));
    }

    @Test
    void drugAllergyIsSentToModel() {
        FastApiClient client = modelClient();
        User user = userWithAllergies(allergy("Penicillin Allergy", AllergyType.DRUG));

        new MedicineSafetyService(client).evaluateMedicineSafety(user, medicine(), null);

        verify(client).checkSafety(new FastApiSafetyRequest("Amoxicillin", "amoxicillin", List.of("Penicillin Allergy")));
    }

    @Test
    void onlyDrugAllergyIsSentWhenOtherTypesAlsoExist() {
        FastApiClient client = modelClient();
        User user = userWithAllergies(
                allergy("Dust Allergy", AllergyType.ENVIRONMENTAL),
                allergy("Penicillin Allergy", AllergyType.DRUG),
                allergy("Peanut Allergy", AllergyType.FOOD));

        new MedicineSafetyService(client).evaluateMedicineSafety(user, medicine(), null);

        verify(client).checkSafety(new FastApiSafetyRequest("Amoxicillin", "amoxicillin", List.of("Penicillin Allergy")));
    }

    @Test
    void noKnownAllergyIsNotSentToModel() {
        FastApiClient client = modelClient();
        User user = userWithAllergies(allergy("No known allergy", AllergyType.NONE));

        new MedicineSafetyService(client).evaluateMedicineSafety(user, medicine(), null);

        verify(client).checkSafety(new FastApiSafetyRequest("Amoxicillin", "amoxicillin", List.of()));
    }

    @Test
    void safetyResultComesFromFastApiModelResponse() {
        FastApiClient client = mock(FastApiClient.class);
        FastApiSafetyResponse modelResponse = new FastApiSafetyResponse();
        modelResponse.setSafe(false);
        modelResponse.setWarning(true);
        modelResponse.setPrediction("Potential Allergy Risk");
        modelResponse.setProbability(0.91);
        modelResponse.setModel("drug-allergy-model");
        modelResponse.setMessage("Potential drug-allergy conflict.");
        when(client.checkSafety(any())).thenReturn(modelResponse);

        MedicineSafetyCheckResponse response = new MedicineSafetyService(client)
                .evaluateMedicineSafety(userWithAllergies(), medicine(), null);

        assertFalse(response.getSafe());
        assertEquals("Potential Allergy Risk", response.getPrediction());
        assertEquals(0.91, response.getProbability());
        assertEquals("drug-allergy-model", response.getModel());
    }

    @Test
    void missingModelResultRemainsUnknown() {
        FastApiClient client = mock(FastApiClient.class);
        FastApiSafetyResponse modelResponse = new FastApiSafetyResponse();
        modelResponse.setPrediction("UNKNOWN");
        modelResponse.setMessage("Drug-allergy model unavailable.");
        when(client.checkSafety(any())).thenReturn(modelResponse);

        MedicineSafetyCheckResponse response = new MedicineSafetyService(client)
                .evaluateMedicineSafety(userWithAllergies(), medicine(), null);

        assertNull(response.getSafe());
        assertEquals("UNKNOWN", response.getPrediction());
    }

    private static FastApiClient modelClient() {
        FastApiClient client = mock(FastApiClient.class);
        when(client.checkSafety(any())).thenReturn(new FastApiSafetyResponse());
        return client;
    }

    private static Medicine medicine() {
        Medicine medicine = new Medicine();
        medicine.setMedicineName("Amoxicillin");
        medicine.setActiveIngredient("amoxicillin");
        return medicine;
    }

    private static User userWithAllergies(Allergy... allergies) {
        User user = new User();
        user.setAllergies(List.of(allergies));
        return user;
    }

    private static Allergy allergy(String title, AllergyType type) {
        Allergy allergy = new Allergy();
        allergy.setTitle(title);
        allergy.setAllergyType(type);
        return allergy;
    }
}
