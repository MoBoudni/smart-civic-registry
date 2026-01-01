package de.behoerde.smartcivicregistry.security.auth.service;

import de.behoerde.smartcivicregistry.security.auth.model.domain.User;
import de.behoerde.smartcivicregistry.security.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;  // <- Hinzugefügt für konsistente Lombok-Nutzung

import java.util.Optional;

@Service
@RequiredArgsConstructor  // <- Ersetzt @Autowired Konstruktor (besser mit final)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // <- GEHINZUFÜGT: Für Spring Security AuthenticationManager
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .accountExpired(!user.isEnabled())
                .accountLocked(!user.isEnabled())
                .credentialsExpired(!user.isEnabled())
                .disabled(!user.isEnabled())
                .build();
    }
}
