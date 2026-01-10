package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service-Interface für die Geschäftslogik und Anwendungsfälle rund um {@link Person}-Entitäten.
 * <p>
 * Dieses Interface definiert die Application Service Layer der hexagonalen Architektur
 * und kapselt alle Use Cases für die Verwaltung von Personenstammdaten. Es bildet die
 * Schnittstelle zwischen der Präsentationsschicht (z.B. REST-Controller) und der
 * Domain-Layer (Repositories, Entities).
 * </p>
 *
 * <strong>Hexagonale Architektur (Ports & Adapters):</strong>
 * <ul>
 *   <li><strong>Application Layer:</strong> Dieses Interface orchestriert Use Cases</li>
 *   <li><strong>Domain Layer:</strong> Nutzt {@link de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository}</li>
 *   <li><strong>Infrastructure Layer:</strong> Wird von REST-Controllern aufgerufen (geplant)</li>
 * </ul>
 *
 * <strong>Verantwortlichkeiten:</strong>
 * <ul>
 *   <li>Geschäftslogik und Validierung vor Persistierung</li>
 *   <li>Koordination von Domain-Operationen</li>
 *   <li>Transaktionsmanagement (@Transactional in Implementierung)</li>
 *   <li>Exception-Handling und Error-Mapping</li>
 *   <li>Audit-Trail-Verwaltung (createdBy, updatedBy)</li>
 * </ul>
 *
 * <strong>Design-Prinzipien:</strong>
 * <ul>
 *   <li><strong>Interface Segregation:</strong> Trennung von Implementierung und Schnittstelle</li>
 *   <li><strong>Dependency Inversion:</strong> Controller abhängig von Interface, nicht Implementierung</li>
 *   <li><strong>Single Responsibility:</strong> Fokus auf Personen-spezifische Use Cases</li>
 * </ul>
 *
 * <strong>Implementierung:</strong>
 * <p>
 * Die Implementierung erfolgt in {@link PersonServiceImpl}, die als Spring Bean
 * registriert ist und automatisch von Spring's Dependency Injection Container verwaltet wird.
 * </p>
 *
 * <strong>Verwendungsbeispiel:</strong>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/api/persons")
 * public class PersonController {
 *
 *     private final PersonService personService;
 *
 *     @Autowired
 *     public PersonController(PersonService personService) {
 *         this.personService = personService;
 *     }
 *
 *     @PostMapping
 *     public ResponseEntity<Person> create(@RequestBody Person person) {
 *         Person created = personService.createPerson(person);
 *         return ResponseEntity.status(HttpStatus.CREATED).body(created);
 *     }
 * }
 * }</pre>
 *
 * @author Smart Civic Registry Team
 * @version 1.0
 * @since Phase 2 (Person Domain Module)
 * @see PersonServiceImpl
 * @see Person
 * @see de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository
 */
public interface PersonService {

    // ==================== CRUD OPERATIONS ====================

