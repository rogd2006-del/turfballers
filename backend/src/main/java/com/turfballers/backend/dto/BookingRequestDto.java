package com.turfballers.backend.dto;

import com.turfballers.backend.model.Booking;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/** Request DTO for creating/updating a booking */
@Data
public class BookingRequestDto {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotBlank(message = "Turf name is required")
    private String turfName;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    private Booking.BookingStatus status;

    private String notes;
}
