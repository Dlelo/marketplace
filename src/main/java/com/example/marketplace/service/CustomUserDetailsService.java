package com.example.marketplace.service;

import com.example.marketplace.model.User;
import com.example.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        String normalizedIdentifier = normalizeIdentifier(identifier);

        User user = userRepository.findByEmail(normalizedIdentifier)
                .or(() -> userRepository.findByPhoneNumber(normalizedIdentifier))
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email or phone")
                );

        List<GrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        String principal =
                (user.getEmail() != null && !user.getEmail().isBlank())
                        ? user.getEmail()
                        : user.getPhoneNumber();

        if (principal == null || principal.isBlank()) {
            throw new UsernameNotFoundException("User has no valid login identifier");
        }

        return new org.springframework.security.core.userdetails.User(
                principal,
                user.getPassword(),
                authorities
        );
    }

    /**
     * Optional but recommended for Kenya 🇰🇪
     */
    private String normalizeIdentifier(String identifier) {
        if (identifier == null) return null;

        identifier = identifier.trim();

        if (identifier.matches("^07\\d{8}$")) {
            return "+254" + identifier.substring(1);
        }

        return identifier;
    }
}