    /**
     * Erstellt eine neue Person im System.
     * <p>
     * Diese Methode führt folgende Schritte aus:
     * </p>
     * <ol>
     *   <li>Validierung der Pflichtfelder (firstName, lastName, dateOfBirth)</li>
     *   <li>Duplikatsprüfung (E-Mail, Personalausweisnummer)</li>
     *   <li>Setzen von Audit-Feldern (createdBy, createdAt)</li>
     *   <li>Persistierung in der Datenbank</li>
     * </ol>
     *
     * <strong>Validierungen:</strong>
     * <ul>
     *   <li>Pflichtfelder dürfen nicht null oder leer sein</li>
     *   <li>E-Mail muss eindeutig sein (wenn angegeben)</li>
     *   <li>Personalausweisnummer muss eindeutig sein (wenn angegeben)</li>
     *   <li>Geburtsdatum darf nicht in der Zukunft liegen</li>
     *   <li>E-Mail-Format muss gültig sein (wenn angegeben)</li>
     * </ul>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Person person = Person.builder()
     *     .firstName("Max")
     *     .lastName("Mustermann")
     *     .dateOfBirth(LocalDate.of(1990, 5, 15))
     *     .email("max.mustermann@example.de")
     *     .build();
     *
     * Person created = personService.createPerson(person);
     * System.out.println("Erstellt mit ID: " + created.getId());
     * }</pre>
     *
     * <strong>DSGVO-Konformität:</strong>
     * <p>
     * Die Erstellung wird automatisch protokolliert (createdBy, createdAt) für
     * revisionssichere Nachvollziehbarkeit gemäß DSGVO-Anforderungen.
     * </p>
     *
     * @param person Die zu erstellende Person (nicht null, Pflichtfelder erforderlich)
     * @return Die persistierte Person mit generierter ID und Audit-Feldern
     * @throws IllegalArgumentException wenn Validierung fehlschlägt
     * @throws IllegalStateException wenn E-Mail oder Personalausweisnummer bereits existiert
     * @see #updatePerson(Long, Person)
     * @see #personExistsByEmail(String)
     */
    Person createPerson(Person person);

    /**
     * Aktualisiert eine bestehende Person vollständig (PUT-Semantik).
     * <p>
     * Diese Methode ersetzt alle Felder der Person mit den neuen Werten.
     * Nicht angegebene optionale Felder werden auf null gesetzt.
     * </p>
     *
     * <strong>Verhalten:</strong>
     * <ul>
     *   <li>Alle Felder werden überschrieben (Full Update)</li>
     *   <li>ID und Audit-Felder (createdBy, createdAt) bleiben unverändert</li>
     *   <li>updatedBy und updatedAt werden automatisch gesetzt</li>
     *   <li>Validierung wie bei {@link #createPerson(Person)}</li>
     * </ul>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Person existingPerson = personService.getPersonById(123L).orElseThrow();
     *
     * // Alle Felder setzen (auch die, die sich nicht ändern)
     * existingPerson.setFirstName("Maximilian"); // Geändert
     * existingPerson.setLastName("Mustermann");  // Unverändert, muss aber gesetzt werden
     * existingPerson.setEmail("new.email@example.de"); // Geändert
     *
     * Person updated = personService.updatePerson(123L, existingPerson);
     * }</pre>
     *
     * <strong>Hinweis:</strong>
     * <p>
     * Für partielle Updates (nur geänderte Felder) verwenden Sie
     * {@link #partialUpdatePerson(Long, Person)}.
     * </p>
     *
     * @param id Die ID der zu aktualisierenden Person (nicht null, muss existieren)
     * @param person Die Person mit den neuen Werten (nicht null)
     * @return Die aktualisierte Person mit neuen Audit-Feldern
     * @throws IllegalArgumentException wenn ID null ist oder Person invalid
     * @throws jakarta.persistence.EntityNotFoundException wenn Person mit ID nicht existiert
     * @see #partialUpdatePerson(Long, Person)
     * @see #createPerson(Person)
     */
    Person updatePerson(Long id, Person person);

