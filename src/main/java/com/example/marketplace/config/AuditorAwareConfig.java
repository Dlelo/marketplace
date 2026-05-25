package com.example.marketplace.config;

import com.example.marketplace.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Resolves @LastModifiedBy / @CreatedBy to the authenticated user's ID.
 * Must NOT call any repository here — doing so triggers Hibernate auto-flush
 * inside a pre-update callback, causing infinite recursion (StackOverflowError).
 */
@Configuration
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return Optional.empty();
            }
            Object principal = auth.getPrincipal();
            if (principal instanceof CustomUserDetails customUserDetails) {
                return Optional.ofNullable(customUserDetails.getUser().getId());
            }
            return Optional.empty();
        };
    }
}
