package de.behoerde.smartcivicregistry.security.auth.service;

import de.behoerde.smartcivicregistry.security.auth.model.domain.User;
import de.behoerde.smartcivicregistry.security.auth.model.domain.UserRole;
import de.behoerde.smartcivicregistry.security.auth.model.dto.request.RegisterRequest;
import de.behoerde.smartcivicregistry.security.auth.model.dto.request.LoginRequest;
import de.behoerde.smartcivicregistry.security.auth.model.dto.response.AuthenticationResponse;
import de.behoerde.smartcivicregistry.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
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
        // Prüfen ob User bereits existiert
        if (userService.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        // User erstellen
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(UserRole.ROLE_USER)  // GEÄNDERT: UserRole.ROLE_USER
                .enabled(true)
                .build();
        
        // User speichern
        User savedUser = userService.save(user);
        
        // JWT Token generieren
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .authorities(savedUser.getRole().name())
                .build();
        
        String token = jwtService.generateToken(userDetails);
        
        return AuthenticationResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .build();
    }
    
    public AuthenticationResponse login(LoginRequest request) {
        // Authentifizierung durchführen
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // User laden
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // JWT Token generieren
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
        
        String token = jwtService.generateToken(userDetails);
        
        return AuthenticationResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
