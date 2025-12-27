// src/main/java/de/behoerde/smartcivicregistry/security/config/JwtAccessDeniedHandler.java
package de.behoerde.smartcivicregistry.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.behoerde.smartcivicregistry.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpServletResponse.SC_FORBIDDEN)
                .error("Forbidden")
                .message("Zugriff verweigert: Sie haben keine Berechtigung für diese Aktion")
                .path(request.getServletPath())
                .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}