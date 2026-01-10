package de.behoerde.smartcivicregistry.unit.security;

import de.behoerde.smartcivicregistry.security.auth.model.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestValidationTest {
    
    private static Validator validator;
    
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void validRegisterRequest_ShouldPassValidation() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.de")
                .password("SecurePass123!")
                .firstName("Max")
                .lastName("Mustermann")
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations).isEmpty();
        System.out.println("✅ Test 1 passed: Valid request passes validation");
    }
    
    @Test
    void registerRequest_WithNullEmail_ShouldFailValidation() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email(null)
                .password("SecurePass123!")
                .firstName("Max")
                .lastName("Mustermann")
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations).isNotEmpty();
        System.out.println("✅ Test 2 passed: Null email fails validation");
    }
    
    @Test
    void registerRequest_WithInvalidEmail_ShouldFailValidation() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("invalid-email")
                .password("SecurePass123!")
                .firstName("Max")
                .lastName("Mustermann")
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations).isNotEmpty();
        System.out.println("✅ Test 3 passed: Invalid email format fails validation");
    }
    
    @Test
    void registerRequest_WithShortPassword_ShouldFailValidation() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.de")
                .password("12345") // 5 chars, should fail
                .firstName("Max")
                .lastName("Mustermann")
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations).isNotEmpty();
        System.out.println("✅ Test 4 passed: Short password fails validation");
    }
    
    @Test
    void registerRequest_WithNullFirstName_ShouldFailValidation() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.de")
                .password("SecurePass123!")
                .firstName(null)
                .lastName("Mustermann")
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations).isNotEmpty();
        System.out.println("✅ Test 5 passed: Null first name fails validation");
    }
    
    @Test
    void registerRequest_WithNullLastName_ShouldFailValidation() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.de")
                .password("SecurePass123!")
                .firstName("Max")
                .lastName(null)
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations).isNotEmpty();
        System.out.println("✅ Test 6 passed: Null last name fails validation");
    }
    
    @Test
    void registerRequest_AllFieldsNull_ShouldReturnMultipleErrors() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email(null)
                .password(null)
                .firstName(null)
                .lastName(null)
                .build();
        
        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        
        // Then
        assertThat(violations.size()).isGreaterThanOrEqualTo(4);
        System.out.println("✅ Test 7 passed: All null fields return multiple errors");
    }
}
