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
 * Repository-Interface fuer den Datenzugriff auf Person-Entitaeten.

 * Dieses Repository-Interface folgt dem Ports-Pattern der hexagonalen Architektur
 * und definiert die Datenzugriffsschicht (Port) fuer Personen-Stammdaten. Es erweitert
 * JpaRepository und bietet sowohl Standard-CRUD-Operationen als auch
 * spezialisierte Suchmethoden fuer Personen.

 * Hexagonale Architektur (Ports & Adapters):
 * - Port: Dieses Interface definiert den Zugriff auf Personen-Daten
 * - Adapter: Spring Data JPA implementiert automatisch die Methoden
 * - Domain Layer: Liegt im domain.repository Package

 * Automatische Implementierung:
 * Spring Data JPA generiert automatisch die Implementierung zur Laufzeit basierend auf:
 * - Methodennamen-Konventionen (Query Derivation)
 * - JPQL-Queries (@Query-Annotation)
 * - Standard-CRUD-Operationen (von JpaRepository geerbt)

 * Geerbte Standard-Operationen:
 * Von JpaRepository geerbt (Auswahl):
 * - save(Person): Speichert oder aktualisiert eine Person
 * - findById(Long): Sucht Person nach ID
 * - findAll(): Gibt alle Personen zurueck
 * - deleteById(Long): Loescht Person nach ID (Soft-Delete via @SQLDelete)
 * - count(): Zaehlt alle Personen

 * Soft-Delete-Verhalten:
 * Durch Person's @SQLDelete und @Where-Annotationen werden
 * geloeschte Personen (deleted = true) automatisch aus Suchergebnissen ausgeschlossen.

 * Performance-Optimierung:
 * Die Entity Person definiert Indizes auf last_name, date_of_birth
 * und city fuer optimierte Abfragen.

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

     * E-Mail-Adressen sind in der Datenbank eindeutig (unique = true in Person).
     * Diese Methode wird typischerweise fuer Login-Prozesse oder Duplikatspruefungen verwendet.

     * Verwendungsbeispiel:
     * Optional<Person> person = personRepository.findByEmail("max.mustermann@example.de");
     * if (person.isPresent()) {
     *     System.out.println("Person gefunden: " + person.get().getFullName());
     * }

     * Performance:
     * Effizient durch Unique-Constraint auf email-Spalte (impliziter Index).
     *
     * @param email Die E-Mail-Adresse der gesuchten Person (nicht null)
     * @return Optional mit gefundener Person oder Optional.empty() wenn nicht gefunden
     * @see #existsByEmail(String)
     */
    Optional<Person> findByEmail(String email);

    /**
     * Sucht eine Person anhand ihrer Personalausweisnummer.

     * Personalausweisnummern (National ID) sind in der Datenbank eindeutig
     * (unique = true in Person). Diese Methode wird fuer behoerdliche
     * Identifikationsprozesse und Abgleiche mit anderen Registern verwendet.

     * Verwendungsbeispiel:
     * Optional<Person> person = personRepository.findByNationalIdNumber("T22000129");
     * person.ifPresent(p -> System.out.println("Gefunden: " + p.getOfficialName()));

     * DSGVO-Hinweis:
     * Personalausweisnummern sind besonders schuetzenswerte Daten. Der Zugriff sollte
     * nur fuer autorisierte Benutzer mit entsprechenden Berechtigungen moeglich sein.

     * Performance:
     * Effizient durch Unique-Constraint auf national_id_number-Spalte (impliziter Index).

     * @param nationalIdNumber Die Personalausweisnummer der gesuchten Person (nicht null)
     * @return Optional mit gefundener Person oder Optional.empty() wenn nicht gefunden
     * @see #existsByNationalIdNumber(String)
     */
    Optional<Person> findByNationalIdNumber(String nationalIdNumber);

    // ==================== LISTEN-SUCHEN ====================

    /**
     * Sucht alle Personen mit einem bestimmten Nachnamen.

     * Gibt eine Liste aller Personen zurueck, deren Nachname exakt uebereinstimmt (case-sensitive).
     * Diese Methode ist nuetzlich fuer Namenssuchen in Behoerdenanwendungen.

     * Verwendungsbeispiel:
     * List<Person> musterleute = personRepository.findByLastName("Mustermann");
     * System.out.println("Gefunden: " + musterleute.size() + " Personen");

     * Hinweis:
     * Fuer case-insensitive und Teilstring-Suchen verwenden Sie
     * findByLastNameContainingIgnoreCase(String, Pageable).

     * Performance:
     * Optimiert durch Index auf last_name-Spalte (idx_person_last_name).

     * @param lastName Der gesuchte Nachname (exakte Uebereinstimmung, nicht null)
     * @return Liste aller Personen mit diesem Nachnamen (nie null, kann leer sein)
     * @see #findByLastNameContainingIgnoreCase(String, Pageable)
     */
    List<Person> findByLastName(String lastName);

    /**
     * Sucht alle Personen mit einem bestimmten Geburtsdatum.

     * Gibt alle Personen zurueck, die am angegebenen Datum geboren wurden.
     * Nuetzlich fuer Geburtstagslisten oder statistische Auswertungen.

     * Verwendungsbeispiel:
     * LocalDate silvester2000 = LocalDate.of(2000, 12, 31);
     * List<Person> millenniumBabies = personRepository.findByDateOfBirth(silvester2000);

     * Performance:
     * Optimiert durch Index auf date_of_birth-Spalte (idx_person_date_of_birth).

     * @param dateOfBirth Das gesuchte Geburtsdatum (nicht null)
     * @return Liste aller Personen mit diesem Geburtsdatum (nie null, kann leer sein)
     * @see #findByBirthDateRange(LocalDate, LocalDate)
     */
    List<Person> findByDateOfBirth(LocalDate dateOfBirth);

    /**
     * Sucht alle Personen, die in einer bestimmten Stadt wohnen.

     * Gibt alle Personen zurueck, deren Wohnort (city-Feld) exakt uebereinstimmt.
     * Nuetzlich fuer geografische Auswertungen und lokale Behoerdenanfragen.

     * Verwendungsbeispiel:
     * List<Person> berliner = personRepository.findByCity("Berlin");
     * System.out.println("Einwohner in Berlin: " + berliner.size());

     * Performance:
     * Optimiert durch Index auf city-Spalte (idx_person_city).

     * @param city Der gesuchte Wohnort (exakte Uebereinstimmung, nicht null)
     * @return Liste aller Personen aus dieser Stadt (nie null, kann leer sein)
     * @see #countByCity(String)
     */
    List<Person> findByCity(String city);

    // ==================== PAGINIERTE SUCHEN ====================

    /**
     * Sucht Personen nach Nachname mit Teilstring-Matching und Paginierung.

     * Findet alle Personen, deren Nachname den Suchbegriff enthaelt (case-insensitive).
     * Die Ergebnisse werden paginiert zurueckgegeben, um grosse Ergebnismengen
     * performant zu verarbeiten.

     * Verwendungsbeispiel:
     * Pageable pageable = PageRequest.of(0, 20, Sort.by("lastName").ascending());
     * Page<Person> result = personRepository.findByLastNameContainingIgnoreCase("muster", pageable);

     * System.out.println("Gefunden: " + result.getTotalElements() + " Personen");
     * System.out.println("Seite " + (result.getNumber() + 1) + " von " + result.getTotalPages());

     * result.getContent().forEach(p -> System.out.println(p.getFullName()));

     * Performance:
     * Nutzt Index auf last_name. Paginierung verhindert Speicherprobleme bei grossen Ergebnismengen.

     * Suchverhalten:
     * - Case-insensitive: "Muster" findet "Mustermann", "MUSTER", "muster"
     * - Teilstring: "mann" findet "Mustermann", "Herrmann", "Neumann"

     * @param lastName Der Suchbegriff fuer den Nachnamen (nicht null)
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Ergebnisse (nie null, kann leer sein)
     * @see #searchByAnyField(String, Pageable)
     */
    Page<Person> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    // ==================== KOMPLEXE QUERIES ====================

    /**
     * Durchsucht Personen ueber mehrere Felder hinweg (Volltextsuche).

     * Diese Methode fuehrt eine globale Suche ueber Vorname, Nachname und E-Mail durch.
     * Ideal fuer Suchfelder in Benutzeroberflaechen, in denen der Nutzer nicht
     * weiss, in welchem Feld sich die gesuchte Information befindet.

     * Durchsuchte Felder:
     * - Vorname (firstName) - case-insensitive, Teilstring
     * - Nachname (lastName) - case-insensitive, Teilstring
     * - E-Mail (email) - case-insensitive, Teilstring

     * Verwendungsbeispiel:
     * Pageable pageable = PageRequest.of(0, 10);
     * Page<Person> result = personRepository.searchByAnyField("max", pageable);

     * Findet:
     * - Max Mustermann (firstName)
     * - Erika Maxima (firstName)
     * - max.mueller@example.de (email)

     * JPQL-Query:
     * SELECT p FROM Person p WHERE
     *   LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
     *   LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
     *   LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))

     * Performance-Hinweis:
     * Diese Methode verwendet OR-Verknuepfungen und LIKE-Operatoren, was bei
     * grossen Datenmengen langsam sein kann. Fuer produktive Anwendungen mit
     * Millionen von Datensaetzen sollte eine Volltextsuchmaschine (z.B. Elasticsearch)
     * in Betracht gezogen werden.

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

     * Findet alle Personen, die zwischen zwei Daten (inklusive) geboren wurden.
     * Nuetzlich fuer Altersgruppenanalysen, Geburtsjahre oder statistische Auswertungen.

     * Verwendungsbeispiele:
     * // Alle in den 1980er Jahren Geborenen
     * LocalDate start = LocalDate.of(1980, 1, 1);
     * LocalDate end = LocalDate.of(1989, 12, 31);
     * List<Person> eighties = personRepository.findByBirthDateRange(start, end);

     * // Alle zwischen 18 und 65 Jahren (fuer Volljaehrige nicht-Senioren)
     * LocalDate now = LocalDate.now();
     * LocalDate vor18Jahren = now.minusYears(18);
     * LocalDate vor65Jahren = now.minusYears(65);
     * List<Person> arbeitsalter = personRepository.findByBirthDateRange(vor65Jahren, vor18Jahren);

     * JPQL-Query:
     * SELECT p FROM Person p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate

     * Performance:
     * Optimiert durch Index auf date_of_birth-Spalte (idx_person_date_of_birth).
     * BETWEEN-Operator ist effizienter als zwei separate Vergleiche.

     * @param startDate Startdatum des Bereichs (inklusive, nicht null)
     * @param endDate Enddatum des Bereichs (inklusive, nicht null)
     * @return Liste aller Personen im angegebenen Geburtsdatum-Bereich (nie null, kann leer sein)
     * @see #findByDateOfBirth(LocalDate)
     */
    @Query("SELECT p FROM Person p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Person> findByBirthDateRange(@Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * Zaehlt die Anzahl der Personen in einer bestimmten Stadt.

     * Diese Methode ist effizienter als findByCity(String), wenn nur die
     * Anzahl benoetigt wird, nicht die tatsaechlichen Datensaetze. Nuetzlich fuer
     * Statistiken und Dashboards.

     * Verwendungsbeispiel:
     * long einwohnerBerlin = personRepository.countByCity("Berlin");
     * long einwohnerMuenchen = personRepository.countByCity("Muenchen");

     * System.out.println("Berlin: " + einwohnerBerlin + " Personen");
     * System.out.println("Muenchen: " + einwohnerMuenchen + " Personen");

     * JPQL-Query:
     * SELECT COUNT(p) FROM Person p WHERE p.city = :city

     * Performance:
     * Sehr effizient, da nur Zaehlung ohne Datenuebertragung. Nutzt Index auf city-Spalte.

     * @param city Die Stadt, fuer die gezaehlt werden soll (nicht null)
     * @return Anzahl der Personen in dieser Stadt (0 wenn keine gefunden)
     * @see #findByCity(String)
     */
    @Query("SELECT COUNT(p) FROM Person p WHERE p.city = :city")
    long countByCity(@Param("city") String city);

    // ==================== EXISTENZPRUEFUNGEN ====================

    /**
     * Prueft, ob eine Person mit der angegebenen E-Mail-Adresse existiert.

     * Diese Methode ist effizienter als findByEmail(String), wenn nur
     * geprueft werden soll, ob ein Datensatz existiert. Typischerweise verwendet
     * fuer Duplikatspruefungen bei Registrierungsprozessen.

     * Verwendungsbeispiel:
     * String email = "max.mustermann@example.de";
     * if (personRepository.existsByEmail(email)) {
     *     throw new DuplicateEmailException("E-Mail bereits registriert");
     * }

     * Performance:
     * Sehr effizient, da nur boolean-Rueckgabe ohne Datenuebertragung.
     * Nutzt Unique-Index auf email-Spalte.

     * @param email Die zu pruefende E-Mail-Adresse (nicht null)
     * @return true wenn eine Person mit dieser E-Mail existiert, sonst false
     * @see #findByEmail(String)
     */
    boolean existsByEmail(String email);

    /**
     * Prueft, ob eine Person mit der angegebenen Personalausweisnummer existiert.

     * Diese Methode ist effizienter als findByNationalIdNumber(String), wenn nur
     * geprueft werden soll, ob ein Datensatz existiert. Wichtig fuer Duplikatspruefungen
     * bei behoerdlichen Registrierungsprozessen.

     * Verwendungsbeispiel:
     * String nationalId = "T22000129";
     * if (personRepository.existsByNationalIdNumber(nationalId)) {
     *     throw new DuplicateNationalIdException("Personalausweisnummer bereits erfasst");
     * }

     * DSGVO-Hinweis:
     * Personalausweisnummern sind besonders schuetzenswerte Daten. Die Pruefung sollte
     * nur fuer autorisierte Prozesz zugaenglich sein.

     * Performance:
     * Sehr effizient, da nur boolean-Rueckgabe ohne Datenuebertragung.
     * Nutzt Unique-Index auf national_id_number-Spalte.

     * @param nationalIdNumber Die zu pruefende Personalausweisnummer (nicht null)
     * @return true wenn eine Person mit dieser Nummer existiert, sonst false
     * @see #findByNationalIdNumber(String)
     */
    boolean existsByNationalIdNumber(String nationalIdNumber);

    /**
     * Findet alle Personen inklusive der geloeschten (nur fuer Admin).

     * Diese Methode umgeht das Soft-Delete-Verhalten und gibt alle Datensaetze
     * zurueck, auch die mit deleted = true. Aus Sicherheitsgruenden sollte
     * diese Methode nur fuer administrative Zwecke verwendet werden.

     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Ergebnisse inklusive geloeschter Personen (nie null, kann leer sein)
     */
    @Query(value = "SELECT * FROM persons", nativeQuery = true)
    Page<Person> findAllIncludingDeleted(Pageable pageable);
}