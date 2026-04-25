package com.example.marketplace.config;

import com.example.marketplace.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Resolves @LastModifiedBy / @CreatedBy to the authenticated user's ID.
 * Returns Optional.empty() for anonymous principals so the column stays null.
 */
@Configuration
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<Long> auditorProvider(UserRepository userRepository) {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.empty();
            }
            String email = auth.getName();
            return userRepository.findByEmail(email).map(u -> u.getId());
        };
    }
}
