package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.application.dto.PersonRequestDTO;
import de.behoerde.smartcivicregistry.person.application.dto.PersonResponseDTO;
import de.behoerde.smartcivicregistry.person.application.mapper.PersonMapper;
import de.behoerde.smartcivicregistry.person.domain.model.Person;
import de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    // ==================== CRUD OPERATIONS ====================

    @Override
    @Transactional
    public PersonResponseDTO createPerson(PersonRequestDTO requestDTO) {
        log.info("Creating new person: {} {}", requestDTO.getFirstName(), requestDTO.getLastName());

        // 1. DTO-Validierung
        requestDTO.validateBusinessRules();

        // 2. DTO → Entity
        Person person = personMapper.toEntity(requestDTO);

        // 3. Eindeutigkeitsprüfungen
        validateUniquenessConstraints(person, null);

        // 4. Business-Regel: Alterswarnung
        if (requestDTO.getDateOfBirth() != null) {
            LocalDate today = LocalDate.now();
            long age = java.time.temporal.ChronoUnit.YEARS.between(requestDTO.getDateOfBirth(), today);
            if (age < 18) {
                log.warn("Creating person under 18: {} {} (Age: {})",
                        requestDTO.getFirstName(), requestDTO.getLastName(), age);
            }
        }

        // 5. Persistierung
        Person savedPerson = personRepository.save(person);
        log.info("Person created successfully with ID: {}", savedPerson.getId());

        // 6. Entity → Response DTO
        return personMapper.toResponseDTO(savedPerson);
    }

    @Override
    @Transactional
    public PersonResponseDTO updatePerson(Long id, PersonRequestDTO requestDTO) {
        log.info("Updating person with id: {}", id);

        // 1. Existierende Person laden
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        // 2. DTO-Validierung
        requestDTO.validateBusinessRules();

        // 3. Eindeutigkeitsprüfungen (ohne aktuelle Person)
        Person tempPerson = personMapper.toEntity(requestDTO);
        tempPerson.setId(id); // Für Vergleichszwecke
        validateUniquenessConstraints(tempPerson, id);

        // 4. Vollständiges Update (PUT-Semantik)
        updateEntityFromDTO(requestDTO, existingPerson);

        // 5. Persistierung
        Person updatedPerson = personRepository.save(existingPerson);
        log.info("Person with ID {} updated successfully", id);

        // 6. Entity → Response DTO
        return personMapper.toResponseDTO(updatedPerson);
    }

    @Override
    @Transactional
    public PersonResponseDTO partialUpdatePerson(Long id, PersonRequestDTO requestDTO) {
        log.info("Partially updating person with id: {}", id);

        // 1. Existierende Person laden
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        // 2. Eindeutigkeitsprüfungen für geänderte Felder
        validatePartialUniquenessConstraints(requestDTO, existingPerson);

        // 3. Partielles Update (PATCH-Semantik)
        personMapper.updateEntityFromDTO(requestDTO, existingPerson);

        // 4. Persistierung
        Person updatedPerson = personRepository.save(existingPerson);
        log.info("Person with ID {} partially updated successfully", id);

        // 5. Entity → Response DTO
        return personMapper.toResponseDTO(updatedPerson);
    }

    @Override
    @Transactional
    public void deletePerson(Long id) {
        log.info("Soft deleting person with id: {}", id);

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));

        // Soft Delete wird durch @SQLDelete Annotation gehandelt
        personRepository.delete(person);

        log.info("Person {} soft deleted successfully", id);
    }

    // ==================== READ OPERATIONS ====================

    @Override
    public Optional<PersonResponseDTO> getPersonById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        return personRepository.findById(id)
                .map(personMapper::toResponseDTO);
    }

    @Override
    public Optional<PersonResponseDTO> getPersonByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return personRepository.findByEmail(email)
                .map(personMapper::toResponseDTO);
    }

    @Override
    public Page<PersonResponseDTO> getAllPersons(Pageable pageable) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }

        Page<Person> personPage = personRepository.findAll(pageable);
        return personMapper.toResponseDTOPage(personPage);
    }

    // ==================== SEARCH OPERATIONS ====================

    @Override
    public List<PersonResponseDTO> searchPersonsByName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Search name cannot be null or empty");
        }

        // Verwendet die existierende Repository-Methode mit Pagination
        Page<Person> resultPage = personRepository.searchByAnyField(name, Pageable.unpaged());
        return personMapper.toResponseDTOList(resultPage.getContent());
    }

    @Override
    public List<PersonResponseDTO> findPersonsByLastName(String lastName) {
        if (!StringUtils.hasText(lastName)) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }

        List<Person> persons = personRepository.findByLastName(lastName);
        return personMapper.toResponseDTOList(persons);
    }

    @Override
    public List<PersonResponseDTO> findPersonsByCity(String city) {
        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }

        List<Person> persons = personRepository.findByCity(city);
        return personMapper.toResponseDTOList(persons);
    }

    @Override
    public List<PersonResponseDTO> findPersonsByDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }

        List<Person> persons = personRepository.findByDateOfBirth(dateOfBirth);
        return personMapper.toResponseDTOList(persons);
    }

    @Override
    public List<PersonResponseDTO> findPersonsByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        List<Person> persons = personRepository.findByBirthDateRange(startDate, endDate);
        return personMapper.toResponseDTOList(persons);
    }

    @Override
    public Page<PersonResponseDTO> searchPersonsByAnyField(String searchTerm, Pageable pageable) {
        if (!StringUtils.hasText(searchTerm)) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }

        Page<Person> resultPage = personRepository.searchByAnyField(searchTerm, pageable);
        return personMapper.toResponseDTOPage(resultPage);
    }

    // ==================== UTILITY OPERATIONS ====================

    @Override
    public long countPersonsByCity(String city) {
        if (!StringUtils.hasText(city)) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }

        return personRepository.countByCity(city);
    }

    @Override
    public boolean personExistsByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return personRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNationalIdNumber(String nationalIdNumber) {
        if (!StringUtils.hasText(nationalIdNumber)) {
            throw new IllegalArgumentException("National ID number cannot be null or empty");
        }

        return personRepository.existsByNationalIdNumber(nationalIdNumber);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void validateUniquenessConstraints(Person person, Long excludedId) {
        // E-Mail-Prüfung
        if (person.getEmail() != null) {
            boolean emailExists = excludedId == null
                    ? personRepository.existsByEmail(person.getEmail())
                    : personRepository.findByEmail(person.getEmail())
                    .filter(p -> !p.getId().equals(excludedId))
                    .isPresent();

            if (emailExists) {
                throw new IllegalArgumentException(
                        String.format("Person with email '%s' already exists", person.getEmail()));
            }
        }

        // Personalausweisnummer-Prüfung
        if (person.getNationalIdNumber() != null) {
            boolean nationalIdExists = excludedId == null
                    ? personRepository.existsByNationalIdNumber(person.getNationalIdNumber())
                    : personRepository.findByNationalIdNumber(person.getNationalIdNumber())
                    .filter(p -> !p.getId().equals(excludedId))
                    .isPresent();

            if (nationalIdExists) {
                throw new IllegalArgumentException(
                        String.format("Person with national ID '%s' already exists", person.getNationalIdNumber()));
            }
        }

        // Steuer-ID-Prüfung (optimierte Version)
        if (person.getTaxId() != null) {
            boolean taxIdExists = excludedId == null
                    ? existsByTaxId(person.getTaxId())
                    : existsByTaxIdExcluding(person.getTaxId(), excludedId);

            if (taxIdExists) {
                throw new IllegalArgumentException(
                        String.format("Person with tax ID '%s' already exists", person.getTaxId()));
            }
        }
    }

    private void validatePartialUniquenessConstraints(PersonRequestDTO requestDTO, Person existingPerson) {
        // E-Mail-Änderung prüfen
        if (requestDTO.getEmail() != null &&
                !requestDTO.getEmail().equals(existingPerson.getEmail())) {
            if (personRepository.existsByEmail(requestDTO.getEmail())) {
                throw new IllegalArgumentException(
                        String.format("Email '%s' already exists", requestDTO.getEmail()));
            }
        }

        // Personalausweisnummer-Änderung prüfen
        if (requestDTO.getNationalIdNumber() != null &&
                !requestDTO.getNationalIdNumber().equals(existingPerson.getNationalIdNumber())) {
            if (personRepository.existsByNationalIdNumber(requestDTO.getNationalIdNumber())) {
                throw new IllegalArgumentException(
                        String.format("National ID '%s' already exists", requestDTO.getNationalIdNumber()));
            }
        }

        // Steuer-ID-Änderung prüfen
        if (requestDTO.getTaxId() != null &&
                !requestDTO.getTaxId().equals(existingPerson.getTaxId())) {
            if (existsByTaxIdExcluding(requestDTO.getTaxId(), existingPerson.getId())) {
                throw new IllegalArgumentException(
                        String.format("Tax ID '%s' already exists", requestDTO.getTaxId()));
            }
        }
    }

    private boolean existsByTaxId(String taxId) {
        // TODO: Repository-Methode implementieren: existsByTaxId(String taxId)
        return personRepository.findAll().stream()
                .anyMatch(p -> taxId != null && taxId.equals(p.getTaxId()));
    }

    private boolean existsByTaxIdExcluding(String taxId, Long excludedId) {
        // TODO: Repository-Methode implementieren: existsByTaxIdAndIdNot(String taxId, Long excludedId)
        return personRepository.findAll().stream()
                .anyMatch(p -> taxId != null && taxId.equals(p.getTaxId()) && !p.getId().equals(excludedId));
    }

    private void updateEntityFromDTO(PersonRequestDTO requestDTO, Person person) {
        // Namensdaten
        person.setTitle(requestDTO.getTitle());
        person.setFirstName(requestDTO.getFirstName());
        person.setMiddleName(requestDTO.getMiddleName());
        person.setLastName(requestDTO.getLastName());
        person.setMaidenName(requestDTO.getMaidenName());

        // Personendaten
        person.setDateOfBirth(requestDTO.getDateOfBirth());
        person.setGender(requestDTO.getGender());
        person.setCitizenship(requestDTO.getCitizenship());

        // Adressdaten
        person.setStreet(requestDTO.getStreet());
        person.setHouseNumber(requestDTO.getHouseNumber());
        person.setPostalCode(requestDTO.getPostalCode());
        person.setCity(requestDTO.getCity());
        person.setCountry(requestDTO.getCountry());

        // Kontaktdaten
        person.setEmail(requestDTO.getEmail());
        person.setPhone(requestDTO.getPhone());
        person.setMobilePhone(requestDTO.getMobilePhone());

        // Weitere Daten
        person.setMaritalStatus(requestDTO.getMaritalStatus());
        person.setBirthPlace(requestDTO.getBirthPlace());
        person.setNationalIdNumber(requestDTO.getNationalIdNumber());
        person.setTaxId(requestDTO.getTaxId());
    }
}