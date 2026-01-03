package de.behoerde.smartcivicregistry.person.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Wertobjekt für Adresse
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Address {
    private String street;
    private String houseNumber;
    private String postalCode;
    private String city;
    private String country;
}
