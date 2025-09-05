package com.example.marketplace.service;

import com.example.marketplace.config.DarajaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DarajaService {

    private final DarajaConfig darajaConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OAUTH_URL = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    private static final String STK_PUSH_URL = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

    public String getAccessToken() {
        String credentials = darajaConfig.getConsumerKey() + ":" + darajaConfig.getConsumerSecret();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + encoded);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(OAUTH_URL, HttpMethod.GET, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }

    public Map<String, Object> lipaNaMpesa(String phoneNumber, double amount, String accountReference, String transactionDesc) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder().encodeToString(
                (darajaConfig.getShortCode() + darajaConfig.getPasskey() + timestamp).getBytes()
        );

        Map<String, Object> body = new HashMap<>();
        body.put("BusinessShortCode", darajaConfig.getShortCode());
        body.put("Password", password);
        body.put("Timestamp", timestamp);
        body.put("TransactionType", "CustomerPayBillOnline");
        body.put("Amount", amount);
        body.put("PartyA", phoneNumber);
        body.put("PartyB", darajaConfig.getShortCode());
        body.put("PhoneNumber", phoneNumber);
        body.put("CallBackURL", darajaConfig.getCallbackUrl());
        body.put("AccountReference", accountReference);
        body.put("TransactionDesc", transactionDesc);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(STK_PUSH_URL, request, Map.class);
        return response.getBody();
    }
}
