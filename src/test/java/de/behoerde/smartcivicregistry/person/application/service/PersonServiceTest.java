package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.application.dto.PersonRequestDTO;
import de.behoerde.smartcivicregistry.person.application.dto.PersonResponseDTO;
import de.behoerde.smartcivicregistry.person.application.mapper.PersonMapper;
import de.behoerde.smartcivicregistry.person.domain.model.Gender;
import de.behoerde.smartcivicregistry.person.domain.model.MaritalStatus;
import de.behoerde.smartcivicregistry.person.domain.model.Person;
import de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Test-Suite für {@link PersonServiceImpl} (Version 2.0).
 * <p>
 * Diese Testklasse validiert die DTO-basierte Geschäftslogik des PersonService.
 * </p>
 *
 * <strong>Version 2.0 Änderungen:</strong>
 * <ul>
 *   <li>Tests für DTO-basierte Service-Methoden</li>
 *   <li>Mock für PersonMapper hinzugefügt</li>
 *   <li>Tests für neue Validierungslogik</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PersonService Tests (DTO Version)")
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonServiceImpl personService;

    private Person testPerson;
    private PersonRequestDTO testRequestDTO;
    private PersonResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        // Test-Person Entity
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setFirstName("Max");
        testPerson.setLastName("Mustermann");
        testPerson.setEmail("max@example.de");
        testPerson.setDateOfBirth(LocalDate.of(1980, 1, 1));
        testPerson.setNationalIdNumber("T22000129");
        testPerson.setTaxId("12345678901");

        // Test Request DTO
        testRequestDTO = PersonRequestDTO.builder()
                .firstName("Max")
                .lastName("Mustermann")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .email("max@example.de")
                .nationalIdNumber("T22000129")
                .taxId("12345678901")
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .citizenship("Deutsch")
                .street("Musterstraße")
                .houseNumber("123")
                .postalCode("12345")
                .city("Musterstadt")
                .country("Deutschland")
                .build();

        // Test Response DTO
        testResponseDTO = PersonResponseDTO.builder()
                .id(1L)
                .firstName("Max")
                .lastName("Mustermann")
                .fullName("Max Mustermann")
                .officialName("Mustermann, Max")
                .age(44)
                .isAdult(true)
                .isSenior(false)
                .email("max@example.de")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .build();
    }

    // ==================== CREATE OPERATIONS ====================

    @Test
    @DisplayName("createPerson - Soll Person erfolgreich speichern")
    void createPerson_ShouldSavePerson() {
        // Given
        when(personRepository.existsByEmail(anyString())).thenReturn(false);
        when(personRepository.existsByNationalIdNumber(anyString())).thenReturn(false);
        when(personMapper.toEntity(any(PersonRequestDTO.class))).thenReturn(testPerson);
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);
        when(personMapper.toResponseDTO(any(Person.class))).thenReturn(testResponseDTO);

        // When
        PersonResponseDTO result = personService.createPerson(testRequestDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("Max Mustermann");
        verify(personRepository, times(1)).save(testPerson);
    }

    @Test
    @DisplayName("createPerson - Mit doppelter E-Mail sollte Exception werfen")
    void createPerson_WithDuplicateEmail_ShouldThrowException() {
        // Given
        when(personRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> personService.createPerson(testRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(personRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPerson - Mit doppelter Personalausweisnummer sollte Exception werfen")
    void createPerson_WithDuplicateNationalId_ShouldThrowException() {
        // Given
        when(personRepository.existsByEmail(anyString())).thenReturn(false);
        when(personRepository.existsByNationalIdNumber(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> personService.createPerson(testRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(personRepository, never()).save(any());
    }

    @Test
    @DisplayName("createPerson - Mit minderjähriger Person sollte warnen")
    void createPerson_WithMinorPerson_ShouldLogWarning() {
        // Given
        PersonRequestDTO minorRequest = PersonRequestDTO.builder()
                .firstName("Kind")
                .lastName("Jugendlich")
                .dateOfBirth(LocalDate.now().minusYears(16))
                .build();

        when(personRepository.existsByEmail(nullable(String.class))).thenReturn(false);
        when(personRepository.existsByNationalIdNumber(nullable(String.class))).thenReturn(false);
        when(personMapper.toEntity(any(PersonRequestDTO.class))).thenReturn(new Person());
        when(personRepository.save(any(Person.class))).thenReturn(new Person());
        when(personMapper.toResponseDTO(any(Person.class))).thenReturn(new PersonResponseDTO());

        // When
        personService.createPerson(minorRequest);

        // Then
        verify(personRepository, times(1)).save(any());
    }

    // ==================== READ OPERATIONS ====================

    @Test
    @DisplayName("getPersonById - Soll Person zurückgeben wenn existiert")
    void getPersonById_ShouldReturnPersonWhenExists() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(personMapper.toResponseDTO(any(Person.class))).thenReturn(testResponseDTO);

        // When
        Optional<PersonResponseDTO> result = personService.getPersonById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getEmail()).isEqualTo("max@example.de");
    }

    @Test
    @DisplayName("getPersonById - Soll empty zurückgeben wenn nicht existiert")
    void getPersonById_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<PersonResponseDTO> result = personService.getPersonById(999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getPersonById - Mit null ID sollte Exception werfen")
    void getPersonById_WithNullId_ShouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> personService.getPersonById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("getPersonByEmail - Soll Person zurückgeben")
    void getPersonByEmail_ShouldReturnPerson() {
        // Given
        when(personRepository.findByEmail("max@example.de")).thenReturn(Optional.of(testPerson));
        when(personMapper.toResponseDTO(any(Person.class))).thenReturn(testResponseDTO);

        // When
        Optional<PersonResponseDTO> result = personService.getPersonByEmail("max@example.de");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("max@example.de");
    }

    @Test
    @DisplayName("getPersonByEmail - Mit leerer Email sollte Exception werfen")
    void getPersonByEmail_WithEmptyEmail_ShouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> personService.getPersonByEmail(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("getAllPersons - Soll paginierte Personen zurückgeben")
    void getAllPersons_ShouldReturnPage() {
        // Given
        Page<Person> personPage = new PageImpl<>(Arrays.asList(testPerson));
        Page<PersonResponseDTO> dtoPage = new PageImpl<>(Arrays.asList(testResponseDTO));

        when(personRepository.findAll(any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toResponseDTOPage(any(Page.class))).thenReturn(dtoPage);

        // When
        Page<PersonResponseDTO> result = personService.getAllPersons(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getAllPersons - Ohne Pageable sollte unpaged verwenden")
    void getAllPersons_WithoutPageable_ShouldUseUnpaged() {
        // Given
        Page<Person> personPage = new PageImpl<>(Collections.emptyList());
        Page<PersonResponseDTO> dtoPage = new PageImpl<>(Collections.emptyList());

        when(personRepository.findAll(any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toResponseDTOPage(any(Page.class))).thenReturn(dtoPage);

        // When
        Page<PersonResponseDTO> result = personService.getAllPersons(null);

        // Then
        assertThat(result).isEmpty();
    }

    // ==================== SEARCH OPERATIONS ====================

    @Test
    @DisplayName("searchPersonsByName - Soll Personen nach Namen suchen")
    void searchPersonsByName_ShouldSearchPersons() {
        // Given
        Page<Person> personPage = new PageImpl<>(Arrays.asList(testPerson));
        when(personRepository.searchByAnyField(eq("Max"), any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(testResponseDTO));

        // When
        List<PersonResponseDTO> result = personService.searchPersonsByName("Max");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).contains("Max");
    }

    @Test
    @DisplayName("findPersonsByLastName - Soll Personen nach Nachnamen finden")
    void findPersonsByLastName_ShouldFindPersons() {
        // Given
        when(personRepository.findByLastName("Mustermann")).thenReturn(Arrays.asList(testPerson));
        when(personMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(testResponseDTO));

        // When
        List<PersonResponseDTO> result = personService.findPersonsByLastName("Mustermann");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Mustermann");
    }

    @Test
    @DisplayName("findPersonsByCity - Soll Personen nach Stadt finden")
    void findPersonsByCity_ShouldFindPersons() {
        // Given
        when(personRepository.findByCity("Musterstadt")).thenReturn(Arrays.asList(testPerson));
        when(personMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(testResponseDTO));

        // When
        List<PersonResponseDTO> result = personService.findPersonsByCity("Musterstadt");

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findPersonsByDateOfBirth - Soll Personen nach Geburtsdatum finden")
    void findPersonsByDateOfBirth_ShouldFindPersons() {
        // Given
        LocalDate birthDate = LocalDate.of(1980, 1, 1);
        when(personRepository.findByDateOfBirth(birthDate)).thenReturn(Arrays.asList(testPerson));
        when(personMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(testResponseDTO));

        // When
        List<PersonResponseDTO> result = personService.findPersonsByDateOfBirth(birthDate);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findPersonsByBirthDateRange - Soll Personen im Datumsbereich finden")
    void findPersonsByBirthDateRange_ShouldFindPersons() {
        // Given
        LocalDate start = LocalDate.of(1970, 1, 1);
        LocalDate end = LocalDate.of(1990, 12, 31);
        when(personRepository.findByBirthDateRange(start, end)).thenReturn(Arrays.asList(testPerson));
        when(personMapper.toResponseDTOList(anyList())).thenReturn(Arrays.asList(testResponseDTO));

        // When
        List<PersonResponseDTO> result = personService.findPersonsByBirthDateRange(start, end);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findPersonsByBirthDateRange - Mit ungültigem Bereich sollte Exception werfen")
    void findPersonsByBirthDateRange_WithInvalidRange_ShouldThrowException() {
        // Given
        LocalDate start = LocalDate.of(1990, 1, 1);
        LocalDate end = LocalDate.of(1970, 12, 31); // start > end

        // When/Then
        assertThatThrownBy(() -> personService.findPersonsByBirthDateRange(start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be before or equal");
    }

    @Test
    @DisplayName("searchPersonsByAnyField - Soll paginierte Suche durchführen")
    void searchPersonsByAnyField_ShouldSearchWithPagination() {
        // Given
        Page<Person> personPage = new PageImpl<>(Arrays.asList(testPerson));
        Page<PersonResponseDTO> dtoPage = new PageImpl<>(Arrays.asList(testResponseDTO));

        when(personRepository.searchByAnyField(eq("search"), any(Pageable.class))).thenReturn(personPage);
        when(personMapper.toResponseDTOPage(any(Page.class))).thenReturn(dtoPage);

        // When
        Page<PersonResponseDTO> result = personService.searchPersonsByAnyField("search", PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
    }

    // ==================== UTILITY OPERATIONS ====================

    @Test
    @DisplayName("countPersonsByCity - Soll Anzahl der Personen in Stadt zählen")
    void countPersonsByCity_ShouldCountPersons() {
        // Given
        when(personRepository.countByCity("Musterstadt")).thenReturn(5L);

        // When
        long count = personService.countPersonsByCity("Musterstadt");

        // Then
        assertThat(count).isEqualTo(5L);
    }

    @Test
    @DisplayName("personExistsByEmail - Soll true zurückgeben wenn Person existiert")
    void personExistsByEmail_ShouldReturnTrueWhenExists() {
        // Given
        when(personRepository.existsByEmail("max@example.de")).thenReturn(true);

        // When
        boolean exists = personService.personExistsByEmail("max@example.de");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByNationalIdNumber - Soll false zurückgeben wenn nicht existiert")
    void existsByNationalIdNumber_ShouldReturnFalseWhenNotExists() {
        // Given
        when(personRepository.existsByNationalIdNumber("UNKNOWN")).thenReturn(false);

        // When
        boolean exists = personService.existsByNationalIdNumber("UNKNOWN");

        // Then
        assertThat(exists).isFalse();
    }

    // ==================== UPDATE OPERATIONS ====================

    @Test
    @DisplayName("updatePerson - Soll Person vollständig aktualisieren")
    void updatePerson_ShouldUpdatePerson() {
        // Given
        Person existingPerson = new Person();
        existingPerson.setId(1L);
        existingPerson.setEmail("old@example.de");

        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setEmail("new@example.de");

        PersonResponseDTO updatedResponse = PersonResponseDTO.builder()
                .id(1L)
                .email("new@example.de")
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(personRepository.existsByEmail("new@example.de")).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);
        when(personMapper.toResponseDTO(any(Person.class))).thenReturn(updatedResponse);

        // When
        PersonRequestDTO updateRequest = PersonRequestDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("new@example.de")
                .build();

        PersonResponseDTO result = personService.updatePerson(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.de");
        verify(personRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("partialUpdatePerson - Soll nur gesetzte Felder aktualisieren")
    void partialUpdatePerson_ShouldUpdateOnlySetFields() {
        // Given
        Person existingPerson = new Person();
        existingPerson.setId(1L);
        existingPerson.setFirstName("Old");
        existingPerson.setLastName("Name");
        existingPerson.setEmail("old@example.de");

        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setFirstName("New"); // Geändert
        updatedPerson.setLastName("Name"); // Unverändert
        updatedPerson.setEmail("old@example.de"); // Unverändert

        PersonResponseDTO updatedResponse = PersonResponseDTO.builder()
                .id(1L)
                .firstName("New")
                .lastName("Name")
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenReturn(updatedPerson);
        when(personMapper.toResponseDTO(any(Person.class))).thenReturn(updatedResponse);

        // When - Nur firstName ändern
        PersonRequestDTO partialUpdate = PersonRequestDTO.builder()
                .firstName("New")
                .build();

        PersonResponseDTO result = personService.partialUpdatePerson(1L, partialUpdate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("New");
        verify(personRepository, times(1)).save(any());
    }

    // ==================== DELETE OPERATIONS ====================

    @Test
    @DisplayName("deletePerson - Soll Person soft-deleten")
    void deletePerson_ShouldSoftDeletePerson() {
        // Given
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));

        // When
        personService.deletePerson(1L);

        // Then
        verify(personRepository, times(1)).delete(testPerson);
    }

    @Test
    @DisplayName("deletePerson - Mit nicht-existierender ID sollte Exception werfen")
    void deletePerson_WithNonExistingId_ShouldThrowException() {
        // Given
        when(personRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> personService.deletePerson(999L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("not found");

        verify(personRepository, never()).delete(any());
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("createPerson - Mit null RequestDTO sollte Exception werfen")
    void createPerson_WithNullRequest_ShouldThrowException() {
        // When/Then
        assertThatThrownBy(() -> personService.createPerson(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("updatePerson - Mit null ID sollte Exception werfen")
    void updatePerson_WithNullId_ShouldThrowException() {
        // Given
        PersonRequestDTO request = PersonRequestDTO.builder()
                .firstName("Test")
                .lastName("Person")
                .dateOfBirth(LocalDate.now())
                .build();

        // When/Then
        assertThatThrownBy(() -> personService.updatePerson(null, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Alle Methoden - Mit leeren Strings sollten Exceptions werfen")
    void allMethods_WithEmptyStrings_ShouldThrowExceptions() {
        // Test verschiedener Methoden mit leeren Strings
        assertThatThrownBy(() -> personService.getPersonByEmail(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> personService.searchPersonsByName(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> personService.findPersonsByLastName(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> personService.findPersonsByCity(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> personService.countPersonsByCity(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> personService.personExistsByEmail(""))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> personService.existsByNationalIdNumber(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Steuer-ID Prüfung - Mit doppelter Steuer-ID sollte Exception werfen")
    void taxIdValidation_WithDuplicateTaxId_ShouldThrowException() {
        // Given
        when(personRepository.existsByEmail(anyString())).thenReturn(false);
        when(personRepository.existsByNationalIdNumber(anyString())).thenReturn(false);

        // Simuliere, dass Steuer-ID bereits existiert
        when(personRepository.findAll()).thenReturn(Arrays.asList(
                Person.builder().id(2L).taxId("12345678901").build() // Andere Person mit gleicher TaxId
        ));

        when(personMapper.toEntity(any(PersonRequestDTO.class))).thenReturn(testPerson);

        // When/Then
        assertThatThrownBy(() -> personService.createPerson(testRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tax ID");
    }
}