    /**
     * Aktualisiert eine bestehende Person partiell (PATCH-Semantik).
     * <p>
     * Diese Methode aktualisiert nur die angegebenen Felder. Felder, die null sind,
     * werden nicht überschrieben. Ideal für REST PATCH-Endpunkte.
     * </p>
     *
     * <strong>Verhalten:</strong>
     * <ul>
     *   <li>Nur nicht-null Felder werden aktualisiert (Partial Update)</li>
     *   <li>Null-Werte werden ignoriert (bestehende Werte bleiben erhalten)</li>
     *   <li>ID und Audit-Felder (createdBy, createdAt) bleiben unverändert</li>
     *   <li>updatedBy und updatedAt werden automatisch gesetzt</li>
     * </ul>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * // Nur E-Mail und Telefon ändern, Rest bleibt unverändert
     * Person partial = new Person();
     * partial.setEmail("new.email@example.de");
     * partial.setPhone("030 98765432");
     * // Alle anderen Felder sind null und werden nicht geändert
     *
     * Person updated = personService.partialUpdatePerson(123L, partial);
     * // E-Mail und Phone sind neu, alle anderen Felder unverändert
     * }</pre>
     *
     * <strong>Vorsicht:</strong>
     * <p>
     * Pflichtfelder (firstName, lastName, dateOfBirth) können durch partielle
     * Updates nicht auf null gesetzt werden - solche Versuche werden ignoriert.
     * </p>
     *
     * @param id Die ID der zu aktualisierenden Person (nicht null, muss existieren)
     * @param person Die Person mit den zu ändernden Feldern (nicht-null Felder werden übernommen)
     * @return Die aktualisierte Person
     * @throws IllegalArgumentException wenn ID null ist
     * @throws jakarta.persistence.EntityNotFoundException wenn Person mit ID nicht existiert
     * @see #updatePerson(Long, Person)
     */
    Person partialUpdatePerson(Long id, Person person);

    /**
     * Löscht eine Person aus dem System (Soft-Delete).
     * <p>
     * Diese Methode führt ein Soft-Delete aus - die Person wird nicht physisch
     * aus der Datenbank gelöscht, sondern nur als gelöscht markiert (deleted = true).
     * Dies erfüllt die Anforderungen für revisionssichere Protokollierung gemäß DSGVO.
     * </p>
     *
     * <strong>Verhalten:</strong>
     * <ul>
     *   <li>Setzt das deleted-Flag auf true</li>
     *   <li>Person erscheint nicht mehr in regulären Suchanfragen</li>
     *   <li>Datensatz bleibt physisch in der Datenbank (Audit-Trail)</li>
     *   <li>updatedBy und updatedAt werden gesetzt</li>
     * </ul>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * personService.deletePerson(123L);
     *
     * // Person ist nun "gelöscht"
     * Optional<Person> deleted = personService.getPersonById(123L);
     * // deleted.isEmpty() == true (durch @Where(clause = "deleted = false"))
     * }</pre>
     *
     * <strong>DSGVO-Recht auf Vergessenwerden:</strong>
     * <p>
     * Für echtes Löschen gemäß DSGVO Art. 17 (Recht auf Vergessenwerden) muss
     * ein separater Prozess implementiert werden, der personenbezogene Daten
     * anonymisiert oder physisch löscht.
     * </p>
     *
     * <strong>Wiederherstellung:</strong>
     * <p>
     * Soft-gelöschte Personen können durch direkten Datenbankzugriff oder
     * spezielle Admin-Funktionen wiederhergestellt werden (deleted = false).
     * </p>
     *
     * @param id Die ID der zu löschenden Person (nicht null, muss existieren)
     * @throws IllegalArgumentException wenn ID null ist
     * @throws jakarta.persistence.EntityNotFoundException wenn Person mit ID nicht existiert
     * @see Person#isDeleted()
     */
    void deletePerson(Long id);

    // ==================== READ OPERATIONS ====================

    /**
     * Sucht eine Person anhand ihrer ID.
     * <p>
     * Gibt die Person zurück, wenn sie existiert und nicht gelöscht ist.
     * Durch {@link Person}'s {@code @Where(clause = "deleted = false")} werden
     * gelöschte Personen automatisch ausgeschlossen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Optional<Person> person = personService.getPersonById(123L);
     *
     * person.ifPresentOrElse(
     *     p -> System.out.println("Gefunden: " + p.getFullName()),
     *     () -> System.out.println("Person nicht gefunden oder gelöscht")
     * );
     * }</pre>
     *
     * @param id Die ID der gesuchten Person (nicht null)
     * @return {@link Optional} mit gefundener Person oder {@link Optional#empty()} wenn nicht gefunden
     * @throws IllegalArgumentException wenn ID null ist
     * @see #getPersonByEmail(String)
     */
    Optional<Person> getPersonById(Long id);

