package de.behoerde.smartcivicregistry.person.domain.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für die {@link Person}-Klasse des Smart Civic Registry Systems.

 * Diese Testklasse verifiziert die Kernfunktionalität der {@link Person}-Entity,
 * insbesondere die Geschäftslogik für Altersberechnung, Namensformatierung
 * und Adressverwaltung. Die Tests folgen dem Arrange-Act-Assert (AAA) Muster
 * und verwenden AssertJ für lesbare Assertions.

 * Teststrategie:

 *   Isolierte Tests: Jeder Test prüft eine spezifische Funktion
 *   Vollständige Fixture-Initialisierung: Alle Pflichtfelder werden gesetzt
 *   Aussagekräftige Assertions: Lesbare Fehlermeldungen durch AssertJ
 *   Grenzwertanalyse: Tests für Erwachsene und Senioren

 * Anforderungen an Testdaten:

 *   firstName: Pflichtfeld, muss immer gesetzt sein
 *   lastName: Pflichtfeld, muss immer gesetzt sein
 *   dateOfBirth: Pflichtfeld für Altersberechnung
 *   Optionale Felder (Titel, Adresse) werden bedarfsweise ergänzt
 *
 * @author Smart Civic Registry Team
 * @version 1.1
 * @since Phase 2 (Person Domain Module)
 * @see Person
 */
class PersonTest {

    /**
     * Testet die grundlegende Erstellung einer {@link Person}-Instanz.

     * Dieser Test verifiziert, dass eine {@link Person}-Instanz erfolgreich
     * erstellt werden kann und die wesentlichen Attribute korrekt gesetzt sind.
     * Zusätzlich werden die berechneten Eigenschaften (Volljährigkeit, Seniorenstatus)
     * überprüft.

     * Geprüfte Aspekte:

     *   Objekterstellung liefert eine gültige Instanz (nicht {@code null})
     *   vollständiger Name wird korrekt formatiert
     *   E-Mail-Adresse ist korrekt gesetzt
     *   Altersbezogene Eigenschaften werden korrekt berechnet
     *
     * @since 1.0
     */
    @Test
    void testPersonCreation() {
        // Arrange - Testperson wird über Factory-Methode erstellt
        Person person = Person.createTestPerson();

        // Assert - Überprüfung aller erwarteten Eigenschaften
        assertThat(person).isNotNull();
        assertThat(person.getFullName()).isEqualTo("Max Mustermann");
        assertThat(person.getEmail()).isEqualTo("max.mustermann@example.de");
        assertThat(person.isAdult()).isTrue();
        assertThat(person.isSenior()).isFalse();
    }

    /**
     * Testet die korrekte Berechnung des Alters einer Person.

     * Dieser Test verifiziert die {@link Person#calculateAge()}-Methode
     * anhand einer Person mit exakt definiertem Geburtsdatum. Es wird
     * geprüft, dass das berechnete Alter dem erwarteten Wert entspricht
     * und die altersbezogenen Status korrekt gesetzt werden.

     * Testaufbau:

     *   Person wird mit {@code LocalDate.now().minusYears(25)} erstellt
     *   Erwartetes Alter: 25 Jahre
     *   Status "Erwachsen" muss {@code true} sein
     *   Status "Senior" muss {@code false} sein

     * Wichtig: Alle Pflichtfelder (firstName, lastName, dateOfBirth)
     * müssen vor dem Aufruf von {@code build()} gesetzt sein.
     *
     * @since 1.0
     * @see Person#calculateAge()
     * @see Person#isAdult()
     * @see Person#isSenior()
     */
    @Test
    void testAgeCalculation() {
        // Arrange - Person wird mit bekanntem Geburtsdatum erstellt
        Person person = Person.builder()
                .firstName("Test")
                .lastName("Person")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .build();

        // Act - Alter wird berechnet
        int age = person.calculateAge();

        // Assert - Ergebnisse werden verifiziert
        assertThat(age).isEqualTo(25);
        assertThat(person.isAdult()).isTrue();
        assertThat(person.isSenior()).isFalse();
    }

    /**
     * Testet den Seniorenstatus einer älteren Person.

     * Dieser Test verifiziert, dass Personen ab einem bestimmten Alter
     * (konfigurierbar, typischerweise 65+ Jahre) korrekt als Senioren
     * erkannt werden. Dies ist relevant für behördliche Dienste,
     * die altersspezifische Leistungen anbieten.

     * Grenzwert:

     * Eine Person mit 70 Jahren gilt als Senior und kann entsprechende
     * Dienste und Ermäßigungen in Anspruch nehmen.
     *
     * @since 1.0
     * @see Person#isSenior()
     */
    @Test
    void testSeniorPerson() {
        // Arrange - Senioren-Person wird erstellt
        Person person = Person.builder()
                .firstName("Senior")
                .lastName("Citizen")
                .dateOfBirth(LocalDate.now().minusYears(70))
                .build();

        // Assert - Seniorenstatus wird verifiziert
        assertThat(person.isSenior()).isTrue();
    }

