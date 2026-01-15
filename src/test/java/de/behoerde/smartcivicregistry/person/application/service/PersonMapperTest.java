package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.application.dto.PersonRequestDTO;
import de.behoerde.smartcivicregistry.person.application.dto.PersonResponseDTO;
import de.behoerde.smartcivicregistry.person.application.mapper.PersonMapper;
import de.behoerde.smartcivicregistry.person.domain.model.Gender;
import de.behoerde.smartcivicregistry.person.domain.model.MaritalStatus;
import de.behoerde.smartcivicregistry.person.domain.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für den {@link PersonMapper} und die Domain-Logik der {@link Person}-Klasse.
 *
 * Diese Testklasse validiert:
 * - Die korrekte Konvertierung zwischen Person-Entität und DTOs durch den Mapper
 * - Die Geschäftslogik der Person-Entität (Alter, Namen, Adresse etc.)
 *
 * Hinweis: PersonResponseDTO enthält keine Logik – daher werden keine statischen
 * Hilfsmethoden darauf getestet.
 *
 * @author Smart Civic Registry Team
 * @version 4.1
 */
@DisplayName("Person Mapping & Domain Logic Tests")
class PersonMapperTest {

    private final PersonMapper mapper = new PersonMapper();

    // ==================== MAPPING TESTS ====================

    @Nested
    @DisplayName("Person ↔ DTO Mapping Tests")
    class MappingTests {

        @Test
        @DisplayName("Soll Person korrekt zu PersonResponseDTO mappen")
        void mapPersonToResponseDTO() {
            // Given
            Person person = Person.builder()
                    .id(1L)
                    .title("Dr.")
                    .firstName("Max")
                    .middleName("Johann")
                    .lastName("Mustermann")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .gender(Gender.MALE)
                    .citizenship("Deutsch")
                    .street("Musterstraße")
                    .houseNumber("123")
                    .postalCode("12345")
                    .city("Musterstadt")
                    .country("Deutschland")
                    .email("max@example.de")
                    .phone("030 12345678")
                    .maritalStatus(MaritalStatus.MARRIED)
                    .birthPlace("Berlin")
                    .nationalIdNumber("T22000129")
                    .taxId("12345678901")
                    .build();

            // When
            PersonResponseDTO dto = mapper.toResponseDTO(person);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getFullName()).isEqualTo("Dr. Max Johann Mustermann");
            assertThat(dto.getOfficialName()).isEqualTo("Mustermann, Max");
            assertThat(dto.getFullAddress()).isEqualTo("Musterstraße 123, 12345 Musterstadt, Deutschland");
            assertThat(dto.getAge()).isEqualTo(java.time.Period.between(LocalDate.of(1980, 1, 1), LocalDate.now()).getYears());
            assertThat(dto.getIsAdult()).isTrue();
            assertThat(dto.getIsSenior()).isFalse();
            assertThat(dto.getIsGermanAddress()).isTrue();
        }

        @Test
        @DisplayName("Soll PersonRequestDTO korrekt zu Person-Entity mappen")
        void mapRequestDTOToPerson() {
            // Given
            PersonRequestDTO request = PersonRequestDTO.builder()
                    .title("Prof.")
                    .firstName("Anna")
                    .middleName("Maria")
                    .lastName("Schmidt")
                    .dateOfBirth(LocalDate.of(1990, 5, 15))
                    .gender(Gender.FEMALE)
                    .citizenship("Deutsch")
                    .street("Beispielweg")
                    .houseNumber("42")
                    .postalCode("54321")
                    .city("Beispielstadt")
                    .country("Germany")
                    .email("anna.schmidt@example.com")
                    .phone("+49 30 98765432")
                    .maritalStatus(MaritalStatus.SINGLE)
                    .birthPlace("Hamburg")
                    .nationalIdNumber("X98765432")
                    .taxId("98765432109")
                    .build();

            // When
            Person person = mapper.toEntity(request);

            // Then
            assertThat(person).isNotNull();
            assertThat(person.getTitle()).isEqualTo("Prof.");
            assertThat(person.getFirstName()).isEqualTo("Anna");
            assertThat(person.getMiddleName()).isEqualTo("Maria");
            assertThat(person.getLastName()).isEqualTo("Schmidt");
            assertThat(person.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
            assertThat(person.getCountry()).isEqualTo("Germany");
            assertThat(person.getEmail()).isEqualTo("anna.schmidt@example.com");
        }
    }

    // ==================== DOMAIN LOGIC (bereits korrekt in Original) ====================

    @Nested
    @DisplayName("Person Domain Logik Tests")
    class PersonDomainLogicTests {

        @Test
        @DisplayName("Soll Alter über calculateAge() berechnen")
        void personCalculateAge() {
            Person person = Person.builder()
                    .firstName("Test")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.of(2000, 1, 1))
                    .build();
            int age = person.calculateAge();
            int expectedAge = java.time.Period.between(LocalDate.of(2000, 1, 1), LocalDate.now()).getYears();
            assertThat(age).isEqualTo(expectedAge);
        }

