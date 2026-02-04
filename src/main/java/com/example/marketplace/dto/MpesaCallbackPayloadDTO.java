package com.example.marketplace.dto;

import lombok.Data;

@Data
public class MpesaCallbackPayloadDTO {
    private Body Body;

    @Data
    public static class Body {
        private StkCallback stkCallback;
    }

    @Data
    public static class StkCallback {
        private String CheckoutRequestID;
        private Integer ResultCode;
    }
}
