package com.example.marketplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DarajaService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${daraja.consumerKey}")
    private String consumerKey;

    @Value("${daraja.consumerSecret}")
    private String consumerSecret;

    @Value("${daraja.baseUrl}")
    private String baseUrl;

    @Value("${daraja.businessShortCode}")
    private String businessShortCode;

    @Value("${daraja.passkey}")
    private String passkey;

    @Value("${daraja.callback-url}")
    private String callbackUrl;

    /**
     * Generate OAuth token
     */
    public String generateAccessToken() {
        String url = baseUrl + "/oauth/v1/generate?grant_type=client_credentials";

        // Build Basic Auth header
        String auth = consumerKey + ":" + consumerSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access_token");
        }
        throw new RuntimeException("Failed to get access token from Daraja");
    }

    /**
     * Generate Timestamp (yyyyMMddHHmmss)
     */
    private String generateTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * Generate Password (Base64(BusinessShortCode + Passkey + Timestamp))
     */
    private String generatePassword(String timestamp) {
        String dataToEncode = businessShortCode + passkey + timestamp;
        return Base64.getEncoder().encodeToString(dataToEncode.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * STK Push Request
     */
    public Map<String, Object> lipaNaMpesa(String phoneNumber, double amount, String accountReference, String transactionDesc) {
        String accessToken = generateAccessToken();

        String url = baseUrl + "/mpesa/stkpush/v1/processrequest";

        String timestamp = generateTimestamp();
        String password = generatePassword(timestamp);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("BusinessShortCode", businessShortCode);
        request.put("Password", password);
        request.put("Timestamp", timestamp);
        request.put("TransactionType", "CustomerPayBillOnline");
        request.put("Amount", amount);
        request.put("PartyA", phoneNumber);
        request.put("PartyB", businessShortCode);
        request.put("PhoneNumber", phoneNumber);
        request.put("CallBackURL", callbackUrl);
        request.put("AccountReference", accountReference);
        request.put("TransactionDesc", transactionDesc);


        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to initiate STK Push request");
    }
}
