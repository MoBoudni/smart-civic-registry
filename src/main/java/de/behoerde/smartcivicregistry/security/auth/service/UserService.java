package de.behoerde.smartcivicregistry.security.auth.service;

import de.behoerde.smartcivicregistry.security.auth.model.domain.User;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
