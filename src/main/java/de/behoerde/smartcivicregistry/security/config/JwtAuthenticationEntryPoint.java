// src/main/java/de/behoerde/smartcivicregistry/security/config/JwtAuthenticationEntryPoint.java
package de.behoerde.smartcivicregistry.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.behoerde.smartcivicregistry.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized")
                .message("Zugriff verweigert: Ungültige oder fehlende Authentifizierung")
                .path(request.getServletPath())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}