package de.behoerde.smartcivicregistry.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private JwtProperties jwtProperties;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Use doReturn to avoid Mockito stubbing issues
        doReturn("VGhpc0lzQVNlY3VyZVNlY3JldEtleUZvclJ3dFRva2VuR2VuZXJhdGlvbjIwMjQh")
                .when(jwtProperties).getSecretKey();
        doReturn(86400000L).when(jwtProperties).getExpiration();

        // Set the mocked properties to the JwtService instance
        ReflectionTestUtils.setField(jwtService, "jwtProperties", jwtProperties);

        // Create a UserDetails object for testing
        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("Should generate JWT token successfully")
    void generateToken_WithValidUserDetails_ReturnsToken() {
        // When - generate a real token
        String token = jwtService.generateToken(userDetails);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should validate token successfully")
    void isTokenValid_WithValidToken_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid token")
    void isTokenValid_WithInvalidToken_ReturnsFalse() {
        // Given - an invalid token (empty string)
        String invalidToken = "";

        // When
        boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_WithValidToken_ReturnsUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertThat(username).isEqualTo("testuser");
    }
}