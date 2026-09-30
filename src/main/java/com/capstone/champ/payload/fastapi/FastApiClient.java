package com.capstone.champ.payload.fastapi;

import com.capstone.champ.configuration.MlServiceProperties;
import com.capstone.champ.exception.MlServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class FastApiClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestClient mlRestClient;
    private final MlServiceProperties mlServiceProperties;

    public FastApiMedicineSuccessResponse predictMedicineSuccess(FastApiMedicineSuccessRequest request) {
        return post(mlServiceProperties.getMedicineSuccessPath(), request, FastApiMedicineSuccessResponse.class, "Medicine success prediction");
    }

    public FastApiRecoveryPredictionResponse predictRecovery(FastApiRecoveryPredictionRequest request) {
        return post(mlServiceProperties.getRecoveryPath(), request, FastApiRecoveryPredictionResponse.class, "Recovery time prediction");
    }

    public FastApiSafetyResponse checkSafety(FastApiSafetyRequest request) {
        return post(mlServiceProperties.getSafetyPath(), request, FastApiSafetyResponse.class, "Medicine safety check");
    }

    private <T> T post(String path, Object payload, Class<T> responseType, String operation) {
        try {
            String jsonBody = OBJECT_MAPPER.writeValueAsString(payload);
            log.info("Sending {} payload to FastAPI [{}]: {}", operation, path, jsonBody);

            return mlRestClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                        String detail = readBody(res);
                        log.error("FastAPI returned 422 for {} [{}]: {}", operation, path, detail);
                        throw new MlServiceException.MlServiceValidationException(operation + " validation failed: " + detail);
                    })
                    .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                        String detail = readBody(res);
                        log.error("FastAPI returned 5xx for {} [{}]: {}", operation, path, detail);
                        throw new MlServiceException.MlServiceUnavailableException(operation + " failed on the ML service: " + detail);
                    })
                    .body(responseType);
        } catch (JsonProcessingException exc) {
            throw new MlServiceException(operation + " request could not be serialized for the ML service.", exc);
        } catch (ResourceAccessException exc) {
            throw new MlServiceException.MlServiceUnavailableException(operation + " timed out or the service is unavailable.", exc);
        } catch (RestClientException exc) {
            throw new MlServiceException(operation + " request could not be completed.", exc);
        }
    }

    private String readBody(ClientHttpResponse response) {
        try {
            if (response.getBody() == null) {
                return "Validation error";
            }
            return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "Validation error";
        }
    }
}
