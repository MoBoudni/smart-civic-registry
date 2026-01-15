package de.behoerde.smartcivicregistry.person.application.mapper;

import de.behoerde.smartcivicregistry.person.application.dto.PersonRequestDTO;
import de.behoerde.smartcivicregistry.person.application.dto.PersonResponseDTO;
import de.behoerde.smartcivicregistry.person.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper für die Konvertierung zwischen Person-Entity und DTOs.
 * Einfache manuelle Implementierung ohne MapStruct.
 */
@Component
public class PersonMapper {

    /**
     * Konvertiert Person-Entity zu PersonResponseDTO.
     */
    public PersonResponseDTO toResponseDTO(Person person) {
        if (person == null) {
            return null;
        }

        return PersonResponseDTO.builder()
                .id(person.getId())
                .title(person.getTitle())
                .firstName(person.getFirstName())
                .middleName(person.getMiddleName())
                .lastName(person.getLastName())
                .maidenName(person.getMaidenName())
                .fullName(person.getFullName())
                .officialName(person.getOfficialName())
                .dateOfBirth(person.getDateOfBirth())
                .age(calculateAge(person.getDateOfBirth()))
                .isAdult(person.isAdult())
                .isSenior(person.isSenior())
                .gender(person.getGender())
                .citizenship(person.getCitizenship())
                .street(person.getStreet())
                .houseNumber(person.getHouseNumber())
                .postalCode(person.getPostalCode())
                .city(person.getCity())
                .country(person.getCountry())
                .fullAddress(person.getFullAddress())
                .isGermanAddress(person.isGermanAddress())
                .email(person.getEmail())
                .phone(person.getPhone())
                .mobilePhone(person.getMobilePhone())
                .maritalStatus(person.getMaritalStatus())
                .birthPlace(person.getBirthPlace())
                .nationalIdNumber(person.getNationalIdNumber())
                .taxId(person.getTaxId())
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .createdBy(person.getCreatedBy())
                .updatedBy(person.getUpdatedBy())
                .deleted(person.isDeleted())
                .build();
    }

    /**
     * Konvertiert PersonRequestDTO zu Person-Entity.
     */
    public Person toEntity(PersonRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        // Verwende den Builder aus der Person-Klasse
        return Person.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dateOfBirth(dto.getDateOfBirth())
                .title(dto.getTitle())
                .middleName(dto.getMiddleName())
                .maidenName(dto.getMaidenName())
                .gender(dto.getGender())
                .citizenship(dto.getCitizenship())
                .street(dto.getStreet())
                .houseNumber(dto.getHouseNumber())
                .postalCode(dto.getPostalCode())
                .city(dto.getCity())
                .country(dto.getCountry())
                .email(dto.getEmail())
                .phone(normalizePhoneNumber(dto.getPhone()))
                .mobilePhone(normalizePhoneNumber(dto.getMobilePhone()))
                .maritalStatus(dto.getMaritalStatus())
                .birthPlace(dto.getBirthPlace())
                .nationalIdNumber(dto.getNationalIdNumber())
                .taxId(dto.getTaxId())
                .deleted(false)
                .build();
    }

    /**
     * Aktualisiert existierende Person-Entity mit Werten aus DTO (PATCH-Semantik).
     */
    public void updateEntityFromDTO(PersonRequestDTO dto, Person person) {
        if (dto == null || person == null) {
            return;
        }

        // Aktualisiere nur nicht-null Felder
        if (dto.getTitle() != null) {
            person.setTitle(dto.getTitle());
        }
        if (dto.getFirstName() != null) {
            person.setFirstName(dto.getFirstName());
        }
        if (dto.getMiddleName() != null) {
            person.setMiddleName(dto.getMiddleName());
        }
        if (dto.getLastName() != null) {
            person.setLastName(dto.getLastName());
        }
        if (dto.getMaidenName() != null) {
            person.setMaidenName(dto.getMaidenName());
        }
        if (dto.getDateOfBirth() != null) {
            person.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getGender() != null) {
            person.setGender(dto.getGender());
        }
        if (dto.getCitizenship() != null) {
            person.setCitizenship(dto.getCitizenship());
        }
        if (dto.getStreet() != null) {
            person.setStreet(dto.getStreet());
        }
        if (dto.getHouseNumber() != null) {
            person.setHouseNumber(dto.getHouseNumber());
        }
        if (dto.getPostalCode() != null) {
            person.setPostalCode(dto.getPostalCode());
        }
        if (dto.getCity() != null) {
            person.setCity(dto.getCity());
        }
        if (dto.getCountry() != null) {
            person.setCountry(dto.getCountry());
        }
        if (dto.getEmail() != null) {
            person.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            person.setPhone(normalizePhoneNumber(dto.getPhone()));
        }
        if (dto.getMobilePhone() != null) {
            person.setMobilePhone(normalizePhoneNumber(dto.getMobilePhone()));
        }
        if (dto.getMaritalStatus() != null) {
            person.setMaritalStatus(dto.getMaritalStatus());
        }
        if (dto.getBirthPlace() != null) {
            person.setBirthPlace(dto.getBirthPlace());
        }
        if (dto.getNationalIdNumber() != null) {
            person.setNationalIdNumber(dto.getNationalIdNumber());
        }
        if (dto.getTaxId() != null) {
            person.setTaxId(dto.getTaxId());
        }
    }

