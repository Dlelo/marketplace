package com.example.marketplace.dto;

import com.example.marketplace.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HireRequestListDTO {
    private Long id;
    private RequestStatus status;
    private boolean paid;
    private LocalDateTime createdAt;
    private HouseHelpRef houseHelp;
    private HomeOwnerRef homeOwner;

    @Data
    public static class HouseHelpRef {
        private Long id;
        private String name;
    }

    @Data
    public static class HomeOwnerRef {
        private Long id;
        private UserRef user;
    }

    @Data
    public static class UserRef {
        private Long id;
        private String name;
    }
}
