package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.application.dto.PersonRequestDTO;
import de.behoerde.smartcivicregistry.person.application.dto.PersonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service-Interface für die Geschäftslogik und Anwendungsfälle rund um Person-Entitäten.
 * <p>
 * Dieses Interface definiert die Application Service Layer der hexagonalen Architektur
 * und kapselt alle Use Cases für die Verwaltung von Personenstammdaten.
 * </p>
 *
 * <strong>Version 2.0 Änderungen:</strong>
 * <ul>
 *   <li>Verwendet DTOs statt direkter Entity-Exposition</li>
 *   <li>Konsistente Rückgabetypen für alle Methoden</li>
 *   <li>Bessere Trennung zwischen Input (RequestDTO) und Output (ResponseDTO)</li>
 * </ul>
 *
 * @author Smart Civic Registry Team
 * @version 2.0
 * @since Phase 2 (Person Domain Module)
 * @see PersonServiceImpl
 * @see PersonRequestDTO
 * @see PersonResponseDTO
 */
public interface PersonService {

    // ==================== CRUD OPERATIONS (DTO-basiert) ====================

    /**
     * Erstellt eine neue Person im System.
     *
     * @param requestDTO Die zu erstellende Person als DTO (nicht null)
     * @return Die persistierte Person als Response DTO
     * @throws IllegalArgumentException wenn Validierung fehlschlägt
     * @throws IllegalStateException wenn E-Mail oder Personalausweisnummer bereits existiert
     */
    PersonResponseDTO createPerson(PersonRequestDTO requestDTO);

    /**
     * Aktualisiert eine bestehende Person vollständig (PUT-Semantik).
     *
     * @param id Die ID der zu aktualisierenden Person (nicht null)
     * @param requestDTO Die Person mit den neuen Werten als DTO (nicht null)
     * @return Die aktualisierte Person als Response DTO
     * @throws IllegalArgumentException wenn ID null ist oder Person invalid
     * @throws jakarta.persistence.EntityNotFoundException wenn Person mit ID nicht existiert
     */
    PersonResponseDTO updatePerson(Long id, PersonRequestDTO requestDTO);

    /**
     * Aktualisiert eine bestehende Person partiell (PATCH-Semantik).
     *
     * @param id Die ID der zu aktualisierenden Person (nicht null)
     * @param requestDTO Die Person mit den zu ändernden Feldern als DTO
     * @return Die aktualisierte Person als Response DTO
     * @throws IllegalArgumentException wenn ID null ist
     * @throws jakarta.persistence.EntityNotFoundException wenn Person mit ID nicht existiert
     */
    PersonResponseDTO partialUpdatePerson(Long id, PersonRequestDTO requestDTO);

    /**
     * Löscht eine Person aus dem System (Soft-Delete).
     *
     * @param id Die ID der zu löschenden Person (nicht null, muss existieren)
     * @throws IllegalArgumentException wenn ID null ist
     * @throws jakarta.persistence.EntityNotFoundException wenn Person mit ID nicht existiert
     */
    void deletePerson(Long id);

    // ==================== READ OPERATIONS (DTO-basiert) ====================

    /**
     * Sucht eine Person anhand ihrer ID.
     *
     * @param id Die ID der gesuchten Person (nicht null)
     * @return Optional mit gefundener Person als Response DTO oder empty wenn nicht gefunden
     * @throws IllegalArgumentException wenn ID null ist
     */
    Optional<PersonResponseDTO> getPersonById(Long id);

    /**
     * Sucht eine Person anhand ihrer E-Mail-Adresse.
     *
     * @param email Die E-Mail-Adresse der gesuchten Person (nicht null, nicht leer)
     * @return Optional mit gefundener Person als Response DTO oder empty wenn nicht gefunden
     * @throws IllegalArgumentException wenn E-Mail null oder leer ist
     */
    Optional<PersonResponseDTO> getPersonByEmail(String email);

