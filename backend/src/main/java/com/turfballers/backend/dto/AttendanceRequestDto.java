package com.turfballers.backend.dto;

import com.turfballers.backend.model.Attendance;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/** Request DTO for marking attendance */
@Data
public class AttendanceRequestDto {

    @NotNull(message = "Member ID is required")
    private Long memberId;

    private Long bookingId;

    private LocalDate date;

    @NotNull(message = "Status is required")
    private Attendance.AttendanceStatus status;

    private String remarks;
}
