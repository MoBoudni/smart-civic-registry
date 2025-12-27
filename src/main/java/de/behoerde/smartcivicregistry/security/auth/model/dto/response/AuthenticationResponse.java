package de.behoerde.smartcivicregistry.security.auth.model.dto.response;

import de.behoerde.smartcivicregistry.security.auth.model.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
}
