package de.behoerde.smartcivicregistry.security.auth.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Email darf nicht leer sein")
    @Email(message = "Muss eine gültige E-Mail-Adresse sein")
    @Size(max = 255, message = "Email darf maximal 255 Zeichen lang sein")
    private String email;
    
    @NotBlank(message = "Passwort darf nicht leer sein")
    @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
        message = "Passwort muss mindestens eine Zahl, einen Kleinbuchstaben, einen Großbuchstaben und ein Sonderzeichen enthalten"
    )
    private String password;
    
    @NotBlank(message = "Vorname darf nicht leer sein")
    @Size(min = 2, max = 100, message = "Vorname muss zwischen 2 und 100 Zeichen lang sein")
    private String firstName;
    
    @NotBlank(message = "Nachname darf nicht leer sein")
    @Size(min = 2, max = 100, message = "Nachname muss zwischen 2 und 100 Zeichen lang sein")
    private String lastName;
}
