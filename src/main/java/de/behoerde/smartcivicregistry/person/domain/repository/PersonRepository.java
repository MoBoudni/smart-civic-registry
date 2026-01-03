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

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    
    // Custom Query Methods
    Optional<Person> findByEmail(String email);
    
    Optional<Person> findByNationalIdNumber(String nationalIdNumber);
    
    List<Person> findByLastName(String lastName);
    
    List<Person> findByDateOfBirth(LocalDate dateOfBirth);
    
    List<Person> findByCity(String city);
    
    Page<Person> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
    
    // Komplexere Queries mit @Query
    @Query("SELECT p FROM Person p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Person> searchByAnyField(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT p FROM Person p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Person> findByBirthDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(p) FROM Person p WHERE p.city = :city")
    long countByCity(@Param("city") String city);
    
    // Existenzprüfungen
    boolean existsByEmail(String email);
    
    boolean existsByNationalIdNumber(String nationalIdNumber);
}