    /**
     * Sucht eine Person anhand ihrer E-Mail-Adresse.
     * <p>
     * E-Mail-Adressen sind eindeutig im System. Diese Methode wird typischerweise
     * für Login-Prozesse oder zur Vermeidung von Duplikaten verwendet.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Optional<Person> person = personService.getPersonByEmail("max.mustermann@example.de");
     *
     * if (person.isPresent()) {
     *     System.out.println("Person gefunden: " + person.get().getFullName());
     * } else {
     *     System.out.println("Keine Person mit dieser E-Mail registriert");
     * }
     * }</pre>
     *
     * @param email Die E-Mail-Adresse der gesuchten Person (nicht null, nicht leer)
     * @return {@link Optional} mit gefundener Person oder {@link Optional#empty()} wenn nicht gefunden
     * @throws IllegalArgumentException wenn E-Mail null oder leer ist
     * @see #personExistsByEmail(String)
     * @see #getPersonById(Long)
     */
    Optional<Person> getPersonByEmail(String email);

    /**
     * Gibt alle Personen mit Paginierung zurück.
     * <p>
     * Diese Methode unterstützt Sortierung und Paginierung für effiziente
     * Darstellung großer Datenmengen in Benutzeroberflächen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * // Seite 1 (0-basiert), 20 Einträge, sortiert nach Nachname
     * Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName").ascending());
     * Page<Person> page = personService.getAllPersons(pageable);
     *
     * System.out.println("Gesamt: " + page.getTotalElements());
     * System.out.println("Seiten: " + page.getTotalPages());
     *
     * page.getContent().forEach(p -> System.out.println(p.getFullName()));
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Nutzt datenbankbasierte Paginierung (LIMIT/OFFSET) für effiziente Abfragen
     * auch bei Millionen von Datensätzen.
     * </p>
     *
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Liste aller Personen (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn Pageable null ist
     */
    Page<Person> getAllPersons(Pageable pageable);

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Sucht Personen nach Namen (Vor- oder Nachname).
     * <p>
     * Durchsucht sowohl Vornamen als auch Nachnamen nach dem angegebenen Suchbegriff.
     * Die Suche ist case-insensitive und unterstützt Teilstring-Matching.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * // Findet "Max Mustermann", "Maximilian Meyer", "Erika Maxima"
     * List<Person> results = personService.searchPersonsByName("max");
     *
     * results.forEach(p -> System.out.println(p.getFullName()));
     * }</pre>
     *
     * <strong>Hinweis:</strong>
     * <p>
     * Diese Methode delegiert intern an das Repository. Für umfassendere Suchen
     * über mehrere Felder verwenden Sie {@link #searchPersonsByAnyField(String, Pageable)}.
     * </p>
     *
     * @param name Der Suchbegriff für Vor- oder Nachname (nicht null, nicht leer)
     * @return Liste aller passenden Personen (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn Name null oder leer ist
     * @see #findPersonsByLastName(String)
     * @see #searchPersonsByAnyField(String, Pageable)
     */
    List<Person> searchPersonsByName(String name);

    /**
     * Sucht Personen nach exaktem Nachnamen.
     * <p>
     * Findet alle Personen, deren Nachname exakt übereinstimmt (case-sensitive).
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * List<Person> mustermanns = personService.findPersonsByLastName("Mustermann");
     * System.out.println("Gefunden: " + mustermanns.size() + " Personen");
     * }</pre>
     *
     * @param lastName Der gesuchte Nachname (exakte Übereinstimmung, nicht null)
     * @return Liste aller Personen mit diesem Nachnamen (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn lastName null ist
     * @see #searchPersonsByName(String)
     */
    List<Person> findPersonsByLastName(String lastName);

    /**
     * Sucht Personen nach Stadt.
     * <p>
     * Findet alle Personen, die in der angegebenen Stadt wohnen (exakte Übereinstimmung).
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * List<Person> berliner = personService.findPersonsByCity("Berlin");
     * System.out.println("Einwohner in Berlin: " + berliner.size());
     * }</pre>
     *
     * @param city Die gesuchte Stadt (exakte Übereinstimmung, nicht null)
     * @return Liste aller Personen aus dieser Stadt (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn city null ist
     * @see #countPersonsByCity(String)
     */
    List<Person> findPersonsByCity(String city);

    /**
     * Sucht Personen nach exaktem Geburtsdatum.
     * <p>
     * Findet alle Personen, die an einem bestimmten Datum geboren wurden.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * LocalDate silvester2000 = LocalDate.of(2000, 12, 31);
     * List<Person> millenniumBabies = personService.findPersonsByDateOfBirth(silvester2000);
     * }</pre>
     *
     * @param dateOfBirth Das gesuchte Geburtsdatum (nicht null)
     * @return Liste aller Personen mit diesem Geburtsdatum (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn dateOfBirth null ist
     * @see #findPersonsByBirthDateRange(LocalDate, LocalDate)
     */
    List<Person> findPersonsByDateOfBirth(LocalDate dateOfBirth);

    /**
     * Sucht Personen nach Geburtsdatum-Bereich.
     * <p>
     * Findet alle Personen, die zwischen zwei Datums (inklusive) geboren wurden.
     * Nützlich für Altersgruppenanalysen und statistische Auswertungen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * // Alle in den 1990er Jahren Geborenen
     * LocalDate start = LocalDate.of(1990, 1, 1);
     * LocalDate end = LocalDate.of(1999, 12, 31);
     * List<Person> nineties = personService.findPersonsByBirthDateRange(start, end);
     *
     * System.out.println("90er-Generation: " + nineties.size() + " Personen");
     * }</pre>
     *
     * @param startDate Startdatum des Bereichs (inklusive, nicht null)
     * @param endDate Enddatum des Bereichs (inklusive, nicht null)
     * @return Liste aller Personen im Geburtsdatum-Bereich (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn startDate oder endDate null ist
     * @throws IllegalArgumentException wenn startDate nach endDate liegt
     * @see #findPersonsByDateOfBirth(LocalDate)
     */
    List<Person> findPersonsByBirthDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Durchsucht Personen über mehrere Felder hinweg mit Paginierung.
     * <p>
     * Führt eine globale Suche über Vorname, Nachname und E-Mail durch.
     * Ideal für Suchfelder in Benutzeroberflächen, wo der Nutzer nicht weiß,
     * in welchem Feld sich die gesuchte Information befindet.
     * </p>
     *
     * <strong>Durchsuchte Felder:</strong>
     * <ul>
     *   <li>Vorname (firstName) - case-insensitive, Teilstring</li>
     *   <li>Nachname (lastName) - case-insensitive, Teilstring</li>
     *   <li>E-Mail (email) - case-insensitive, Teilstring</li>
     * </ul>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Pageable pageable = PageRequest.of(0, 10, Sort.by("lastName"));
     * Page<Person> results = personService.searchPersonsByAnyField("max", pageable);
     *
     * // Findet:
     * // - Max Mustermann (firstName)
     * // - Erika Maxima (firstName)
     * // - max.mueller@example.de (email)
     *
     * System.out.println("Gefunden: " + results.getTotalElements());
     * }</pre>
     *
     * <strong>Performance-Hinweis:</strong>
     * <p>
     * Bei sehr großen Datenmenken (>100.000 Datensätze) kann diese Suche langsam werden.
     * Für produktive Systeme mit Millionen von Datensätzen sollte eine dedizierte
     * Volltextsuchmaschine (z.B. Elasticsearch) verwendet werden.
     * </p>
     *
     * @param searchTerm Der Suchbegriff (wird in allen Feldern gesucht, nicht null)
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Suchergebnisse (nie null, kann leer sein)
     * @throws IllegalArgumentException wenn searchTerm oder pageable null ist
     * @see #searchPersonsByName(String)
     */
    Page<Person> searchPersonsByAnyField(String searchTerm, Pageable pageable);

    // ==================== UTILITY OPERATIONS ====================

    /**
     * Zählt die Anzahl der Personen in einer bestimmten Stadt.
     * <p>
     * Diese Methode ist effizienter als {@link #findPersonsByCity(String)}, wenn nur
     * die Anzahl benötigt wird, nicht die tatsächlichen Datensätze.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * long einwohnerBerlin = personService.countPersonsByCity("Berlin");
     * long einwohnerMuenchen = personService.countPersonsByCity("München");
     *
     * System.out.println("Berlin: " + einwohnerBerlin + " Personen");
     * System.out.println("München: " + einwohnerMuenchen + " Personen");
     * }</pre>
     *
     * @param city Die Stadt, für die gezählt werden soll (nicht null)
     * @return Anzahl der Personen in dieser Stadt (0 wenn keine gefunden)
     * @throws IllegalArgumentException wenn city null ist
     * @see #findPersonsByCity(String)
     */
    long countPersonsByCity(String city);

    /**
     * Prüft, ob eine Person mit der angegebenen E-Mail-Adresse existiert.
     * <p>
     * Diese Methode ist effizienter als {@link #getPersonByEmail(String)}, wenn nur
     * geprüft werden soll, ob ein Datensatz existiert. Typisch für Duplikatsprüfungen
     * bei Registrierungsprozessen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * String email = "max.mustermann@example.de";
     * if (personService.personExistsByEmail(email)) {
     *     throw new DuplicateEmailException("E-Mail bereits registriert");
     * }
     *
     * // E-Mail ist frei, Registrierung fortsetzen
     * Person newPerson = Person.builder()
     *     .email(email)
     *     .firstName("Max")
     *     .lastName("Mustermann")
     *     .build();
     * personService.createPerson(newPerson);
     * }</pre>
     *
     * @param email Die zu prüfende E-Mail-Adresse (nicht null)
     * @return {@code true} wenn eine Person mit dieser E-Mail existiert, sonst {@code false}
     * @throws IllegalArgumentException wenn email null ist
     * @see #getPersonByEmail(String)
     * @see #existsByNationalIdNumber(String)
     */
    boolean personExistsByEmail(String email);

    /**
     * Prüft, ob eine Person mit der angegebenen Personalausweisnummer existiert.
     * <p>
     * Diese Methode ist effizienter als Repository-Lookup, wenn nur geprüft werden soll,
     * ob ein Datensatz existiert. Wichtig für Duplikatsprüfungen bei behördlichen
     * Registrierungsprozessen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * String nationalId = "T22000129";
     * if (personService.existsByNationalIdNumber(nationalId)) {
     *     throw new DuplicateNationalIdException("Personalausweisnummer bereits erfasst");
     * }
     *
     * // Personalausweisnummer ist frei, Erfassung fortsetzen
     * }</pre>
     *
     * <strong>DSGVO-Hinweis:</strong>
     * <p>
     * Personalausweisnummern sind besonders schützenswerte Daten. Die Prüfung sollte
     * nur für autorisierte Prozesse zugänglich sein und entsprechend geloggt werden.
     * </p>
     *
     * @param nationalIdNumber Die zu prüfende Personalausweisnummer (nicht null)
     * @return {@code true} wenn eine Person mit dieser Nummer existiert, sonst {@code false}
     * @throws IllegalArgumentException wenn nationalIdNumber null ist
     * @see #personExistsByEmail(String)
     */
    boolean existsByNationalIdNumber(String nationalIdNumber);
}