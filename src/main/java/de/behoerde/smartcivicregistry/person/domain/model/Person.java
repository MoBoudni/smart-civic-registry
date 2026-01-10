package de.behoerde.smartcivicregistry.person.domain.model;

import de.behoerde.smartcivicregistry.common.domain.AuditableEntity;
import de.behoerde.smartcivicregistry.person.application.service.PersonService;
import de.behoerde.smartcivicregistry.person.domain.repository.PersonRepository;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.Period;

/**
 * Aggregate Root für natürliche Personen im Smart Civic Registry.
 * <p>
 * Verwaltet DSGVO-konforme Stammdaten mit Geschäftslogik für Altersberechnung,
 * Namensformatierung und Adressvalidierung. Unterstützt Soft-Delete für Audit-Sicherheit.
 *
 * @see AuditableEntity
 * @see PersonRepository
 * @see PersonService
 */
@Entity
@Table(name = "persons",
        indexes = {
                @Index(name = "idx_person_last_name", columnList = "last_name"),
                @Index(name = "idx_person_date_of_birth", columnList = "date_of_birth"),
                @Index(name = "idx_person_city", columnList = "city")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE persons SET deleted = true WHERE id=?")
@Where(clause = "deleted = false")
public class Person extends AuditableEntity {

    /**
     * Technische ID (Primärschlüssel), auto-generiert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== NAMENSDATEN ====================

    /** Akademischer Titel (z.B. "Dr.", optional, max. 50 Zeichen). */
    @Column(name = "title", length = 50)
    private String title;

    /** Vorname (Pflichtfeld, max. 100 Zeichen). */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /** Mittelname (optional, max. 100 Zeichen). */
    @Column(name = "middle_name", length = 100)
    private String middleName;

    /** Nachname (Pflichtfeld, max. 100 Zeichen). */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /** Geburtsname (optional, max. 100 Zeichen). */
    @Column(name = "maiden_name", length = 100)
    private String maidenName;

    // ==================== PERSONENDATEN ====================

    /** Geburtsdatum (Pflichtfeld). */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * Geschlecht.
     *
     * @see Gender
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    /** Staatsangehörigkeit (optional, max. 100 Zeichen). */
    @Column(name = "citizenship", length = 100)
    private String citizenship;

    // ==================== ADRESSDATEN ====================

    /** Straße (optional, max. 255 Zeichen). */
    @Column(name = "street", length = 255)
    private String street;

    /** Hausnummer (optional, max. 20 Zeichen). */
    @Column(name = "house_number", length = 20)
    private String houseNumber;

    /** PLZ (optional, max. 20 Zeichen). */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** Stadt (optional, max. 100 Zeichen). */
    @Column(name = "city", length = 100)
    private String city;

    /** Land (optional, max. 100 Zeichen). */
    @Column(name = "country", length = 100)
    private String country;

    // ==================== KONTAKTDATEN ====================

    /** E-Mail (eindeutig, optional, max. 255 Zeichen). */
    @Column(name = "email", length = 255, unique = true)
    private String email;

    /** Festnetz (optional, max. 50 Zeichen). */
    @Column(name = "phone", length = 50)
    private String phone;

    /** Mobilfunk (optional, max. 50 Zeichen). */
    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;

    // ==================== WEITERE DATEN ====================

    /**
     * Familienstand.
     *
     * @see MaritalStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 50)
    private MaritalStatus maritalStatus;

    /** Geburtsort (optional, max. 100 Zeichen). */
    @Column(name = "birth_place", length = 100)
    private String birthPlace;

    /** Personalausweisnummer (eindeutig, optional, max. 50 Zeichen). */
    @Column(name = "national_id_number", length = 50, unique = true)
    private String nationalIdNumber;

    /** Steuer-ID (eindeutig, optional, max. 50 Zeichen). */
    @Column(name = "tax_id", length = 50, unique = true)
    private String taxId;

    // ==================== GESCHÄFTSLOGIK ====================

    /**
     * Berechnet das Alter der Person in Jahren.
     *
     * @return das aktuelle Alter in Jahren
     * @throws NullPointerException falls das Geburtsdatum nicht gesetzt ist
     */
    public int calculateAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Prüft, ob die Person volljährig ist (mindestens 18 Jahre alt).
     *
     * @return true, wenn die Person volljährig ist, sonst false
     */
    public boolean isAdult() {
        return calculateAge() >= 18;
    }

    /**
     * Prüft, ob die Person den Seniorenstatus erreicht hat (mindestens 65 Jahre alt).
     *
     * @return true, wenn die Person Senior ist, sonst false
     */
    public boolean isSenior() {
        return calculateAge() >= 65;
    }

    /**
     * Gibt den vollständigen Namen der Person zurück.
     * Format: "Titel Vorname Mittelname Nachname"
     *
     * @return der vollständige Name als String
     */
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

    /**
     * Gibt den offiziellen Namen der Person zurück.
     * Format: "Nachname, Vorname"
     *
     * @return der offizielle Name als String
     */
    public String getOfficialName() {
        return lastName + ", " + firstName;
    }

    /**
     * Gibt die vollständige Adresse der Person zurück.
     * Format: "Straße Nr, PLZ Ort, Land"
     *
     * @return die vollständige Adresse als String
     */
    public String getFullAddress() {
        return String.format("%s %s, %s %s, %s",
                street, houseNumber, postalCode, city, country);
    }

    /**
     * Prüft, ob die Adresse in Deutschland liegt.
     * Akzeptiert "DE", "Deutschland" oder "Germany" (Groß-/Kleinschreibung ignoriert).
     *
     * @return true, wenn die Adresse deutsch ist, sonst false
     */
    public boolean isGermanAddress() {
        return "DE".equalsIgnoreCase(country) ||
                "Deutschland".equalsIgnoreCase(country) ||
                "Germany".equalsIgnoreCase(country);
    }

    // ==================== FACTORY ====================

    /**
     * Erstellt eine Test-Person mit Musterdaten.
     *
     * @return eine neue Person-Instanz mit Beispielwerten
     */
    public static Person createTestPerson() {
        Person person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setDateOfBirth(LocalDate.of(1980, 1, 1));
        person.setGender(Gender.MALE);
        person.setCitizenship("Deutsch");
        person.setStreet("Musterstraße");
        person.setHouseNumber("123");
        person.setPostalCode("12345");
        person.setCity("Musterstadt");
        person.setCountry("Deutschland");
        person.setEmail("max.mustermann@example.de");
        person.setPhone("030 12345678");
        person.setMaritalStatus(MaritalStatus.MARRIED);
        person.setBirthPlace("Berlin");
        return person;
    }

    // ==================== OBJECT ====================

    /**
     * Vergleicht diese Person mit einem anderen Objekt auf Gleichheit.
     * Zwei Personen gelten als gleich, wenn sie dieselbe ID, E-Mail oder Personalausweisnummer haben.
     *
     * @param o das zu vergleichende Objekt
     * @return true, wenn gleich, sonst false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (id != null && person.id != null) {
            return id.equals(person.id);
        }
        if (email != null && person.email != null) {
            return email.equals(person.email);
        }
        if (nationalIdNumber != null && person.nationalIdNumber != null) {
            return nationalIdNumber.equals(person.nationalIdNumber);
        }

        return false;
    }

    /**
     * Berechnet den Hashcode für diese Person.
     * Bevorzugt ID, dann E-Mail, dann Personalausweisnummer.
     *
     * @return der Hashcode
     */
    @Override
    public int hashCode() {
        if (id != null) return id.hashCode();
        if (email != null) return email.hashCode();
        if (nationalIdNumber != null) return nationalIdNumber.hashCode();
        return super.hashCode();
    }

    /**
     * Gibt eine String-Repräsentation der Person zurück.
     *
     * @return String mit ID, Name und E-Mail
     */
    @Override
    public String toString() {
        return String.format("Person[id=%d, name='%s', email='%s']",
                id, getFullName(), email);
    }

    // ==================== BUILDER ====================

    /**
     * Builder-Klasse für Person-Entitäten.
     * <p>
     * Stellt eine typsichere Methode zur Erstellung von Person-Instanzen bereit
     * und validiert Pflichtfelder.
     */
    public static class PersonBuilder {
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String title;
        private String middleName;
        private String maidenName;
        private Gender gender;
        private String citizenship;
        private String street;
        private String houseNumber;
        private String postalCode;
        private String city;
        private String country;
        private String email;
        private String phone;
        private String mobilePhone;
        private MaritalStatus maritalStatus;
        private String birthPlace;

        /**
         * Privater Konstruktor für den Builder.
         */
        PersonBuilder() {
        }

        /**
         * Setzt den Vornamen.
         *
         * @param firstName Vorname
         * @return Builder-Instanz
         */
        public PersonBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Setzt den Nachnamen.
         *
         * @param lastName Nachname
         * @return Builder-Instanz
         */
        public PersonBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Setzt das Geburtsdatum.
         *
         * @param dateOfBirth Geburtsdatum
         * @return Builder-Instanz
         */
        public PersonBuilder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public PersonBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PersonBuilder middleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public PersonBuilder maidenName(String maidenName) {
            this.maidenName = maidenName;
            return this;
        }

        public PersonBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public PersonBuilder citizenship(String citizenship) {
            this.citizenship = citizenship;
            return this;
        }

        public PersonBuilder street(String street) {
            this.street = street;
            return this;
        }

        public PersonBuilder houseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
            return this;
        }

        public PersonBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public PersonBuilder city(String city) {
            this.city = city;
            return this;
        }

        public PersonBuilder country(String country) {
            this.country = country;
            return this;
        }

        public PersonBuilder email(String email) {
            this.email = email;
            return this;
        }

        public PersonBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public PersonBuilder mobilePhone(String mobilePhone) {
            this.mobilePhone = mobilePhone;
            return this;
        }

        public PersonBuilder maritalStatus(MaritalStatus maritalStatus) {
            this.maritalStatus = maritalStatus;
            return this;
        }

        public PersonBuilder birthPlace(String birthPlace) {
            this.birthPlace = birthPlace;
            return this;
        }

        /**
         * Erstellt eine neue Person-Instanz mit den gesetzten Werten.
         * Pflichtfelder werden validiert.
         *
         * @return neue Person-Instanz
         * @throws IllegalStateException falls Pflichtfelder fehlen
         */
        public Person build() {
            if (firstName == null) {
                throw new IllegalStateException("firstName darf nicht null sein");
            }
            if (lastName == null) {
                throw new IllegalStateException("lastName darf nicht null sein");
            }
            if (dateOfBirth == null) {
                throw new IllegalStateException("dateOfBirth darf nicht null sein");
            }

            Person person = new Person();
            person.setFirstName(this.firstName);
            person.setLastName(this.lastName);
            person.setDateOfBirth(this.dateOfBirth);
            person.setTitle(this.title);
            person.setMiddleName(this.middleName);
            person.setMaidenName(this.maidenName);
            person.setGender(this.gender);
            person.setCitizenship(this.citizenship);
            person.setStreet(this.street);
            person.setHouseNumber(this.houseNumber);
            person.setPostalCode(this.postalCode);
            person.setCity(this.city);
            person.setCountry(this.country);
            person.setEmail(this.email);
            person.setPhone(this.phone);
            person.setMobilePhone(this.mobilePhone);
            person.setMaritalStatus(this.maritalStatus);
            person.setBirthPlace(this.birthPlace);
            return person;
        }
    }

    /**
     * Statische Factory-Methode für den Person-Builder.
     *
     * @return neue Builder-Instanz
     */
    public static PersonBuilder builder() {
        return new PersonBuilder();
    }
}
