package com.capstone.champ.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CHAMP API",
                version = "v1",
                description = "REST API for authentication, patient records, doctor records, and administration."
        ),
        tags = {
                @Tag(name = "Authentication", description = "Account registration and login operations"),
                @Tag(name = "Users", description = "Patient details, visits, allergies, and medicine feedback"),
                @Tag(name = "Doctors", description = "Doctor details and patient visit operations"),
                @Tag(name = "Administration", description = "User and doctor verification operations")
        }
)
public class OpenApiConfig {
}
