package com.turfballers.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payment entity - financial transactions for bookings and memberships.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_member", columnList = "member_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_date", columnList = "payment_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "transaction_ref")
    private String transactionRef;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paymentDate == null) paymentDate = LocalDate.now();
        if (status == null) status = PaymentStatus.PAID;
    }

    // ---- Enums ----

    public enum PaymentMethod { UPI, CASH, CARD, NET_BANKING }

    public enum PaymentStatus { PAID, PENDING, FAILED }
}
