package com.example.marketplace.service;

//import com.example.marketplace.dto.MpesaCallbackRequest;
import com.example.marketplace.dto.PaymentResponseDTO;
import com.example.marketplace.dto.UserResponseDTO;
import com.example.marketplace.enums.PaymentStatus;
import com.example.marketplace.model.Agent;
import com.example.marketplace.model.Payment;
import com.example.marketplace.model.Role;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.PaymentRepository;
import com.example.marketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Expectation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final DarajaService darajaService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    /**
     * Initiates M-Pesa STK Push payment
     */
    public Map<String, Object> initiatePayment(String phoneNumber, double amount, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        String accountReference = "Subscription-" + email;
        String transactionDesc = "Subscription Payment";

//        TODO Bring back the below once we go live
//        Map<String, Object> response = darajaService.lipaNaMpesa(phoneNumber, amount, accountReference, transactionDesc);

        // Save pending payment
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String transactionId = phoneNumber + "-" + today;
        Payment payment = Payment.builder()
                .user(user)
//                .transactionId(response.get("CheckoutRequestID").toString())
                .transactionId(transactionId)
                .amount(amount)
                .provider("M-PESA")
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

//        return response;
        Map<String, Object> mutedResponse = new HashMap<>();
        mutedResponse.put("message", "Payment request received. Awaiting confirmation.");
        mutedResponse.put("checkoutRequestId", null);
        mutedResponse.put("status", "PENDING");

        return mutedResponse;
    }

    /**
     * Process callback from Daraja
     */
    @SuppressWarnings("unchecked")
    public void handleDarajaCallback(Map<String, Object> payload) {
        Map<String, Object> body = (Map<String, Object>) payload.get("Body");
        Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");

        String checkoutRequestId = (String) stkCallback.get("CheckoutRequestID");
        int resultCode = (Integer) stkCallback.get("ResultCode");
        String resultDesc = (String) stkCallback.get("ResultDesc");

        Payment payment = paymentRepository.findFirstByTransactionId(checkoutRequestId)
                .orElseThrow(() -> new RuntimeException("Payment not found for CheckoutRequestID: " + checkoutRequestId));

        if (resultCode == 0) {
            // Extract metadata
            Map<String, Object> callbackMetadata = (Map<String, Object>) stkCallback.get("CallbackMetadata");
            if (callbackMetadata != null) {
                var items = (Iterable<Map<String, Object>>) callbackMetadata.get("Item");
                for (Map<String, Object> item : items) {
                    String name = (String) item.get("Name");
                    Object value = item.get("Value");

                    if ("MpesaReceiptNumber".equals(name)) {
                        payment.setTransactionId(value.toString());
                    }
                    if ("Amount".equals(name)) {
                        payment.setAmount(Double.valueOf(value.toString()));
                    }
                }
            }

            payment.setStatus(PaymentStatus.SUCCESS);
            subscriptionService.handleSuccessfulPayment(payment.getUser().getEmail(), payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
    }

    public boolean hasSuccessfulPayment(String email) {
        return paymentRepository.existsByUser_EmailAndStatus(email, PaymentStatus.SUCCESS);
    }

    public Optional<Payment> findLatestSuccessfulPayment(String email) {
        return paymentRepository.findFirstByUser_EmailAndStatusOrderByCreatedAtDesc(email, PaymentStatus.SUCCESS);
    }

    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(payment -> {
                    PaymentResponseDTO dto = new PaymentResponseDTO();
                    dto.setId(payment.getId());
                    dto.setTransactionId(payment.getTransactionId());
                    dto.setAmount(payment.getAmount());
                    dto.setProvider(payment.getProvider());
                    dto.setStatus(payment.getStatus());
                    dto.setCreatedAt(payment.getCreatedAt());
                    dto.setUserId(payment.getUser().getId());
                    dto.setUserEmail(payment.getUser().getEmail());
                    return dto;
                });
    }

    public PaymentResponseDTO findByTransactionId(String transactionId){
        Payment payment =  paymentRepository.findFirstByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
        PaymentResponseDTO paymentDto = new PaymentResponseDTO();
        paymentDto.setId(payment.getId());
        paymentDto.setTransactionId(payment.getTransactionId());
        paymentDto.setStatus(payment.getStatus());
//        paymentDto.setUserEmail(payment.getUserEmail());

        return paymentDto;
    }

}
