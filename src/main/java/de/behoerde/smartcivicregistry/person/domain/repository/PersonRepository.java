package de.behoerde.smartcivicregistry.person.domain.repository;

import de.behoerde.smartcivicregistry.person.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für den Datenzugriff auf {@link Person}-Entitäten.
 * <p>
 * Dieses Repository-Interface folgt dem Ports-Pattern der hexagonalen Architektur
 * und definiert die Datenzugriffsschicht (Port) für Personen-Stammdaten. Es erweitert
 * {@link JpaRepository} und bietet sowohl Standard-CRUD-Operationen als auch
 * spezialisierte Suchmethoden für Personen.
 * </p>
 *
 * <strong>Hexagonale Architektur (Ports & Adapters):</strong>
 * <ul>
 *   <li><strong>Port:</strong> Dieses Interface definiert den Zugriff auf Personen-Daten</li>
 *   <li><strong>Adapter:</strong> Spring Data JPA implementiert automatisch die Methoden</li>
 *   <li><strong>Domain Layer:</strong> Liegt im domain.repository Package</li>
 * </ul>
 *
 * <strong>Automatische Implementierung:</strong>
 * <p>
 * Spring Data JPA generiert automatisch die Implementierung zur Laufzeit basierend auf:
 * </p>
 * <ul>
 *   <li>Methodennamen-Konventionen (Query Derivation)</li>
 *   <li>JPQL-Queries (@Query-Annotation)</li>
 *   <li>Standard-CRUD-Operationen (von JpaRepository geerbt)</li>
 * </ul>
 *
 * <strong>Geerbte Standard-Operationen:</strong>
 * <p>
 * Von {@link JpaRepository} geerbt (Auswahl):
 * </p>
 * <ul>
 *   <li>{@code save(Person)}: Speichert oder aktualisiert eine Person</li>
 *   <li>{@code findById(Long)}: Sucht Person nach ID</li>
 *   <li>{@code findAll()}: Gibt alle Personen zurück</li>
 *   <li>{@code deleteById(Long)}: Löscht Person nach ID (Soft-Delete via @SQLDelete)</li>
 *   <li>{@code count()}: Zählt alle Personen</li>
 * </ul>
 *
 * <strong>Soft-Delete-Verhalten:</strong>
 * <p>
 * Durch {@link Person}'s {@code @SQLDelete} und {@code @Where}-Annotationen werden
 * gelöschte Personen ({@code deleted = true}) automatisch aus Suchergebnissen ausgeschlossen.
 * </p>
 *
 * <strong>Performance-Optimierung:</strong>
 * <p>
 * Die Entity {@link Person} definiert Indizes auf {@code last_name}, {@code date_of_birth}
 * und {@code city} für optimierte Abfragen.
 * </p>
 *
 * @author Smart Civic Registry Team
 * @version 1.0
 * @since Phase 2 (Person Domain Module)
 * @see Person
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see de.behoerde.smartcivicregistry.person.application.service.PersonService
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    // ==================== EINDEUTIGKEITS-SUCHEN ====================

    /**
     * Sucht eine Person anhand ihrer E-Mail-Adresse.
     * <p>
     * E-Mail-Adressen sind in der Datenbank eindeutig ({@code unique = true} in {@link Person}).
     * Diese Methode wird typischerweise für Login-Prozesse oder Duplikatsprüfungen verwendet.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Optional<Person> person = personRepository.findByEmail("max.mustermann@example.de");
     * if (person.isPresent()) {
     *     System.out.println("Person gefunden: " + person.get().getFullName());
     * }
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Effizient durch Unique-Constraint auf email-Spalte (impliziter Index).
     * </p>
     *
     * @param email Die E-Mail-Adresse der gesuchten Person (nicht null)
     * @return {@link Optional} mit gefundener Person oder {@link Optional#empty()} wenn nicht gefunden
     * @see #existsByEmail(String)
     */
    Optional<Person> findByEmail(String email);

    /**
     * Sucht eine Person anhand ihrer Personalausweisnummer.
     * <p>
     * Personalausweisnummern (National ID) sind in der Datenbank eindeutig
     * ({@code unique = true} in {@link Person}). Diese Methode wird für behördliche
     * Identifikationsprozesse und Abgleiche mit anderen Registern verwendet.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Optional<Person> person = personRepository.findByNationalIdNumber("T22000129");
     * person.ifPresent(p -> System.out.println("Gefunden: " + p.getOfficialName()));
     * }</pre>
     *
     * <strong>DSGVO-Hinweis:</strong>
     * <p>
     * Personalausweisnummern sind besonders schützenswerte Daten. Der Zugriff sollte
     * nur für autorisierte Benutzer mit entsprechenden Berechtigungen möglich sein.
     * </p>
     *
     * <strong>Performance:</strong>
     * <p>
     * Effizient durch Unique-Constraint auf national_id_number-Spalte (impliziter Index).
     * </p>
     *
     * @param nationalIdNumber Die Personalausweisnummer der gesuchten Person (nicht null)
     * @return {@link Optional} mit gefundener Person oder {@link Optional#empty()} wenn nicht gefunden
     * @see #existsByNationalIdNumber(String)
     */
    Optional<Person> findByNationalIdNumber(String nationalIdNumber);

    // ==================== LISTEN-SUCHEN ====================

    /**
     * Sucht alle Personen mit einem bestimmten Nachnamen.
     * <p>
     * Gibt eine Liste aller Personen zurück, deren Nachname exakt übereinstimmt (case-sensitive).
     * Diese Methode ist nützlich für Namenssuchen in Behördenanwendungen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * List<Person> musterleute = personRepository.findByLastName("Mustermann");
     * System.out.println("Gefunden: " + musterleute.size() + " Personen");
     * }</pre>
     *
     * <strong>Hinweis:</strong>
     * <p>
     * Für case-insensitive und Teilstring-Suchen verwenden Sie
     * {@link #findByLastNameContainingIgnoreCase(String, Pageable)}.
     * </p>
     *
     * <strong>Performance:</strong>
     * <p>
     * Optimiert durch Index auf last_name-Spalte ({@code idx_person_last_name}).
     * </p>
     *
     * @param lastName Der gesuchte Nachname (exakte Übereinstimmung, nicht null)
     * @return Liste aller Personen mit diesem Nachnamen (nie null, kann leer sein)
     * @see #findByLastNameContainingIgnoreCase(String, Pageable)
     */
    List<Person> findByLastName(String lastName);

    /**
     * Sucht alle Personen mit einem bestimmten Geburtsdatum.
     * <p>
     * Gibt alle Personen zurück, die am angegebenen Datum geboren wurden.
     * Nützlich für Geburtstagslisten oder statistische Auswertungen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * LocalDate silvester2000 = LocalDate.of(2000, 12, 31);
     * List<Person> millenniumBabies = personRepository.findByDateOfBirth(silvester2000);
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Optimiert durch Index auf date_of_birth-Spalte ({@code idx_person_date_of_birth}).
     * </p>
     *
     * @param dateOfBirth Das gesuchte Geburtsdatum (nicht null)
     * @return Liste aller Personen mit diesem Geburtsdatum (nie null, kann leer sein)
     * @see #findByBirthDateRange(LocalDate, LocalDate)
     */
    List<Person> findByDateOfBirth(LocalDate dateOfBirth);

    /**
     * Sucht alle Personen, die in einer bestimmten Stadt wohnen.
     * <p>
     * Gibt alle Personen zurück, deren Wohnort (city-Feld) exakt übereinstimmt.
     * Nützlich für geografische Auswertungen und lokale Behördenanfragen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * List<Person> berliner = personRepository.findByCity("Berlin");
     * System.out.println("Einwohner in Berlin: " + berliner.size());
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Optimiert durch Index auf city-Spalte ({@code idx_person_city}).
     * </p>
     *
     * @param city Der gesuchte Wohnort (exakte Übereinstimmung, nicht null)
     * @return Liste aller Personen aus dieser Stadt (nie null, kann leer sein)
     * @see #countByCity(String)
     */
    List<Person> findByCity(String city);

    // ==================== PAGINIERTE SUCHEN ====================

    /**
     * Sucht Personen nach Nachname mit Teilstring-Matching und Paginierung.
     * <p>
     * Findet alle Personen, deren Nachname den Suchbegriff enthält (case-insensitive).
     * Die Ergebnisse werden paginiert zurückgegeben, um große Ergebnismengen
     * performant zu verarbeiten.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName").ascending());
     * Page<Person> result = personRepository.findByLastNameContainingIgnoreCase("muster", pageable);
     *
     * System.out.println("Gefunden: " + result.getTotalElements() + " Personen");
     * System.out.println("Seite " + (result.getNumber() + 1) + " von " + result.getTotalPages());
     *
     * result.getContent().forEach(p -> System.out.println(p.getFullName()));
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Nutzt Index auf last_name. Paginierung verhindert Speicherprobleme bei großen Ergebnismengen.
     * </p>
     *
     * <strong>Suchverhalten:</strong>
     * <ul>
     *   <li>Case-insensitive: "Muster" findet "Mustermann", "MUSTER", "muster"</li>
     *   <li>Teilstring: "mann" findet "Mustermann", "Herrmann", "Neumann"</li>
     * </ul>
     *
     * @param lastName Der Suchbegriff für den Nachnamen (nicht null)
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Ergebnisse (nie null, kann leer sein)
     * @see #searchByAnyField(String, Pageable)
     */
    Page<Person> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    // ==================== KOMPLEXE QUERIES ====================

    /**
     * Durchsucht Personen über mehrere Felder hinweg (Volltextsuche).
     * <p>
     * Diese Methode führt eine globale Suche über Vorname, Nachname und E-Mail durch.
     * Ideal für Suchfelder in Benutzeroberflächen, in denen der Nutzer nicht
     * weiß, in welchem Feld sich die gesuchte Information befindet.
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
     * Pageable pageable = PageRequest.of(0, 10);
     * Page<Person> result = personRepository.searchByAnyField("max", pageable);
     *
     * // Findet:
     * // - Max Mustermann (firstName)
     * // - Erika Maxima (firstName)
     * // - max.mueller@example.de (email)
     * }</pre>
     *
     * <strong>JPQL-Query:</strong>
     * <pre>{@code
     * SELECT p FROM Person p WHERE
     *   LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
     *   LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
     *   LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
     * }</pre>
     *
     * <strong>Performance-Hinweis:</strong>
     * <p>
     * Diese Methode verwendet OR-Verknüpfungen und LIKE-Operatoren, was bei
     * großen Datenmengen langsam sein kann. Für produktive Anwendungen mit
     * Millionen von Datensätzen sollte eine Volltextsuchmaschine (z.B. Elasticsearch)
     * in Betracht gezogen werden.
     * </p>
     *
     * @param searchTerm Der Suchbegriff (wird in allen durchsuchten Feldern gesucht, nicht null)
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Suchergebnisse (nie null, kann leer sein)
     */
    @Query("SELECT p FROM Person p WHERE " +
            "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Person> searchByAnyField(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Sucht Personen nach einem Geburtsdatum-Bereich.
     * <p>
     * Findet alle Personen, die zwischen zwei Datums (inklusive) geboren wurden.
     * Nützlich für Altersgruppenanalysen, Geburtsjahrgänge oder statistische Auswertungen.
     * </p>
     *
     * <strong>Verwendungsbeispiele:</strong>
     * <pre>{@code
     * // Alle in den 1980er Jahren Geborenen
     * LocalDate start = LocalDate.of(1980, 1, 1);
     * LocalDate end = LocalDate.of(1989, 12, 31);
     * List<Person> eighties = personRepository.findByBirthDateRange(start, end);
     *
     * // Alle zwischen 18 und 65 Jahren (für Volljährige nicht-Senioren)
     * LocalDate now = LocalDate.now();
     * LocalDate vor18Jahren = now.minusYears(18);
     * LocalDate vor65Jahren = now.minusYears(65);
     * List<Person> arbeitsalter = personRepository.findByBirthDateRange(vor65Jahren, vor18Jahren);
     * }</pre>
     *
     * <strong>JPQL-Query:</strong>
     * <pre>{@code
     * SELECT p FROM Person p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Optimiert durch Index auf date_of_birth-Spalte ({@code idx_person_date_of_birth}).
     * BETWEEN-Operator ist effizienter als zwei separate Vergleiche.
     * </p>
     *
     * @param startDate Startdatum des Bereichs (inklusive, nicht null)
     * @param endDate Enddatum des Bereichs (inklusive, nicht null)
     * @return Liste aller Personen im angegebenen Geburtsdatum-Bereich (nie null, kann leer sein)
     * @see #findByDateOfBirth(LocalDate)
     */
    @Query("SELECT p FROM Person p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Person> findByBirthDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * Zählt die Anzahl der Personen in einer bestimmten Stadt.
     * <p>
     * Diese Methode ist effizienter als {@link #findByCity(String)}, wenn nur die
     * Anzahl benötigt wird, nicht die tatsächlichen Datensätze. Nützlich für
     * Statistiken und Dashboards.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * long einwohnerBerlin = personRepository.countByCity("Berlin");
     * long einwohnerMuenchen = personRepository.countByCity("München");
     *
     * System.out.println("Berlin: " + einwohnerBerlin + " Personen");
     * System.out.println("München: " + einwohnerMuenchen + " Personen");
     * }</pre>
     *
     * <strong>JPQL-Query:</strong>
     * <pre>{@code
     * SELECT COUNT(p) FROM Person p WHERE p.city = :city
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Sehr effizient, da nur Zählung ohne Datenübertragung. Nutzt Index auf city-Spalte.
     * </p>
     *
     * @param city Die Stadt, für die gezählt werden soll (nicht null)
     * @return Anzahl der Personen in dieser Stadt (0 wenn keine gefunden)
     * @see #findByCity(String)
     */
    @Query("SELECT COUNT(p) FROM Person p WHERE p.city = :city")
    long countByCity(@Param("city") String city);

    // ==================== EXISTENZPRÜFUNGEN ====================

    /**
     * Prüft, ob eine Person mit der angegebenen E-Mail-Adresse existiert.
     * <p>
     * Diese Methode ist effizienter als {@link #findByEmail(String)}, wenn nur
     * geprüft werden soll, ob ein Datensatz existiert. Typischerweise verwendet
     * für Duplikatsprüfungen bei Registrierungsprozessen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * String email = "max.mustermann@example.de";
     * if (personRepository.existsByEmail(email)) {
     *     throw new DuplicateEmailException("E-Mail bereits registriert");
     * }
     * }</pre>
     *
     * <strong>Performance:</strong>
     * <p>
     * Sehr effizient, da nur boolean-Rückgabe ohne Datenübertragung.
     * Nutzt Unique-Index auf email-Spalte.
     * </p>
     *
     * @param email Die zu prüfende E-Mail-Adresse (nicht null)
     * @return {@code true} wenn eine Person mit dieser E-Mail existiert, sonst {@code false}
     * @see #findByEmail(String)
     */
    boolean existsByEmail(String email);

    /**
     * Prüft, ob eine Person mit der angegebenen Personalausweisnummer existiert.
     * <p>
     * Diese Methode ist effizienter als {@link #findByNationalIdNumber(String)}, wenn nur
     * geprüft werden soll, ob ein Datensatz existiert. Wichtig für Duplikatsprüfungen
     * bei behördlichen Registrierungsprozessen.
     * </p>
     *
     * <strong>Verwendungsbeispiel:</strong>
     * <pre>{@code
     * String nationalId = "T22000129";
     * if (personRepository.existsByNationalIdNumber(nationalId)) {
     *     throw new DuplicateNationalIdException("Personalausweisnummer bereits erfasst");
     * }
     * }</pre>
     *
     * <strong>DSGVO-Hinweis:</strong>
     * <p>
     * Personalausweisnummern sind besonders schützenswerte Daten. Die Prüfung sollte
     * nur für autorisierte Prozesse zugänglich sein.
     * </p>
     *
     * <strong>Performance:</strong>
     * <p>
     * Sehr effizient, da nur boolean-Rückgabe ohne Datenübertragung.
     * Nutzt Unique-Index auf national_id_number-Spalte.
     * </p>
     *
     * @param nationalIdNumber Die zu prüfende Personalausweisnummer (nicht null)
     * @return {@code true} wenn eine Person mit dieser Nummer existiert, sonst {@code false}
     * @see #findByNationalIdNumber(String)
     */
    boolean existsByNationalIdNumber(String nationalIdNumber);

    // Nur für Admin: alle Personen, inkl. gelöschte
    @Query(value = "SELECT * FROM persons", nativeQuery = true)
    Page<Person> findAllIncludingDeleted(Pageable pageable);
}