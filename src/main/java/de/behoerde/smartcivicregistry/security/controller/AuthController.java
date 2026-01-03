package de.behoerde.smartcivicregistry.security.controller;

import de.behoerde.smartcivicregistry.security.auth.model.dto.request.LoginRequest;
import de.behoerde.smartcivicregistry.security.auth.model.dto.request.RegisterRequest;
import de.behoerde.smartcivicregistry.security.auth.model.dto.response.AuthenticationResponse;
import de.behoerde.smartcivicregistry.security.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // TODO: Implement mit LogoutService (TODO-224)
        return ResponseEntity.ok().build();
    }
}
