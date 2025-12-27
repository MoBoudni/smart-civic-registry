// src/main/java/de/behoerde/smartcivicregistry/config/OpenApiConfig.java
package de.behoerde.smartcivicregistry.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI smartCivicRegistryOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Civic Registry API")
                        .description("""
                    REST API für das Smart Civic Registry System - 
                    Ein behördentaugliches Stammdaten-System zur Verwaltung von Personen, 
                    Organisationen und Anträgen.
                    
                    ## Authentifizierung
                    Diese API verwendet JWT (JSON Web Tokens) für die Authentifizierung.
                    
                    ## Wichtige Endpoints
                    - `/api/v1/auth/**` - Authentifizierung & Registrierung
                    - `/api/v1/persons/**` - Personenverwaltung
                    - `/api/v1/organizations/**` - Organisationsverwaltung
                    - `/api/v1/applications/**` - Antragsverwaltung
                    """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Smart Civic Registry Team")
                                .email("info@behoerde.de"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}