    /**
     * Konvertiert Liste von Personen zu Liste von Response DTOs.
     */
    public List<PersonResponseDTO> toResponseDTOList(List<Person> persons) {
        if (persons == null) {
            return List.of();
        }

        return persons.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Konvertiert Page von Personen zu Page von Response DTOs.
     */
    public Page<PersonResponseDTO> toResponseDTOPage(Page<Person> personPage) {
        if (personPage == null) {
            return Page.empty();
        }

        List<PersonResponseDTO> dtoContent = toResponseDTOList(personPage.getContent());

        return new PageImpl<>(
                dtoContent,
                personPage.getPageable(),
                personPage.getTotalElements()
        );
    }

    /**
     * Konvertiert Iterable von Personen zu Liste von Response DTOs.
     */
    public List<PersonResponseDTO> toResponseDTOListFromIterable(Iterable<Person> persons) {
        if (persons == null) {
            return List.of();
        }

        List<Person> personList = new java.util.ArrayList<>();
        persons.forEach(personList::add);
        return toResponseDTOList(personList);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Berechnet das Alter basierend auf Geburtsdatum.
     */
    private Integer calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return null;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Normalisiert Telefonnummern (entfernt alle Nicht-Ziffern außer +).
     */
    private String normalizePhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        // Entferne alle Leerzeichen, Klammern, Bindestriche etc.
        return phone.replaceAll("[^\\d+]", "").trim();
    }

    /**
     * Erstellt eine neue Person-Entity mit ID (für Updates).
     */
    public Person toEntityWithId(PersonRequestDTO dto, Long id) {
        Person person = toEntity(dto);
        if (person != null) {
            person.setId(id);
        }
        return person;
    }

    /**
     * Konvertiert ResponseDTO zurück zu Entity (für spezielle Use Cases).
     */
    public Person toEntityFromResponseDTO(PersonResponseDTO dto) {
        if (dto == null) {
            return null;
        }

        Person person = new Person();
        person.setId(dto.getId());
        person.setTitle(dto.getTitle());
        person.setFirstName(dto.getFirstName());
        person.setMiddleName(dto.getMiddleName());
        person.setLastName(dto.getLastName());
        person.setMaidenName(dto.getMaidenName());
        person.setDateOfBirth(dto.getDateOfBirth());
        person.setGender(dto.getGender());
        person.setCitizenship(dto.getCitizenship());
        person.setStreet(dto.getStreet());
        person.setHouseNumber(dto.getHouseNumber());
        person.setPostalCode(dto.getPostalCode());
        person.setCity(dto.getCity());
        person.setCountry(dto.getCountry());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        person.setMobilePhone(dto.getMobilePhone());
        person.setMaritalStatus(dto.getMaritalStatus());
        person.setBirthPlace(dto.getBirthPlace());
        person.setNationalIdNumber(dto.getNationalIdNumber());
        person.setTaxId(dto.getTaxId());
        person.setCreatedAt(dto.getCreatedAt());
        person.setUpdatedAt(dto.getUpdatedAt());
        person.setCreatedBy(dto.getCreatedBy());
        person.setUpdatedBy(dto.getUpdatedBy());
        person.setDeleted(dto.getDeleted() != null && dto.getDeleted());

        return person;
    }
}