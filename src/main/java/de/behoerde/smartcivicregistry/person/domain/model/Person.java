package de.behoerde.smartcivicregistry.person.domain.model;

import de.behoerde.smartcivicregistry.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "persons",
       indexes = {
           @Index(name = "idx_person_last_name", columnList = "last_name"),
           @Index(name = "idx_person_date_of_birth", columnList = "date_of_birth"),
           @Index(name = "idx_person_city", columnList = "city")
       })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE persons SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class Person extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Namenfelder direkt
    @Column(name = "title", length = 50)
    private String title;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "middle_name", length = 100)
    private String middleName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "maiden_name", length = 100)
    private String maidenName;
    
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;
    
    @Column(name = "citizenship", length = 100)
    private String citizenship;
    
    // Adressfelder
    @Column(name = "street", length = 255)
    private String street;
    
    @Column(name = "house_number", length = 20)
    private String houseNumber;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "country", length = 100)
    private String country;
    
    // Kontaktdaten
    @Column(name = "email", length = 255, unique = true)
    private String email;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;
    
    // Weitere persönliche Daten
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 50)
    private MaritalStatus maritalStatus;
    
    @Column(name = "birth_place", length = 100)
    private String birthPlace;
    
    @Column(name = "national_id_number", length = 50, unique = true)
    private String nationalIdNumber;
    
    @Column(name = "tax_id", length = 50, unique = true)
    private String taxId;
    
    // Geschäftslogik Methoden (Domain Logic)
    public int calculateAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    public boolean isAdult() {
        return calculateAge() >= 18;
    }
    
    public boolean isSenior() {
        return calculateAge() >= 65;
    }
    
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        
        if (title != null && !title.isEmpty()) {
            fullName.append(title).append(" ");
        }
        
        fullName.append(firstName);
        
        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        
        fullName.append(" ").append(lastName);
        
        return fullName.toString().trim();
    }
    
    public String getOfficialName() {
        return lastName + ", " + firstName;
    }
    
    public String getFullAddress() {
        return String.format("%s %s, %s %s, %s", 
            street, houseNumber, postalCode, city, country);
    }
    
    public boolean isGermanAddress() {
        return "DE".equalsIgnoreCase(country) || 
               "Deutschland".equalsIgnoreCase(country) ||
               "Germany".equalsIgnoreCase(country);
    }
    
    // Factory Method für Tests
    public static Person createTestPerson() {
        return Person.builder()
                .title("")
                .firstName("Max")
                .lastName("Mustermann")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .gender(Gender.MALE)
                .citizenship("Deutsch")
                .street("Musterstraße")
                .houseNumber("123")
                .postalCode("12345")
                .city("Musterstadt")
                .country("Deutschland")
                .email("max.mustermann@example.de")
                .phone("030 12345678")
                .maritalStatus(MaritalStatus.MARRIED)
                .birthPlace("Berlin")
                .build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Person person = (Person) o;
        
        if (id != null && person.id != null) {
            return id.equals(person.id);
        }
        
        // Fallback auf eindeutige Felder
        if (email != null && person.email != null) {
            return email.equals(person.email);
        }
        
        if (nationalIdNumber != null && person.nationalIdNumber != null) {
            return nationalIdNumber.equals(person.nationalIdNumber);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        if (id != null) return id.hashCode();
        if (email != null) return email.hashCode();
        if (nationalIdNumber != null) return nationalIdNumber.hashCode();
        return super.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("Person[id=%d, name='%s', email='%s']", 
            id, getFullName(), email);
    }
}
