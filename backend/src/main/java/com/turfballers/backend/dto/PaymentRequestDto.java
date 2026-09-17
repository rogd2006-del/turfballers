package com.turfballers.backend.dto;

import com.turfballers.backend.model.Payment;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Request DTO for recording a payment */
@Data
public class PaymentRequestDto {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    private Long bookingId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;

    private LocalDate paymentDate;

    private Payment.PaymentStatus status;

    private String transactionRef;
}
