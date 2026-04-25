package com.example.marketplace.repository;

import com.example.marketplace.model.PasswordResetToken;
import com.example.marketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    /** Invalidate any outstanding tokens for the user before issuing a new one. */
    void deleteAllByUser(User user);
}
