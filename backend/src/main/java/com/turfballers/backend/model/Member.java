package com.turfballers.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Member entity - represents a turf facility customer/member.
 */
@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_member_email", columnList = "email"),
    @Index(name = "idx_member_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(name = "membership_plan", nullable = false)
    @Enumerated(EnumType.STRING)
    private MembershipPlan membershipPlan;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (joinDate == null) joinDate = LocalDate.now();
        if (status == null) status = MemberStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ---- Enums ----

    public enum MembershipPlan { BASIC, STANDARD, VIP }

    public enum MemberStatus { ACTIVE, INACTIVE }
}
