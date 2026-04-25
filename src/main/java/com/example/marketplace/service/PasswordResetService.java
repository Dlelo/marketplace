package com.example.marketplace.service;

import com.example.marketplace.model.PasswordResetToken;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.PasswordResetTokenRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final long TOKEN_TTL_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;

    public record InitiateResult(String sessionToken, String channel) {}

    /**
     * Generate a 6-digit OTP, store its BCrypt hash, and deliver via SMS (phone)
     * or email. Returns a session token that the client uses to complete the
     * reset; or null when the identifier doesn't match anyone (caller still
     * shows a generic ack to prevent enumeration).
     */
    @Transactional
    public InitiateResult initiateReset(String identifier) {
        if (identifier == null || identifier.isBlank()) return null;

        String trimmed = identifier.trim();
        boolean looksLikePhone = trimmed.startsWith("+") || trimmed.matches("^\\d{6,}$");

        Optional<User> userOpt = looksLikePhone
                ? userRepository.findByPhoneNumber(trimmed).or(() -> userRepository.findByEmail(trimmed.toLowerCase()))
                : userRepository.findByEmail(trimmed.toLowerCase()).or(() -> userRepository.findByPhoneNumber(trimmed));

        if (userOpt.isEmpty()) {
            log.info("Password reset requested for unknown identifier: {}", identifier);
            return null;
        }

        User user = userOpt.get();
        tokenRepository.deleteAllByUser(user);

        String code = generateCode();
        Instant now = Instant.now();
        PasswordResetToken token = PasswordResetToken.builder()
                .token(UUID.randomUUID().toString())
                .codeHash(passwordEncoder.encode(code))
                .user(user)
                .createdAt(now)
                .expiresAt(now.plus(TOKEN_TTL_MINUTES, ChronoUnit.MINUTES))
                .attempts(0)
                .build();
        tokenRepository.save(token);

        String channel = deliverCode(user, code, looksLikePhone);
        return new InitiateResult(token.getToken(), channel);
    }

    private String deliverCode(User user, String code, boolean prefersSms) {
        boolean smsAttempted = false;
        if (prefersSms && user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            String body = "YayaConnect: your password reset code is " + code
                    + ". It expires in " + TOKEN_TTL_MINUTES + " minutes. Don't share it.";
            smsAttempted = true;
            if (smsService.send(user.getPhoneNumber(), body)) {
                return "sms";
            }
            // fall through to email
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (emailService.sendPasswordResetOtp(user.getEmail(), user.getName(), code, TOKEN_TTL_MINUTES)) {
                return "email";
            }
        }

        // Final fallback for dev/no-provider setups: log the code so an operator can recover.
        log.info("Password reset OTP for user {} (id={}, smsAttempted={}): {}",
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(),
                user.getId(), smsAttempted, code);
        return "logged";
    }

    /** Verify the OTP for the supplied session token and rotate the password. */
    @Transactional
    public void completeReset(String sessionToken, String code, String newPassword) {
        if (sessionToken == null || sessionToken.isBlank()) {
            throw new IllegalArgumentException("Session expired. Request a new code.");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Enter the code you received.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }

        PasswordResetToken token = tokenRepository.findByToken(sessionToken)
                .orElseThrow(() -> new IllegalArgumentException("Session expired. Request a new code."));

        if (!token.isValid()) {
            throw new IllegalArgumentException("This reset session has expired. Request a new code.");
        }
        if (token.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalArgumentException("Too many wrong attempts. Request a new code.");
        }

        if (!passwordEncoder.matches(code.trim(), token.getCodeHash())) {
            token.setAttempts(token.getAttempts() + 1);
            tokenRepository.save(token);
            throw new IllegalArgumentException("Code is incorrect. Try again.");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        tokenRepository.save(token);
    }

    @Transactional
    public void changePassword(User user, String currentPassword, String newPassword) {
        if (currentPassword == null || newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters.");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private String generateCode() {
        int n = RANDOM.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}
