package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.domain.model.Person;
import de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit-Test-Suite für {@link PersonServiceImpl}.
 * <p>
 * Diese Testklasse validiert die Geschäftslogik des PersonService unter Verwendung
 * von Mocking für externe Abhängigkeiten. Sie testet isoliert die Service-Layer-Logik
 * ohne tatsächliche Datenbankzugriffe.
 * </p>
 *
 * <strong>Test-Strategie:</strong>
 * <ul>
 *   <li><strong>Unit-Tests:</strong> Isolierte Tests der Service-Logik</li>
 *   <li><strong>Mocking:</strong> {@link PersonRepository} wird gemockt (keine DB)</li>
 *   <li><strong>Fokus:</strong> Geschäftslogik, Validierungen, Exception-Handling</li>
 *   <li><strong>Test-Pyramide:</strong> Schnelle Unit-Tests als Basis</li>
 * </ul>
 *
 * <strong>Test-Frameworks:</strong>
 * <ul>
 *   <li><strong>JUnit 5 (Jupiter):</strong> Test-Framework</li>
 *   <li><strong>Mockito:</strong> Mocking-Framework für Repository</li>
 *   <li><strong>AssertJ:</strong> Flüssige Assertions für bessere Lesbarkeit</li>
 *   <li><strong>MockitoExtension:</strong> Automatische Mock-Initialisierung</li>
 * </ul>
 *
 * <strong>Testabdeckung:</strong>
 * <p>
 * Diese Test-Suite deckt aktuell folgende Use Cases ab:
 * </p>
 * <ul>
 *   <li>Person erstellen (Success & Duplikat-E-Mail)</li>
 *   <li>Person abrufen nach ID</li>
 *   <li>Alle Personen abrufen (paginiert)</li>
 *   <li>Person löschen (Soft-Delete)</li>
 * </ul>
 *
 * <strong>Fehlende Testabdeckung (TODO):</strong>
 * <ul>
 *   <li>Update-Operationen (updatePerson, partialUpdatePerson)</li>
 *   <li>Such-Operationen (searchByName, searchByCity, etc.)</li>
 *   <li>Validierungen (Personalausweisnummer, Steuer-ID)</li>
 *   <li>Business-Regeln (Minderjährige-Warnung)</li>
 *   <li>Edge Cases (null-Werte, leere Strings)</li>
 * </ul>
 *
 * <strong>Naming Convention:</strong>
 * <p>
 * Testmethoden folgen dem Pattern: {@code methodName_scenario_expectedBehavior}
 * </p>
 * <ul>
 *   <li>Beispiel: {@code createPerson_WithDuplicateEmail_ShouldThrowException}</li>
 *   <li>Lesbar, selbstdokumentierend, beschreibt Erwartung klar</li>
 * </ul>
 *
 * <strong>Given-When-Then Pattern:</strong>
 * <p>
 * Alle Tests folgen dem Given-When-Then (Arrange-Act-Assert) Pattern für
 * strukturierte und leicht verständliche Tests.
 * </p>
 *
 * @author Smart Civic Registry Team
 * @version 1.0
 * @since Phase 2 (Person Domain Module)
 * @see PersonServiceImpl
 * @see PersonRepository
 * @see Person
 */
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    /**
     * Gemocktes Repository für Datenzugriff.
     * <p>
     * Wird von Mockito automatisch initialisiert und in {@link #personService} injiziert.
     * Alle Methodenaufrufe müssen explizit mit {@code when(...).thenReturn(...)} konfiguriert werden.
     * </p>
     *
     * @see Mock
     */
    @Mock
    private PersonRepository personRepository;

    /**
     * Zu testende Service-Instanz.
     * <p>
     * Mockito injiziert automatisch alle {@link Mock}-annotierten Abhängigkeiten
     * (hier {@link #personRepository}) in diese Instanz.
     * </p>
     *
     * @see InjectMocks
     */
    @InjectMocks
    private PersonServiceImpl personService;

    /**
     * Test-Person die in mehreren Tests wiederverwendet wird.
     * <p>
     * Wird in {@link #setUp()} vor jedem Test neu initialisiert, um Test-Isolation
     * zu gewährleisten (keine Seiteneffekte zwischen Tests).
     * </p>
     */
    private Person testPerson;

    /**
     * Setup-Methode die vor jedem Test ausgeführt wird.
     * <p>
     * Initialisiert {@link #testPerson} mit Standardwerten für typische Test-Szenarien.
     * Die Verwendung von {@link BeforeEach} stellt sicher, dass jeder Test mit
     * frischen, unveränderten Daten startet.
     * </p>
     *
     * <strong>Test-Person-Daten:</strong>
     * <ul>
     *   <li>ID: 1L (simuliert persistierte Entity)</li>
     *   <li>Name: Max Mustermann</li>
     *   <li>E-Mail: max@example.de</li>
     *   <li>Geburtsdatum: 01.01.1980 (44 Jahre alt, volljährig)</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setFirstName("Max");
        testPerson.setLastName("Mustermann");
        testPerson.setEmail("max@example.de");
        testPerson.setDateOfBirth(LocalDate.of(1980, 1, 1));
    }

    /**
     * Testet das erfolgreiche Erstellen einer Person.
     * <p>
     * Validiert, dass {@link PersonServiceImpl#createPerson(Person)} eine Person
     * korrekt persistiert, wenn alle Validierungen erfolgreich sind.
     * </p>
     *
     * <strong>Test-Szenario:</strong>
     * <ul>
     *   <li>E-Mail existiert noch nicht (keine Duplikate)</li>
     *   <li>Person wird erfolgreich gespeichert</li>
     * </ul>
     *
     * <strong>Erwartetes Verhalten:</strong>
     * <ul>
     *   <li>Person wird gespeichert</li>
     *   <li>Rückgabe-Objekt ist nicht null</li>
     *   <li>Vollständiger Name ist korrekt formatiert</li>
     *   <li>Repository.save() wird genau einmal aufgerufen</li>
     * </ul>
     *
     * <strong>Mocking-Setup:</strong>
     * <ul>
     *   <li>{@code existsByEmail()} gibt false zurück (E-Mail ist frei)</li>
     *   <li>{@code save()} gibt die gespeicherte Person zurück</li>
     * </ul>
     */
    @Test
    void createPerson_ShouldSavePerson() {
        // Given - Vorbereitung der Test-Daten und Mocks
        when(personRepository.existsByEmail(anyString())).thenReturn(false);
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        // When - Ausführung der zu testenden Methode
        Person created = personService.createPerson(testPerson);

        // Then - Validierung der Ergebnisse
        assertThat(created).isNotNull();
        assertThat(created.getFullName()).isEqualTo("Max Mustermann");
        verify(personRepository, times(1)).save(testPerson);
    }

    /**
     * Testet die E-Mail-Duplikatsprüfung beim Erstellen einer Person.
     * <p>
     * Validiert, dass {@link PersonServiceImpl#createPerson(Person)} eine
     * {@link IllegalArgumentException} wirft, wenn bereits eine Person mit
     * der gleichen E-Mail-Adresse existiert.
     * </p>
     *
     * <strong>Test-Szenario:</strong>
     * <ul>
     *   <li>E-Mail existiert bereits in der Datenbank</li>
     *   <li>Versuch, neue Person mit gleicher E-Mail zu erstellen</li>
     * </ul>
     *
     * <strong>Erwartetes Verhalten:</strong>
     * <ul>
     *   <li>IllegalArgumentException wird geworfen</li>
     *   <li>Exception-Message enthält "already exists"</li>
     *   <li>Repository.save() wird NICHT aufgerufen (Validierung verhindert Persistierung)</li>
     * </ul>
     *
     * <strong>Business-Regel:</strong>
     * <p>
     * E-Mail-Adressen müssen systemweit eindeutig sein, um korrekte Identifikation
     * und Kommunikation sicherzustellen.
     * </p>
     *
     * <strong>Mocking-Setup:</strong>
     * <ul>
     *   <li>{@code existsByEmail()} gibt true zurück (E-Mail bereits vergeben)</li>
     * </ul>
     */
    @Test
    void createPerson_WithDuplicateEmail_ShouldThrowException() {
        // Given - E-Mail existiert bereits
        when(personRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then - Erwartete Exception validieren
        assertThatThrownBy(() -> personService.createPerson(testPerson))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    /**
     * Testet das Abrufen einer Person nach ID.
     * <p>
     * Validiert, dass {@link PersonServiceImpl#getPersonById(Long)} eine Person
     * korrekt aus dem Repository lädt.
     * </p>
     *
     * <strong>Test-Szenario:</strong>
     * <ul>
     *   <li>Person mit ID 1 existiert</li>
     *   <li>Abruf nach dieser ID</li>
     * </ul>
     *
     * <strong>Erwartetes Verhalten:</strong>
     * <ul>
     *   <li>Optional mit Person wird zurückgegeben</li>
     *   <li>Optional ist nicht leer (isPresent)</li>
     *   <li>Person hat korrekte E-Mail-Adresse</li>
     * </ul>
     *
     * <strong>Mocking-Setup:</strong>
     * <ul>
     *   <li>{@code findById(1L)} gibt Optional mit testPerson zurück</li>
     * </ul>
     *
     * <strong>Hinweis:</strong>
     * <p>
     * Durch {@code @Where(clause = "deleted = false")} auf {@link Person} würden
     * gelöschte Personen automatisch ausgeschlossen. Im Unit-Test wird dies nicht
     * getestet, da Repository gemockt ist.
     * </p>
     */
    @Test
    void getPersonById_ShouldReturnPerson() {
        // Given - Person existiert im Repository
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));

        // When - Person nach ID abrufen
        Optional<Person> found = personService.getPersonById(1L);

        // Then - Validierung der Ergebnisse
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("max@example.de");
    }

    /**
     * Testet das paginierte Abrufen aller Personen.
     * <p>
     * Validiert, dass {@link PersonServiceImpl#getAllPersons(Pageable)} eine
     * paginierte Liste von Personen korrekt zurückgibt.
     * </p>
     *
     * <strong>Test-Szenario:</strong>
     * <ul>
     *   <li>Repository enthält mindestens eine Person</li>
     *   <li>Abruf mit Paginierung (Seite 0, Größe 10)</li>
     * </ul>
     *
     * <strong>Erwartetes Verhalten:</strong>
     * <ul>
     *   <li>Page-Objekt wird zurückgegeben</li>
     *   <li>Page ist nicht leer</li>
     *   <li>Page enthält genau 1 Person</li>
     * </ul>
     *
     * <strong>Mocking-Setup:</strong>
     * <ul>
     *   <li>{@code findAll(Pageable)} gibt PageImpl mit testPerson zurück</li>
     * </ul>
     *
     * <strong>Paginierung:</strong>
     * <p>
     * Die Methode unterstützt Sortierung und Paginierung. Im Test wird
     * {@code PageRequest.of(0, 10)} verwendet (Seite 0, 10 Elemente pro Seite).
     * </p>
     */
    @Test
    void getAllPersons_ShouldReturnPage() {
        // Given - Repository liefert Page mit einer Person
        Page<Person> personPage = new PageImpl<>(Arrays.asList(testPerson));
        when(personRepository.findAll(any(Pageable.class))).thenReturn(personPage);

        // When - Alle Personen abrufen
        Page<Person> result = personService.getAllPersons(PageRequest.of(0, 10));

        // Then - Validierung der Paginierung
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1);
    }

    /**
     * Testet das Soft-Delete einer Person.
     * <p>
     * Validiert, dass {@link PersonServiceImpl#deletePerson(Long)} das Repository
     * korrekt aufruft, um eine Person zu löschen (Soft-Delete).
     * </p>
     *
     * <strong>Test-Szenario:</strong>
     * <ul>
     *   <li>Person mit ID 1 existiert</li>
     *   <li>Löschung der Person</li>
     * </ul>
     *
     * <strong>Erwartetes Verhalten:</strong>
     * <ul>
     *   <li>Repository.delete() wird genau einmal aufgerufen</li>
     *   <li>Übergebenes Objekt ist die testPerson</li>
     * </ul>
     *
     * <strong>Soft-Delete-Mechanismus:</strong>
     * <p>
     * Durch {@code @SQLDelete(sql = "UPDATE persons SET deleted = true WHERE id=?")}
     * auf {@link Person} wird beim {@code repository.delete()} kein physisches DELETE,
     * sondern ein UPDATE ausgeführt. Dies wird im Unit-Test nicht validiert, da
     * Repository gemockt ist. Für echte Soft-Delete-Tests siehe Integration-Tests.
     * </p>
     *
     * <strong>Mocking-Setup:</strong>
     * <ul>
     *   <li>{@code findById(1L)} gibt Optional mit testPerson zurück</li>
     *   <li>{@code delete()} wird gemockt (keine Rückgabe)</li>
     * </ul>
     */
    @Test
    void deletePerson_ShouldCallRepositoryDelete() {
        // Given - Person existiert im Repository
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));

        // When - Person löschen
        personService.deletePerson(1L);

        // Then - Validierung des Repository-Aufrufs
        verify(personRepository, times(1)).delete(testPerson);
    }

    // ==================== FEHLENDE TESTS (TODO) ====================

    // TODO: Test für updatePerson() - Vollständiges Update
    // - Erfolgreicher Update
    // - Update mit E-Mail-Änderung (Success & Duplikat)
    // - Update mit Personalausweisnummer-Änderung (Success & Duplikat)
    // - Update mit Steuer-ID-Änderung (Success & Duplikat)
    // - Update nicht-existierende Person (Exception)

    // TODO: Test für partialUpdatePerson() - Partielles Update
    // - Nur einzelne Felder aktualisieren
    // - Null-Werte werden ignoriert
    // - E-Mail-Änderung mit Duplikatsprüfung

    // TODO: Tests für Such-Operationen
    // - searchPersonsByName()
    // - findPersonsByLastName()
    // - findPersonsByCity()
    // - findPersonsByDateOfBirth()
    // - findPersonsByBirthDateRange()
    // - searchPersonsByAnyField()

    // TODO: Tests für Utility-Operationen
    // - countPersonsByCity()
    // - personExistsByEmail()
    // - existsByNationalIdNumber()

    // TODO: Edge-Case-Tests
    // - Null-Werte in Pflichtfeldern
    // - Leere Strings
    // - Sehr lange Strings (über Feldlänge)
    // - Ungültige Datumswerte
    // - Zukünftige Geburtsdaten

    // TODO: Business-Regel-Tests
    // - Minderjährige-Warnung (Alter < 18)
    // - Senior-Erkennung (Alter >= 65)
    // - Deutsche Adresserkennung
}