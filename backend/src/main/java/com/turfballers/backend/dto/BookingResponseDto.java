package com.turfballers.backend.dto;

import com.turfballers.backend.model.Booking;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Response DTO for booking data returned to the frontend */
@Data
@Builder
public class BookingResponseDto {
    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String turfName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double durationHours;
    private BigDecimal amount;
    private Booking.BookingStatus status;
    private String notes;
    private LocalDateTime createdAt;

    public static BookingResponseDto from(Booking b) {
        return BookingResponseDto.builder()
            .id(b.getId())
            .memberId(b.getMember().getId())
            .memberName(b.getMember().getFullName())
            .memberEmail(b.getMember().getEmail())
            .turfName(b.getTurfName())
            .bookingDate(b.getBookingDate())
            .startTime(b.getStartTime())
            .endTime(b.getEndTime())
            .durationHours(b.getDurationHours())
            .amount(b.getAmount())
            .status(b.getStatus())
            .notes(b.getNotes())
            .createdAt(b.getCreatedAt())
            .build();
    }
}