    /**
     * Gibt alle Personen mit Paginierung zurück.
     *
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Liste aller Personen als Response DTOs
     * @throws IllegalArgumentException wenn Pageable null ist
     */
    Page<PersonResponseDTO> getAllPersons(Pageable pageable);

    // ==================== SEARCH OPERATIONS (DTO-basiert) ====================

    /**
     * Sucht Personen nach Namen (Vor- oder Nachname).
     *
     * @param name Der Suchbegriff für Vor- oder Nachname (nicht null, nicht leer)
     * @return Liste aller passenden Personen als Response DTOs
     * @throws IllegalArgumentException wenn Name null oder leer ist
     */
    List<PersonResponseDTO> searchPersonsByName(String name);

    /**
     * Sucht Personen nach exaktem Nachnamen.
     *
     * @param lastName Der gesuchte Nachname (exakte Übereinstimmung, nicht null)
     * @return Liste aller Personen mit diesem Nachnamen als Response DTOs
     * @throws IllegalArgumentException wenn lastName null ist
     */
    List<PersonResponseDTO> findPersonsByLastName(String lastName);

    /**
     * Sucht Personen nach Stadt.
     *
     * @param city Die gesuchte Stadt (exakte Übereinstimmung, nicht null)
     * @return Liste aller Personen aus dieser Stadt als Response DTOs
     * @throws IllegalArgumentException wenn city null ist
     */
    List<PersonResponseDTO> findPersonsByCity(String city);

    /**
     * Sucht Personen nach exaktem Geburtsdatum.
     *
     * @param dateOfBirth Das gesuchte Geburtsdatum (nicht null)
     * @return Liste aller Personen mit diesem Geburtsdatum als Response DTOs
     * @throws IllegalArgumentException wenn dateOfBirth null ist
     */
    List<PersonResponseDTO> findPersonsByDateOfBirth(LocalDate dateOfBirth);

    /**
     * Sucht Personen nach Geburtsdatum-Bereich.
     *
     * @param startDate Startdatum des Bereichs (inklusive, nicht null)
     * @param endDate Enddatum des Bereichs (inklusive, nicht null)
     * @return Liste aller Personen im Geburtsdatum-Bereich als Response DTOs
     * @throws IllegalArgumentException wenn startDate oder endDate null ist
     * @throws IllegalArgumentException wenn startDate nach endDate liegt
     */
    List<PersonResponseDTO> findPersonsByBirthDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Durchsucht Personen über mehrere Felder hinweg mit Paginierung.
     *
     * @param searchTerm Der Suchbegriff (wird in allen Feldern gesucht, nicht null)
     * @param pageable Paginierungs- und Sortierungsinformationen (nicht null)
     * @return Paginierte Suchergebnisse als Response DTOs
     * @throws IllegalArgumentException wenn searchTerm oder pageable null ist
     */
    Page<PersonResponseDTO> searchPersonsByAnyField(String searchTerm, Pageable pageable);

    // ==================== UTILITY OPERATIONS ====================

    /**
     * Zählt die Anzahl der Personen in einer bestimmten Stadt.
     *
     * @param city Die Stadt, für die gezählt werden soll (nicht null)
     * @return Anzahl der Personen in dieser Stadt (0 wenn keine gefunden)
     * @throws IllegalArgumentException wenn city null ist
     */
    long countPersonsByCity(String city);

    /**
     * Prüft, ob eine Person mit der angegebenen E-Mail-Adresse existiert.
     *
     * @param email Die zu prüfende E-Mail-Adresse (nicht null)
     * @return true wenn eine Person mit dieser E-Mail existiert, sonst false
     * @throws IllegalArgumentException wenn email null ist
     */
    boolean personExistsByEmail(String email);

    /**
     * Prüft, ob eine Person mit der angegebenen Personalausweisnummer existiert.
     *
     * @param nationalIdNumber Die zu prüfende Personalausweisnummer (nicht null)
     * @return true wenn eine Person mit dieser Nummer existiert, sonst false
     * @throws IllegalArgumentException wenn nationalIdNumber null ist
     */
    boolean existsByNationalIdNumber(String nationalIdNumber);
}