package de.behoerde.smartcivicregistry.person.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {
    
    @Test
    void testPersonCreation() {
        // Given
        Person person = Person.createTestPerson();
        
        // Then
        assertThat(person).isNotNull();
        assertThat(person.getFullName()).isEqualTo("Max Mustermann");
        assertThat(person.getEmail()).isEqualTo("max.mustermann@example.de");
        assertThat(person.isAdult()).isTrue();
        assertThat(person.isSenior()).isFalse();
    }
    
    @Test
    void testAgeCalculation() {
        // Given
        Person person = Person.builder()
                .firstName("Test")
                .lastName("Person")
                .dateOfBirth(LocalDate.now().minusYears(25))
                .build();
        
        // When
        int age = person.calculateAge();
        
        // Then
        assertThat(age).isEqualTo(25);
        assertThat(person.isAdult()).isTrue();
        assertThat(person.isSenior()).isFalse();
    }
    
    @Test
    void testSeniorPerson() {
        // Given
        Person person = Person.builder()
                .firstName("Senior")
                .lastName("Citizen")
                .dateOfBirth(LocalDate.now().minusYears(70))
                .build();
        
        // Then
        assertThat(person.isSenior()).isTrue();
    }
    
    @Test
    void testAddressFunctionality() {
        // Given
        Person person = Person.builder()
                .street("Musterstraße")
                .houseNumber("123")
                .postalCode("12345")
                .city("Musterstadt")
                .country("Deutschland")
                .build();
        
        // When
        String fullAddress = person.getFullAddress();
        boolean isGerman = person.isGermanAddress();
        
        // Then
        assertThat(fullAddress).isEqualTo("Musterstraße 123, 12345 Musterstadt, Deutschland");
        assertThat(isGerman).isTrue();
    }
    
    @Test
    void testNameFunctionality() {
        // Given
        Person person = Person.builder()
                .title("Dr.")
                .firstName("Max")
                .lastName("Mustermann")
                .build();
        
        // When
        String fullName = person.getFullName();
        String officialName = person.getOfficialName();
        
        // Then
        assertThat(fullName).isEqualTo("Dr. Max Mustermann");
        assertThat(officialName).isEqualTo("Mustermann, Max");
    }
    
    @Test
    void testPersonWithMiddleName() {
        // Given
        Person person = Person.builder()
                .firstName("Anna")
                .middleName("Maria")
                .lastName("Schmidt")
                .build();
        
        // When
        String fullName = person.getFullName();
        
        // Then
        assertThat(fullName).isEqualTo("Anna Maria Schmidt");
    }
}
