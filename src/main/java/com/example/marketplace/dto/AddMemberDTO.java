package com.example.marketplace.dto;

import lombok.Data;

/** Request body for adding an existing agent user to an agency. */
@Data
public class AddMemberDTO {
    /** Phone number of the user who already has ROLE_AGENT. */
    private String phone;
    /** ADMIN or EMPLOYEE — defaults to ADMIN if omitted. */
    private String role;
}
