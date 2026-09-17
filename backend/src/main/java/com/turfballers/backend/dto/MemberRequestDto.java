package com.turfballers.backend.dto;

import com.turfballers.backend.model.Member;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/** Request DTO for creating/updating a member */
@Data
public class MemberRequestDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,15}$", message = "Invalid phone number")
    private String phone;

    @NotNull(message = "Membership plan is required")
    private Member.MembershipPlan membershipPlan;

    private LocalDate joinDate;

    private Member.MemberStatus status;
}
