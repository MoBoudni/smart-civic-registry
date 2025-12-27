package de.behoerde.smartcivicregistry.security.auth.service;

import de.behoerde.smartcivicregistry.security.auth.model.domain.User;
import de.behoerde.smartcivicregistry.security.auth.model.domain.UserRole;
import de.behoerde.smartcivicregistry.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationResponse register(RegisterRequest request) {
        // Prüfe ob User bereits existiert
        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User mit dieser Email existiert bereits");
        }
        
        // Erstelle neuen User
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .role(UserRole.ROLE_USER) // Standard-Rolle
            .enabled(true)
            .build();
        
        // Speichere User
        User savedUser = userService.save(user);
        
        // Generiere JWT Token
        String jwtToken = jwtService.generateToken(savedUser);
        
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .email(savedUser.getEmail())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .role(savedUser.getRole())
            .build();
    }
    
    public AuthenticationResponse login(LoginRequest request) {
        // Authentifiziere mit Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // Lade User aus Datenbank
        User user = userService.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
        
        // Generiere JWT Token
        String jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
            .token(jwtToken)
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .build();
    }
}
