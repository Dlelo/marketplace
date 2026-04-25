package com.example.marketplace.service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper around Twilio Programmable SMS. When unconfigured (no
 * TWILIO_ACCOUNT_SID), {@link #send} logs the payload and returns false so the
 * surrounding flow keeps working in dev.
 */
@Slf4j
@Service
public class SmsService {

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private boolean initialized = false;

    public SmsService(
            @Value("${twilio.account-sid:}") String accountSid,
            @Value("${twilio.auth-token:}") String authToken,
            @Value("${twilio.from-number:}") String fromNumber
    ) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
    }

    @PostConstruct
    void init() {
        if (isConfigured()) {
            Twilio.init(accountSid, authToken);
            initialized = true;
        }
    }

    public boolean isConfigured() {
        return accountSid != null && !accountSid.isBlank()
                && authToken != null && !authToken.isBlank()
                && fromNumber != null && !fromNumber.isBlank();
    }

    /** Returns true on accepted-by-Twilio (200/queued/sent), false otherwise. */
    public boolean send(String toPhoneE164, String body) {
        if (toPhoneE164 == null || toPhoneE164.isBlank()) {
            log.warn("Skipping SMS — recipient phone number is blank.");
            return false;
        }
        if (!initialized) {
            log.warn("Twilio not configured; would send SMS to {}: {}", toPhoneE164, body);
            return false;
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneE164),
                    new PhoneNumber(fromNumber),
                    body
            ).create();
            log.info("SMS sent to {} (sid={}, status={})", toPhoneE164, message.getSid(), message.getStatus());
            return true;
        } catch (ApiException e) {
            log.error("Twilio SMS failed for {}: code={} message={}", toPhoneE164, e.getCode(), e.getMessage());
            return false;
        }
    }
}
