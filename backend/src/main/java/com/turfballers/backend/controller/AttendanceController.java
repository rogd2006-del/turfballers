package com.turfballers.backend.controller;

import com.turfballers.backend.dto.AttendanceRequestDto;
import com.turfballers.backend.dto.AttendanceResponseDto;
import com.turfballers.backend.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Attendance REST controller.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<AttendanceResponseDto>> getAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long memberId) {
        if (memberId != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByMember(memberId));
        }
        if (date != null) {
            return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
        }
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @PostMapping
    public ResponseEntity<AttendanceResponseDto> markAttendance(@Valid @RequestBody AttendanceRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.markAttendance(dto));
    }

    @GetMapping("/stats/{memberId}")
    public ResponseEntity<Map<String, Object>> getMemberStats(@PathVariable Long memberId) {
        return ResponseEntity.ok(attendanceService.getMemberAttendanceStats(memberId));
    }
}
