package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.domain.model.Person;
import de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementierung des {@link PersonService}-Interfaces für Personenstammdaten-Verwaltung.
 * <p>
 * Diese Klasse bildet den Application Service Layer in der hexagonalen Architektur und
 * orchestriert die Geschäftslogik für alle Use Cases rund um Personen. Sie koordiniert
 * die Interaktion zwischen Domain Layer (Repository) und Infrastructure Layer (Controller).
 * </p>
 *
 * <strong>Architektur-Rolle:</strong>
 * <ul>
 *   <li><strong>Application Service:</strong> Orchestriert Use Cases und Geschäftslogik</li>
 *   <li><strong>Transaction Boundary:</strong> Definiert Transaktionsgrenzen mit @Transactional</li>
 *   <li><strong>Validation Layer:</strong> Führt Business-Validierungen durch</li>
 *   <li><strong>Logging Point:</strong> Zentrale Logging-Stelle für alle Operationen</li>
 * </ul>
 *
 * <strong>Design-Patterns:</strong>
 * <ul>
 *   <li><strong>Service Pattern:</strong> Kapselt Geschäftslogik in wiederverwendbaren Services</li>
 *   <li><strong>Repository Pattern:</strong> Nutzt PersonRepository für Datenzugriff</li>
 *   <li><strong>Dependency Injection:</strong> Constructor Injection via @RequiredArgsConstructor</li>
 * </ul>
 *
 * <strong>Transaktionsverhalten:</strong>
 * <ul>
 *   <li>Klassen-Level: {@code @Transactional(readOnly = true)} - Standard für Lese-Operationen</li>
 *   <li>Methoden-Level: {@code @Transactional} - Überschreibt für Schreib-Operationen</li>
 *   <li>Read-Only optimiert Performance und verhindert unbeabsichtigte Änderungen</li>
 * </ul>
 *
 * <strong>Logging-Strategie:</strong>
 * <ul>
 *   <li><strong>INFO:</strong> Wichtige Geschäftsvorgänge (Create, Update, Delete)</li>
 *   <li><strong>WARN:</strong> Ungewöhnliche aber valide Situationen (z.B. minderjährige Personen)</li>
 *   <li><strong>DEBUG:</strong> Detaillierte Operationsinformationen für Entwicklung</li>
 * </ul>
 *
 * <strong>Validierungskonzept:</strong>
 * <p>
 * Alle Validierungen erfolgen auf Service-Ebene vor der Persistierung:
 * </p>
 * <ul>
 *   <li>Eindeutigkeitsprüfungen (E-Mail, Personalausweisnummer, Steuer-ID)</li>
 *   <li>Business-Regel-Validierungen (z.B. Alterswarnung)</li>
 *   <li>Referenzielle Integrität</li>
 * </ul>
 *
 * <strong>Exception-Handling:</strong>
 * <ul>
 *   <li>{@link IllegalArgumentException}: Validierungsfehler, ungültige Parameter</li>
 *   <li>{@link jakarta.persistence.EntityNotFoundException}: Entity nicht gefunden (implizit durch orElseThrow)</li>
 * </ul>
 *
 * @author Smart Civic Registry Team
 * @version 1.0
 * @since Phase 2 (Person Domain Module)
 * @see PersonService
 * @see PersonRepository
 * @see Person
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    /**
     * Repository für Datenzugriff auf Person-Entitäten.
     * <p>
     * Wird via Constructor Injection durch {@link RequiredArgsConstructor} injiziert.
     * Final, da nach Initialisierung unveränderlich.
     * </p>
     */
    private final PersonRepository personRepository;

    // ==================== CRUD OPERATIONS ====================

    /**
     * {@inheritDoc}
     *
     * <strong>Implementierungsdetails:</strong>
     * <ol>
     *   <li>Prüft Eindeutigkeit der E-Mail-Adresse (wenn vorhanden)</li>
     *   <li>Prüft Eindeutigkeit der Personalausweisnummer (wenn vorhanden)</li>
     *   <li>Warnt bei Erstellung minderjähriger Personen (Business-Regel)</li>
     *   <li>Persistiert Person über Repository</li>
     *   <li>Loggt erfolgreiche Erstellung mit generierter ID</li>
     * </ol>
     *
     * <strong>Transaktionsverhalten:</strong>
     * <p>
     * Methode ist mit {@code @Transactional} annotiert (überschreibt Klassen-Level readOnly).
     * Bei Exception wird automatisch Rollback durchgeführt.
     * </p>
     *
     * <strong>Business-Regeln:</strong>
     * <ul>
     *   <li>E-Mail muss systemweit eindeutig sein (wenn angegeben)</li>
     *   <li>Personalausweisnummer muss systemweit eindeutig sein (wenn angegeben)</li>
     *   <li>Minderjährige Personen (unter 18) werden geloggt, aber akzeptiert</li>
     * </ul>
     *
     * @throws IllegalArgumentException wenn E-Mail oder Personalausweisnummer bereits existiert
     */
    @Override
    @Transactional
    public Person createPerson(Person person) {
        log.info("Creating new person: {}", person.getFullName());

        // Validierung: Email muss eindeutig sein
        if (person.getEmail() != null && personRepository.existsByEmail(person.getEmail())) {
            throw new IllegalArgumentException("Person with email " + person.getEmail() + " already exists");
        }

        // Validierung: National ID muss eindeutig sein
        if (person.getNationalIdNumber() != null &&
                personRepository.existsByNationalIdNumber(person.getNationalIdNumber())) {
            throw new IllegalArgumentException("Person with national ID " + person.getNationalIdNumber() + " already exists");
        }

        // Altersvalidierung (Business Logic)
        if (!person.isAdult()) {
            log.warn("Creating person under 18: {}", person.getFullName());
        }

        Person savedPerson = personRepository.save(person);
        log.info("Person created successfully with ID: {}", savedPerson.getId());
        return savedPerson;
    }

    /**
     * {@inheritDoc}
     *
     * <strong>Implementierungsdetails:</strong>
     * <ol>
     *   <li>Lädt existierende Person aus Datenbank</li>
     *   <li>Aktualisiert alle Felder mit neuen Werten (außer ID und Audit-Felder)</li>
     *   <li>Prüft Eindeutigkeit bei Änderung von E-Mail, Personalausweisnummer, Steuer-ID</li>
     *   <li>Persistiert aktualisierte Person</li>
     *   <li>Loggt erfolgreiche Aktualisierung</li>
     * </ol>
     *
     * <strong>Eindeutigkeitsprüfungen:</strong>
     * <p>
     * Bei Änderung eindeutiger Felder wird geprüft, ob der neue Wert bereits
     * von einer anderen Person verwendet wird. Änderungen auf den gleichen Wert
     * (keine Änderung) sind erlaubt.
     * </p>
     *
     * <strong>Performance-Hinweis:</strong>
     * <p>
     * Die Steuer-ID-Prüfung verwendet {@code findAll().stream()} und ist bei
     * großen Datenmengen ineffizient. TODO: Repository-Methode implementieren.
     * </p>
     *
     * <strong>Unveränderliche Felder:</strong>
     * <ul>
     *   <li>ID (technischer Primärschlüssel)</li>
     *   <li>createdAt, createdBy (Audit-Trail)</li>
     *   <li>updatedAt, updatedBy (automatisch durch Hibernate gesetzt)</li>
     * </ul>
     *
     * @throws IllegalArgumentException wenn Person nicht existiert oder Eindeutigkeitsprüfung fehlschlägt
     */
    @Override
    @Transactional
    public Person updatePerson(Long id, Person person) {
        log.info("Updating person with id: {}", id);

        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        // Aktualisiere alle Felder (Full Update / PUT-Semantik)
        existingPerson.setTitle(person.getTitle());
        existingPerson.setFirstName(person.getFirstName());
        existingPerson.setMiddleName(person.getMiddleName());
        existingPerson.setLastName(person.getLastName());
        existingPerson.setMaidenName(person.getMaidenName());
        existingPerson.setDateOfBirth(person.getDateOfBirth());
        existingPerson.setGender(person.getGender());
        existingPerson.setCitizenship(person.getCitizenship());
        existingPerson.setStreet(person.getStreet());
        existingPerson.setHouseNumber(person.getHouseNumber());
        existingPerson.setPostalCode(person.getPostalCode());
        existingPerson.setCity(person.getCity());
        existingPerson.setCountry(person.getCountry());
        existingPerson.setPhone(person.getPhone());
        existingPerson.setMobilePhone(person.getMobilePhone());
        existingPerson.setMaritalStatus(person.getMaritalStatus());
        existingPerson.setBirthPlace(person.getBirthPlace());

        // Email kann nur geändert werden, wenn sie eindeutig bleibt
        if (person.getEmail() != null && !person.getEmail().equals(existingPerson.getEmail())) {
            if (personRepository.existsByEmail(person.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + person.getEmail());
            }
            existingPerson.setEmail(person.getEmail());
        }

        // National ID kann nur geändert werden, wenn sie eindeutig bleibt
        if (person.getNationalIdNumber() != null &&
                !person.getNationalIdNumber().equals(existingPerson.getNationalIdNumber())) {
            if (personRepository.existsByNationalIdNumber(person.getNationalIdNumber())) {
                throw new IllegalArgumentException("National ID already exists: " + person.getNationalIdNumber());
            }
            existingPerson.setNationalIdNumber(person.getNationalIdNumber());
        }

        // Tax ID kann nur geändert werden, wenn sie eindeutig bleibt
        // TODO: Performance-Optimierung - Repository-Methode statt findAll()
        if (person.getTaxId() != null &&
                !person.getTaxId().equals(existingPerson.getTaxId())) {
            // Prüfe ob Tax ID bereits verwendet wird (außer vom aktuellen Benutzer)
            Optional<Person> personWithSameTaxId = personRepository.findAll().stream()
                    .filter(p -> p.getTaxId() != null &&
                            p.getTaxId().equals(person.getTaxId()) &&
                            !p.getId().equals(id))
                    .findFirst();

            if (personWithSameTaxId.isPresent()) {
                throw new IllegalArgumentException("Tax ID already exists: " + person.getTaxId());
            }
            existingPerson.setTaxId(person.getTaxId());
        }

        Person updatedPerson = personRepository.save(existingPerson);
        log.info("Person with ID {} updated successfully", id);
        return updatedPerson;
    }

    /**
     * {@inheritDoc}
     *
     * <strong>Implementierungsdetails:</strong>
     * <ol>
     *   <li>Lädt existierende Person aus Datenbank</li>
     *   <li>Aktualisiert nur die nicht-null Felder aus personUpdates</li>
     *   <li>Prüft Eindeutigkeit bei Änderung der E-Mail</li>
     *   <li>Persistiert partiell aktualisierte Person</li>
     *   <li>Loggt erfolgreiche partielle Aktualisierung</li>
     * </ol>
     *
     * <strong>PATCH-Semantik:</strong>
     * <p>
     * Im Gegensatz zu {@link #updatePerson(Long, Person)} werden hier nur
     * die Felder aktualisiert, die in {@code personUpdates} gesetzt (nicht null) sind.
     * Alle null-Felder werden ignoriert und behalten ihren aktuellen Wert.
     * </p>
     *
     * <strong>Implementierte Felder:</strong>
     * <p>
     * Aktuell werden nur ausgewählte Felder unterstützt:
     * </p>
     * <ul>
     *   <li>Namensdaten: title, firstName, middleName, lastName</li>
     *   <li>Personendaten: dateOfBirth</li>
     *   <li>Kontaktdaten: email (mit Eindeutigkeitsprüfung)</li>
     * </ul>
     *
     * <strong>TODO:</strong>
     * <p>
     * Weitere Felder (Adresse, Telefon, etc.) sollten hinzugefügt werden,
     * um vollständige PATCH-Unterstützung zu bieten.
     * </p>
     *
     * @throws IllegalArgumentException wenn Person nicht existiert oder E-Mail bereits vergeben
     */
    @Override
    @Transactional
    public Person partialUpdatePerson(Long id, Person personUpdates) {
        log.info("Partially updating person with id: {}", id);

        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        // Aktualisiere nur gesetzte Felder (PATCH-Semantik)
        if (personUpdates.getTitle() != null) {
            existingPerson.setTitle(personUpdates.getTitle());
        }
        if (personUpdates.getFirstName() != null) {
            existingPerson.setFirstName(personUpdates.getFirstName());
        }
        if (personUpdates.getMiddleName() != null) {
            existingPerson.setMiddleName(personUpdates.getMiddleName());
        }
        if (personUpdates.getLastName() != null) {
            existingPerson.setLastName(personUpdates.getLastName());
        }
        if (personUpdates.getDateOfBirth() != null) {
            existingPerson.setDateOfBirth(personUpdates.getDateOfBirth());
        }
        if (personUpdates.getEmail() != null &&
                !personUpdates.getEmail().equals(existingPerson.getEmail())) {
            if (personRepository.existsByEmail(personUpdates.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + personUpdates.getEmail());
            }
            existingPerson.setEmail(personUpdates.getEmail());
        }

        Person updatedPerson = personRepository.save(existingPerson);
        log.info("Person with ID {} partially updated successfully", id);
        return updatedPerson;
    }

    /**
     * {@inheritDoc}
     *
     * <strong>Implementierungsdetails:</strong>
     * <ol>
     *   <li>Lädt existierende Person aus Datenbank</li>
     *   <li>Ruft {@code repository.delete()} auf</li>
     *   <li>Durch {@code @SQLDelete} auf {@link Person} wird Soft-Delete ausgeführt</li>
     *   <li>Person wird als gelöscht markiert (deleted = true), aber nicht physisch entfernt</li>
     *   <li>Loggt erfolgreiche Soft-Delete-Operation</li>
     * </ol>
     *
     * <strong>Soft-Delete-Mechanismus:</strong>
     * <p>
     * Die Person-Entity ist mit {@code @SQLDelete(sql = "UPDATE persons SET deleted = true WHERE id=?")}
     * annotiert. Dadurch wird beim Aufruf von {@code repository.delete()} kein DELETE SQL,
     * sondern ein UPDATE SQL ausgeführt, das das deleted-Flag setzt.
     * </p>
     *
     * <strong>Auswirkungen:</strong>
     * <ul>
     *   <li>Person verschwindet aus normalen Suchanfragen (durch @Where(clause = "deleted = false"))</li>
     *   <li>Datensatz bleibt physisch in DB für Audit-Zwecke und DSGVO-Compliance</li>
     *   <li>Referenzen zu anderen Entitäten bleiben intakt</li>
     *   <li>Wiederherstellung ist möglich durch manuelles Setzen von deleted = false</li>
     * </ul>
     *
     * <strong>DSGVO-Konformität:</strong>
     * <p>
     * Für echtes Löschen gemäß DSGVO Art. 17 (Recht auf Vergessenwerden) muss
     * ein separater Prozess implementiert werden, der personenbezogene Daten
     * anonymisiert oder physisch löscht.
     * </p>
     *
     * @throws IllegalArgumentException wenn Person nicht existiert
     */
    @Override
    @Transactional
    public void deletePerson(Long id) {
        log.info("Soft deleting person with id: {}", id);

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found with id: " + id));

        // Soft Delete (mark as deleted) - wird durch @SQLDelete Annotation gehandelt
        personRepository.delete(person);

        log.info("Person {} soft deleted successfully", id);
    }

    @Override
    public Optional<Person> getPersonById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> getPersonByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Page<Person> getAllPersons(Pageable pageable) {
        return null;
    }

    @Override
    public List<Person> searchPersonsByName(String name) {
        return List.of();
    }

    @Override
    public List<Person> findPersonsByLastName(String lastName) {
        return List.of();
    }

    @Override
    public List<Person> findPersonsByCity(String city) {
        return List.of();
    }

    @Override
    public List<Person> findPersonsByDateOfBirth(LocalDate dateOfBirth) {
        return List.of();
    }

    @Override
    public List<Person> findPersonsByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public Page<Person> searchPersonsByAnyField(String searchTerm, Pageable pageable) {
        return null;
    }

    @Override
    public long countPersonsByCity(String city) {
        return 0;
    }

    @Override
    public boolean personExistsByEmail(String email) {
        return false;
    }

// ==================== READ OPERATIONS ====================

/**
 * {@inheritDoc}
 *
 * <strong>Implementierungsdetai
 * <p>
 * Delegiert direkt an {@code personRepository.existsByNationalIdNumber()}.
 * Effizienter als findByNationalIdNumber(), da nur boolean ohne Datenübertragung.
 * </p>
 */
@Override
public boolean existsByNationalIdNumber(String nationalIdNumber) {
    log.debug("Checking if person exists with national ID: {}", nationalIdNumber);
    return personRepository.existsByNationalIdNumber(nationalIdNumber);
}
}