package de.behoerde.smartcivicregistry.person.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonName {
    
    @Column(name = "title", length = 50)
    private String title; // Dr., Prof., etc.
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "middle_name", length = 100)
    private String middleName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "maiden_name", length = 100)
    private String maidenName; // Geburtsname
    
    // Geschäftslogik Methoden
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
}
