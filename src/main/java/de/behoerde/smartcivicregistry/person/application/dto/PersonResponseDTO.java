package de.behoerde.smartcivicregistry.person.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.behoerde.smartcivicregistry.person.domain.model.Gender;
import de.behoerde.smartcivicregistry.person.domain.model.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO für ausgehende Person-Daten (API Response).
 * Enthält zusätzlich berechnete Felder und Audit-Informationen.
 *
 * @version 1.0
 * @since 2025-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO für die Ausgabe von Person-Daten")
public class PersonResponseDTO {

    // ==================== IDENTIFIKATION ====================

    @Schema(description = "Eindeutige ID der Person", example = "1")
    private Long id;

    // ==================== NAMENSDATEN ====================

    @Schema(description = "Akademischer Titel", example = "Dr.")
    private String title;

    @Schema(description = "Vorname", example = "Max")
    private String firstName;

    @Schema(description = "Zweitname", example = "Johann")
    private String middleName;

    @Schema(description = "Nachname", example = "Mustermann")
    private String lastName;

    @Schema(description = "Geburtsname", example = "Schmidt")
    private String maidenName;

    @Schema(description = "Vollständiger Name", example = "Dr. Max Johann Mustermann")
    private String fullName;

    @Schema(description = "Offizieller Name (Nachname, Vorname)", example = "Mustermann, Max")
    private String officialName;

    // ==================== PERSONENDATEN ====================

    @Schema(description = "Geburtsdatum", example = "1980-01-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Schema(description = "Alter in Jahren", example = "44")
    private Integer age;

    @Schema(description = "Ist volljährig?", example = "true")
    private Boolean isAdult;

    @Schema(description = "Ist Senior?", example = "false")
    private Boolean isSenior;

    @Schema(description = "Geschlecht", example = "MALE")
    private Gender gender;

    @Schema(description = "Staatsangehörigkeit", example = "Deutsch")
    private String citizenship;

    // ==================== ADRESSDATEN ====================

    @Schema(description = "Straße", example = "Musterstraße")
    private String street;

    @Schema(description = "Hausnummer", example = "123")
    private String houseNumber;

    @Schema(description = "Postleitzahl", example = "12345")
    private String postalCode;

    @Schema(description = "Stadt", example = "Musterstadt")
    private String city;

    @Schema(description = "Land", example = "Deutschland")
    private String country;

    @Schema(description = "Vollständige Adresse", example = "Musterstraße 123, 12345 Musterstadt, Deutschland")
    private String fullAddress;

    @Schema(description = "Ist deutsche Adresse?", example = "true")
    private Boolean isGermanAddress;

    // ==================== KONTAKTDATEN ====================

    @Schema(description = "E-Mail-Adresse", example = "max.mustermann@example.de")
    private String email;

    @Schema(description = "Festnetznummer", example = "030 12345678")
    private String phone;

    @Schema(description = "Mobilfunknummer", example = "0176 12345678")
    private String mobilePhone;

    // ==================== WEITERE DATEN ====================

    @Schema(description = "Familienstand", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Geburtsort", example = "Berlin")
    private String birthPlace;

    @Schema(description = "Personalausweisnummer", example = "T22000129")
    private String nationalIdNumber;

    @Schema(description = "Steuer-ID", example = "12345678901")
    private String taxId;

    // ==================== AUDIT INFORMATIONEN ====================

    @Schema(description = "Erstellungszeitpunkt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Letzte Aktualisierung")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "Erstellt von")
    private String createdBy;

    @Schema(description = "Aktualisiert von")
    private String updatedBy;

    @Schema(description = "Gelöscht?", example = "false")
    private Boolean deleted;

    // ==================== LINKS (HATEOAS) ====================

    @Schema(description = "Selbst-Referenz (HATEOAS)")
    private String selfLink;

    @Schema(description = "Update-Link (HATEOAS)")
    private String updateLink;

    @Schema(description = "Delete-Link (HATEOAS)")
    private String deleteLink;
}