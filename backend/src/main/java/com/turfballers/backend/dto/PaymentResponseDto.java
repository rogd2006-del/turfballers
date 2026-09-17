package com.turfballers.backend.dto;

import com.turfballers.backend.model.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response DTO for payment data */
@Data
@Builder
public class PaymentResponseDto {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long bookingId;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private Payment.PaymentStatus status;
    private String transactionRef;
    private LocalDateTime createdAt;

    public static PaymentResponseDto from(Payment p) {
        return PaymentResponseDto.builder()
            .id(p.getId())
            .memberId(p.getMember().getId())
            .memberName(p.getMember().getFullName())
            .bookingId(p.getBooking() != null ? p.getBooking().getId() : null)
            .amount(p.getAmount())
            .paymentMethod(p.getPaymentMethod())
            .paymentDate(p.getPaymentDate())
            .status(p.getStatus())
            .transactionRef(p.getTransactionRef())
            .createdAt(p.getCreatedAt())
            .build();
    }
}
