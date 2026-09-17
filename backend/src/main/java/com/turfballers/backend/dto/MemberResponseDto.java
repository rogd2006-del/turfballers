package com.turfballers.backend.dto;

import com.turfballers.backend.model.Member;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response DTO for member data returned to the frontend */
@Data
@Builder
public class MemberResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Member.MembershipPlan membershipPlan;
    private LocalDate joinDate;
    private Member.MemberStatus status;
    private LocalDateTime createdAt;

    public static MemberResponseDto from(Member m) {
        return MemberResponseDto.builder()
            .id(m.getId())
            .fullName(m.getFullName())
            .email(m.getEmail())
            .phone(m.getPhone())
            .membershipPlan(m.getMembershipPlan())
            .joinDate(m.getJoinDate())
            .status(m.getStatus())
            .createdAt(m.getCreatedAt())
            .build();
    }
}
