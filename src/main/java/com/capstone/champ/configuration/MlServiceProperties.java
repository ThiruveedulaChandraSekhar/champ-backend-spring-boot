package com.capstone.champ.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ml.service")
public class MlServiceProperties {
    private String baseUrl = "http://localhost:8000";
    private String medicineSuccessPath = "/api/v1/predictions/medicine-success";
    private String recoveryPath = "/api/v1/predictions/recovery-time";
    private String safetyPath = "/api/v1/safety/check";
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;
}
