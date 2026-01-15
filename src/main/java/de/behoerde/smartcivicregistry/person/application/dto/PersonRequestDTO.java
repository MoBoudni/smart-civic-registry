package de.behoerde.smartcivicregistry.person.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.behoerde.smartcivicregistry.person.domain.model.Gender;
import de.behoerde.smartcivicregistry.person.domain.model.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

/**
 * DTO für eingehende Person-Daten (API Request).
 * Enthält alle Validierungsannotationen für die API-Eingabe.
 *
 * @version 1.0
 * @since 2025-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO für die Erstellung oder Aktualisierung einer Person")
public class PersonRequestDTO {

    // ==================== NAMENSDATEN ====================

    @Schema(description = "Akademischer Titel (optional, max. 50 Zeichen)", example = "Dr.", maxLength = 50)
    @Size(max = 50, message = "Titel darf maximal 50 Zeichen lang sein")
    private String title;

    @Schema(description = "Vorname (pflichtfeld)", example = "Max", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Vorname ist erforderlich")
    @Size(min = 1, max = 100, message = "Vorname muss zwischen 1 und 100 Zeichen lang sein")
    private String firstName;

    @Schema(description = "Zweitname (optional)", example = "Johann", maxLength = 100)
    @Size(max = 100, message = "Zweitname darf maximal 100 Zeichen lang sein")
    private String middleName;

    @Schema(description = "Nachname (pflichtfeld)", example = "Mustermann", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nachname ist erforderlich")
    @Size(min = 1, max = 100, message = "Nachname muss zwischen 1 und 100 Zeichen lang sein")
    private String lastName;

    @Schema(description = "Geburtsname (optional)", example = "Schmidt", maxLength = 100)
    @Size(max = 100, message = "Geburtsname darf maximal 100 Zeichen lang sein")
    private String maidenName;

    // ==================== PERSONENDATEN ====================

    @Schema(description = "Geburtsdatum (pflichtfeld)", example = "1980-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Geburtsdatum ist erforderlich")
    @Past(message = "Geburtsdatum muss in der Vergangenheit liegen")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Schema(description = "Geschlecht", example = "MALE")
    private Gender gender;

    @Schema(description = "Staatsangehörigkeit (optional)", example = "Deutsch", maxLength = 100)
    @Size(max = 100, message = "Staatsangehörigkeit darf maximal 100 Zeichen lang sein")
    private String citizenship;

    // ==================== ADRESSDATEN ====================

    @Schema(description = "Straße (optional)", example = "Musterstraße", maxLength = 255)
    @Size(max = 255, message = "Straße darf maximal 255 Zeichen lang sein")
    private String street;

    @Schema(description = "Hausnummer (optional)", example = "123", maxLength = 20)
    @Size(max = 20, message = "Hausnummer darf maximal 20 Zeichen lang sein")
    private String houseNumber;

    @Schema(description = "Postleitzahl (optional)", example = "12345", maxLength = 20)
    @Size(max = 20, message = "Postleitzahl darf maximal 20 Zeichen lang sein")
    private String postalCode;

    @Schema(description = "Stadt (optional)", example = "Musterstadt", maxLength = 100)
    @Size(max = 100, message = "Stadt darf maximal 100 Zeichen lang sein")
    private String city;

    @Schema(description = "Land (optional)", example = "Deutschland", maxLength = 100)
    @Size(max = 100, message = "Land darf maximal 100 Zeichen lang sein")
    private String country;

    // ==================== KONTAKTDATEN ====================

    @Schema(description = "E-Mail-Adresse (optional, eindeutig)", example = "max.mustermann@example.de", maxLength = 255)
    @Email(message = "Muss eine gültige E-Mail-Adresse sein")
    @Size(max = 255, message = "E-Mail darf maximal 255 Zeichen lang sein")
    private String email;

    @Schema(description = "Festnetznummer (optional)", example = "030 12345678", maxLength = 50)
    @Size(max = 50, message = "Telefonnummer darf maximal 50 Zeichen lang sein")
    @Pattern(regexp = "^[\\d\\s\\+\\-\\(\\)]*$", message = "Ungültiges Telefonnummernformat")
    private String phone;

    @Schema(description = "Mobilfunknummer (optional)", example = "0176 12345678", maxLength = 50)
    @Size(max = 50, message = "Mobilnummer darf maximal 50 Zeichen lang sein")
    @Pattern(regexp = "^[\\d\\s\\+\\-\\(\\)]*$", message = "Ungültiges Mobilnummernformat")
    private String mobilePhone;

    // ==================== WEITERE DATEN ====================

    @Schema(description = "Familienstand", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Geburtsort (optional)", example = "Berlin", maxLength = 100)
    @Size(max = 100, message = "Geburtsort darf maximal 100 Zeichen lang sein")
    private String birthPlace;

    @Schema(description = "Personalausweisnummer (optional, eindeutig)", example = "T22000129", maxLength = 50)
    @Size(max = 50, message = "Personalausweisnummer darf maximal 50 Zeichen lang sein")
    @Pattern(regexp = "^[A-Z0-9]*$", message = "Personalausweisnummer darf nur Großbuchstaben und Zahlen enthalten")
    private String nationalIdNumber;

    @Schema(description = "Steuer-ID (optional, eindeutig)", example = "12345678901", maxLength = 50)
    @Size(max = 50, message = "Steuer-ID darf maximal 50 Zeichen lang sein")
    @Pattern(regexp = "^[0-9]*$", message = "Steuer-ID darf nur Zahlen enthalten")
    private String taxId;

    // ==================== VALIDIERUNGSMETHODEN ====================

    /**
     * Überprüft ob die Person volljährig ist.
     * Business-Logik Validierung.
     */
    public boolean isAdult() {
        if (dateOfBirth == null) return false;
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
    }

    /**
     * Validiert Geschäftsregeln.
     * @throws IllegalArgumentException wenn Business-Regeln verletzt werden
     */
    public void validateBusinessRules() {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Geburtsdatum darf nicht in der Zukunft liegen");
        }

        // Weitere Business-Regeln hier...
    }
}