package com.turfballers.backend.dto;

import com.turfballers.backend.model.Attendance;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Response DTO for attendance records */
@Data
@Builder
public class AttendanceResponseDto {
    private Long id;
    private Long memberId;
    private String memberName;
    private Long bookingId;
    private LocalDate date;
    private Attendance.AttendanceStatus status;
    private String remarks;
    private LocalDateTime createdAt;

    public static AttendanceResponseDto from(Attendance a) {
        return AttendanceResponseDto.builder()
            .id(a.getId())
            .memberId(a.getMember().getId())
            .memberName(a.getMember().getFullName())
            .bookingId(a.getBooking() != null ? a.getBooking().getId() : null)
            .date(a.getDate())
            .status(a.getStatus())
            .remarks(a.getRemarks())
            .createdAt(a.getCreatedAt())
            .build();
    }
}