    /**
     * Testet die Adressfunktionalität einer {@link Person}.

     * Dieser Test verifiziert die korrekte Formatierung der vollständigen
     * Adresse sowie die Ländererkennung für deutsche Adressen. Die Methode
     * {@link Person#getFullAddress()} kombiniert alle Adressbestandteile
     * zu einer lesbaren Zeichenkette.

     * Geprüfte Adressbestandteile:

     *   Straße und Hausnummer
     *   Postleitzahl und Ort
     *   Land

     * Erwartetes Format:
     * Straße Hausnummer, PLZ Ort, Land

     * Wichtig: Alle Pflichtfelder (firstName, lastName, dateOfBirth)
     * müssen vor den optionalen Adressfeldern gesetzt werden.
     *
     * @since 1.0
     * @see Person#getFullAddress()
     * @see Person#isGermanAddress()
     */
    @Test
    void testAddressFunctionality() {
        // Arrange - Person wird mit vollständiger Adresse erstellt
        Person person = Person.builder()
                .firstName("Max")           // Pflichtfeld
                .lastName("Mustermann")     // Pflichtfeld
                .dateOfBirth(LocalDate.of(1980, 1, 1))  // Pflichtfeld
                .street("Musterstraße")
                .houseNumber("123")
                .postalCode("12345")
                .city("Musterstadt")
                .country("Deutschland")
                .build();

        // Act - Adressinformationen werden abgerufen
        String fullAddress = person.getFullAddress();
        boolean isGerman = person.isGermanAddress();

        // Assert - Ergebnisse werden verifiziert
        assertThat(fullAddress).isEqualTo("Musterstraße 123, 12345 Musterstadt, Deutschland");
        assertThat(isGerman).isTrue();
    }

    /**
     * Testet die Namensformatierung mit akademischem Titel.

     * Dieser Test verifiziert die {@link PersonName}-Funktionalität
     * innerhalb der {@link Person}-Klasse. Titel werden in {@link Person#getFullName()}
     * korrekt eingefügt, jedoch in {@link Person#getOfficialName()}
     * für behördliche Zwecke weggelassen.

     * Geprüfte Formate:

     *   {@code getFullName()}: "Dr. Max Mustermann" (mit Titel)
     *   {@code getOfficialName()}: "Mustermann, Max" (ohne Titel)
     *
     * @since 1.0
     * @see Person#getFullName()
     * @see Person#getOfficialName()
     */
    @Test
    void testNameFunctionality() {
        // Arrange - Person wird mit Titel erstellt
        Person person = Person.builder()
                .title("Dr.")
                .firstName("Max")
                .lastName("Mustermann")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();

        // Act - Namensformate werden abgerufen
        String fullName = person.getFullName();
        String officialName = person.getOfficialName();

        // Assert - Ergebnisse werden verifiziert
        assertThat(fullName).isEqualTo("Dr. Max Mustermann");
        assertThat(officialName).isEqualTo("Mustermann, Max");
    }

    /**
     * Testet die Verarbeitung von Zweitnamen (Middle Names).

     * Dieser Test verifiziert, dass {@link PersonName}-Objekte
     * Zweitnamen korrekt in der Namensformatierung berücksichtigen.
     * Der Zweitname wird in {@link Person#getFullName()} zwischen
     * Erst- und Nachname eingefügt.

     * Erwartetes Format:
     * Vorname Zweitname Nachname

     * Beispiel:

     * "Anna Maria Schmidt" für firstName="Anna", middleName="Maria", lastName="Schmidt"
     * 
     *
     * @since 1.0
     * @see PersonName
     * @see Person#getFullName()
     */
    @Test
    void testPersonWithMiddleName() {
        // Arrange - Person wird mit Zweitnamen erstellt
        Person person = Person.builder()
                .firstName("Anna")
                .middleName("Maria")
                .lastName("Schmidt")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        // Act - vollständiger Name wird abgerufen
        String fullName = person.getFullName();

        // Assert - Ergebnis wird verifiziert
        assertThat(fullName).isEqualTo("Anna Maria Schmidt");
    }
}