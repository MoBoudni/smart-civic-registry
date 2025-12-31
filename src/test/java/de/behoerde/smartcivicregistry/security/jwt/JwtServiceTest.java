package de.behoerde.smartcivicregistry.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    
    @Mock
    private JwtProperties jwtProperties;
    
    private JwtService jwtService;
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecretKey()).thenReturn("VGVzdCBzZWNyZXQga2V5IGZvciBKWVQgdGVzdGluZyBvbmx5IQ==");
        when(jwtProperties.getExpiration()).thenReturn(3600000L);
        
        jwtService = new JwtService(jwtProperties);
        userDetails = new User("test@behoerde.de", "password", Collections.emptyList());
    }
    
    @Test
    void generateToken_ShouldReturnValidToken() {
        // When
        String token = jwtService.generateToken(userDetails);
        
        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 50);
        assertTrue(token.contains(".")); // JWT hat 3 Teile
    }
    
    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);
        
        // When
        String username = jwtService.extractUsername(token);
        
        // Then
        assertEquals("test@behoerde.de", username);
    }
    
    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtService.generateToken(userDetails);
        
        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        // Then
        assertTrue(isValid, "Valid token should return true");
    }
    
    @Test
    void isTokenValid_WithDifferentUser_ShouldReturnFalse() {
        // Given
        String token = jwtService.generateToken(userDetails);
        UserDetails differentUser = new User("other@behoerde.de", "password", Collections.emptyList());
        
        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);
        
        // Then
        assertFalse(isValid, "Token with different user should return false");
    }
    
    @Test
    void isTokenValid_WithNullToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtService.isTokenValid(null, userDetails);
        
        // Then
        assertFalse(isValid, "Null token should return false");
    }
    
    @Test
    void isTokenValid_WithEmptyToken_ShouldReturnFalse() {
        // When
        boolean isValid = jwtService.isTokenValid("", userDetails);
        
        // Then
        assertFalse(isValid, "Empty token should return false");
    }
}
