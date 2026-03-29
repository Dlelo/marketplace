package com.example.marketplace.dto;

import lombok.Data;

@Data
public class HomeOwnerListDTO {
    private Long id;
    private boolean active;
    private UserSummary user;

    @Data
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
    }
}
