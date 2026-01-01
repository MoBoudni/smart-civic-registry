package de.behoerde.smartcivicregistry.security.auth.service;

import de.behoerde.smartcivicregistry.security.auth.model.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
