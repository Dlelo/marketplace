package com.example.marketplace.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "daraja")
@Getter
@Setter
public class DarajaConfig {
    private String consumerKey;
    private String consumerSecret;
    private String shortCode;      // Business shortcode
    private String passkey;        // Lipa Na M-Pesa passkey
    private String callbackUrl;    // Your endpoint for Daraja to call after STK push
}