        @Test
        @DisplayName("Soll Volljährigkeit über isAdult() prüfen")
        void personIsAdult() {
            Person minor = Person.builder()
                    .firstName("Minor")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.now().minusYears(16))
                    .build();
            Person adult = Person.builder()
                    .firstName("Adult")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.now().minusYears(25))
                    .build();
            assertThat(minor.isAdult()).isFalse();
            assertThat(adult.isAdult()).isTrue();
        }

        @Test
        @DisplayName("Soll Seniorenstatus über isSenior() prüfen")
        void personIsSenior() {
            Person senior = Person.builder()
                    .firstName("Senior")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.now().minusYears(70))
                    .build();
            Person nonSenior = Person.builder()
                    .firstName("NonSenior")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.now().minusYears(50))
                    .build();
            assertThat(senior.isSenior()).isTrue();
            assertThat(nonSenior.isSenior()).isFalse();
        }

        @Test
        @DisplayName("Soll deutschen Adressstatus über isGermanAddress() prüfen")
        void personIsGermanAddress() {
            Person germanPerson = Person.builder()
                    .firstName("German")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .country("Deutschland")
                    .build();
            Person austrianPerson = Person.builder()
                    .firstName("Austrian")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .country("Österreich")
                    .build();
            assertThat(germanPerson.isGermanAddress()).isTrue();
            assertThat(austrianPerson.isGermanAddress()).isFalse();
        }

        @Test
        @DisplayName("Soll vollständigen Namen über getFullName() generieren")
        void personGetFullName() {
            Person person = Person.builder()
                    .title("Dr.")
                    .firstName("Max")
                    .middleName("Johann")
                    .lastName("Mustermann")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .build();
            assertThat(person.getFullName()).isEqualTo("Dr. Max Johann Mustermann");
        }

        @Test
        @DisplayName("Soll offiziellen Namen über getOfficialName() generieren")
        void personGetOfficialName() {
            Person person = Person.builder()
                    .firstName("Max")
                    .lastName("Mustermann")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .build();
            assertThat(person.getOfficialName()).isEqualTo("Mustermann, Max");
        }

        @Test
        @DisplayName("Soll vollständige Adresse über getFullAddress() generieren")
        void personGetFullAddress() {
            Person person = Person.builder()
                    .firstName("Test")
                    .lastName("Person")
                    .dateOfBirth(LocalDate.of(1990, 1, 1))
                    .street("Musterstraße")
                    .houseNumber("123")
                    .postalCode("12345")
                    .city("Musterstadt")
                    .country("Deutschland")
                    .build();
            assertThat(person.getFullAddress()).isEqualTo("Musterstraße 123, 12345 Musterstadt, Deutschland");
        }
    }

    // ==================== BUILDER & ENUM TESTS (beibehalten) ====================

    @Nested
    @DisplayName("Person Builder Validierung Tests")
    class PersonBuilderValidationTests {

        @Test
        @DisplayName("Soll Person mit allen Pflichtfeldern erstellen")
        void builderWithRequiredFields() {
            Person person = Person.builder()
                    .firstName("Max")
                    .lastName("Mustermann")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .build();
            assertThat(person).isNotNull();
            assertThat(person.getFirstName()).isEqualTo("Max");
            assertThat(person.getLastName()).isEqualTo("Mustermann");
            assertThat(person.getDateOfBirth()).isEqualTo(LocalDate.of(1980, 1, 1));
        }

        @Test
        @DisplayName("Soll Person mit allen optionalen Feldern erstellen")
        void builderWithAllFields() {
            Person person = Person.builder()
                    .title("Dr.")
                    .firstName("Max")
                    .middleName("Johann")
                    .lastName("Mustermann")
                    .maidenName("Geburtsname")
                    .dateOfBirth(LocalDate.of(1980, 1, 1))
                    .gender(Gender.MALE)
                    .maritalStatus(MaritalStatus.MARRIED)
                    .citizenship("Deutsch")
                    .street("Musterstraße")
                    .houseNumber("123")
                    .postalCode("12345")
                    .city("Musterstadt")
                    .country("Deutschland")
                    .email("max@example.de")
                    .phone("030 12345678")
                    .mobilePhone("0176 98765432")
                    .birthPlace("Berlin")
                    .nationalIdNumber("T22000129")
                    .taxId("12345678901")
                    .build();
            assertThat(person).isNotNull();
            assertThat(person.getTitle()).isEqualTo("Dr.");
            assertThat(person.getMiddleName()).isEqualTo("Johann");
            assertThat(person.getMaidenName()).isEqualTo("Geburtsname");
            assertThat(person.getGender()).isEqualTo(Gender.MALE);
            assertThat(person.getMaritalStatus()).isEqualTo(MaritalStatus.MARRIED);
            assertThat(person.getEmail()).isEqualTo("max@example.de");
        }
    }

    @Nested
    @DisplayName("Gender und MaritalStatus Tests")
    class GenderAndMaritalStatusTests {

        @Test
        @DisplayName("Soll alle Geschlechterwerte verarbeiten")
        void testAllGenders() {
            assertThat(Gender.MALE.getDisplayName()).isEqualTo("Männlich");
            assertThat(Gender.FEMALE.getDisplayName()).isEqualTo("Weiblich");
            assertThat(Gender.DIVERSE.getDisplayName()).isEqualTo("Divers");
            assertThat(Gender.UNKNOWN.getDisplayName()).isEqualTo("Unbekannt");
        }

        @Test
        @DisplayName("Soll alle Familienstandswerte verarbeiten")
        void testAllMaritalStatuses() {
            assertThat(MaritalStatus.SINGLE.getDisplayName()).isEqualTo("Ledig");
            assertThat(MaritalStatus.MARRIED.getDisplayName()).isEqualTo("Verheiratet");
            assertThat(MaritalStatus.DIVORCED.getDisplayName()).isEqualTo("geschieden");
            assertThat(MaritalStatus.WIDOWED.getDisplayName()).isEqualTo("Verwitwet");
            assertThat(MaritalStatus.REGISTERED_PARTNERSHIP.getDisplayName())
                    .isEqualTo("Eingetragene Lebenspartnerschaft");
        }
    }
}