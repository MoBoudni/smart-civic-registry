// src/main/java/de/behoerde/smartcivicregistry/security/auth/service/UserDetailsServiceImpl.java
package de.behoerde.smartcivicregistry.security.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Später mit echter Datenbank-Implementierung ersetzen
        if ("admin@behoerde.de".equals(username)) {
            return User.builder()
                    .username("admin@behoerde.de")
                    .password("$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW") // password = "password"
                    .authorities(Collections.emptyList())
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}