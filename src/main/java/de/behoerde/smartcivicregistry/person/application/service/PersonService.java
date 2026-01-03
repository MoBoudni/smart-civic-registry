package de.behoerde.smartcivicregistry.person.application.service;

import de.behoerde.smartcivicregistry.person.domain.model.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person createPerson(Person person);
    Person updatePerson(Long id, Person person);
    void deletePerson(Long id);
    Optional<Person> getPersonById(Long id);
    Optional<Person> getPersonByEmail(String email);
    Page<Person> getAllPersons(Pageable pageable);
    List<Person> searchPersonsByName(String name);
    List<Person> findPersonsByCity(String city);
    List<Person> findPersonsByBirthDateRange(LocalDate startDate, LocalDate endDate);
    long countPersonsByCity(String city);
    boolean personExistsByEmail(String email);
}
