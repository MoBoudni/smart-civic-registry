package de.behoerde.smartcivicregistry.person.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object zur Kapselung von Adressinformationen im Smart Civic Registry.
 * <p>
 * Diese {@link Embeddable} Klasse wird in andere Entities eingebettet (z.B. {@link Person})
 * und enthält alle relevanten Felder für eine Adresse. Als Value Object besitzt sie keine eigene
 * Identität und wird ausschließlich über ihre Attributwerte verglichen.
 * </p>
 *
 * <h2>Charakteristika:</h2>
 * <ul>
 *   <li>Keine eigene Datenbanktabelle, sondern Einbettung in Parent-Entity</li>
 *   <li>Gleichheit basiert auf Attributwerten</li>
 *   <li>Kein eigener Lifecycle oder Primärschlüssel</li>
 *   <li>Kapselt zusammengehörige Adressinformationen</li>
 * </ul>
 *
 * <h2>Verwendung:</h2>
 * <pre>{@code
 * @Embedded
 * private Address homeAddress;
 *
 * Address address = Address.builder()
 *     .street("Musterstraße")
 *     .houseNumber("123")
 *     .postalCode("12345")
 *     .city("Musterstadt")
 *     .country("Deutschland")
 *     .build();
 * }</pre>
 *
 * <h2>Anwendungsfälle:</h2>
 * <ul>
 *   <li>Wohnadresse von Personen</li>
 *   <li>Geschäftsadressen von Organisationen</li>
 *   <li>Lieferadressen für Dokumente</li>
 *   <li>Postanschrift für Behördenkommunikation</li>
 * </ul>
 *
 * <p>
 * <b>Hinweis:</b> Aktuell ist diese Klasse package-private, da die Adressfelder direkt in der
 * {@link Person}-Entity verwaltet werden. Bei Refaktorierung zur Nutzung dieses Value Objects
 * sollte die Sichtbarkeit auf <code>public</code> geändert werden.
 * </p>
 *
 * @author Smart Civic Registry Team
 * @version 1.0
 * @since Phase 2 (Person Domain Module)
 * @see Person
 * @see jakarta.persistence.Embeddable
 * @see jakarta.persistence.Embedded
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Address {

    /**
     * Straßenname der Adresse.
     * <p>
     * Enthält den vollständigen Straßennamen ohne Hausnummer.
     * Optional, da nicht alle Adressen weltweit Straßennamen verwenden.
     * </p>
     * Beispiele: "Musterstraße", "Hauptstraße", "Am Marktplatz"
     */
    @Column(name = "street", length = 255)
    private String street;

    /**
     * Hausnummer inklusive optionaler Zusätze.
     * <p>
     * Kann numerisch oder alphanumerisch sein, inkl. Zusätze wie Buchstaben oder Bereiche.
     * </p>
     * Beispiele: "123", "45a", "67-69", "12/4"
     */
    @Column(name = "house_number", length = 20)
    private String houseNumber;

    /**
     * Postleitzahl (PLZ) der Adresse.
     * <p>
     * Internationales Format möglich, z.B. "12345" (DE), "SW1A 1AA" (UK), "75001" (FR).
     * </p>
     */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Stadt oder Gemeinde der Adresse.
     * <p>
     * Name der Stadt, Gemeinde oder des Ortes.
     * </p>
     * Beispiele: "Berlin", "Musterstadt", "Frankfurt am Main"
     */
    @Column(name = "city", length = 100)
    private String city;

    /**
     * Land der Adresse.
     * <p>
     * Kann als ISO-Code ("DE", "AT") oder ausgeschrieben ("Deutschland", "Austria") gespeichert werden.
     * Für internationale Anwendungen wird die Verwendung von ISO-Codes empfohlen.
     * </p>
     */
    @Column(name = "country", length = 100)
    private String country;

    // ==================== DOMAIN LOGIC (optional) ====================
    // Aktuell keine Geschäftslogik implementiert.
    // Mögliche Erweiterungen:
    // - getFullAddress(): Gibt formatierte Adresse zurück
    // - isGermanAddress(): Prüft, ob Adresse in Deutschland liegt
    // - validatePostalCode(): Validiert PLZ je nach Land
    // - equals() und hashCode(): Werden von Lombok generiert (basierend auf allen Feldern)
}
