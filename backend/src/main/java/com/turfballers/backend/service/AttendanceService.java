package com.turfballers.backend.service;

import com.turfballers.backend.dto.AttendanceRequestDto;
import com.turfballers.backend.dto.AttendanceResponseDto;
import com.turfballers.backend.exception.ResourceNotFoundException;
import com.turfballers.backend.model.Attendance;
import com.turfballers.backend.model.Booking;
import com.turfballers.backend.model.Member;
import com.turfballers.backend.repository.AttendanceRepository;
import com.turfballers.backend.repository.BookingRepository;
import com.turfballers.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attendance service - mark attendance and compute member statistics.
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final BookingRepository bookingRepository;

    /** All attendance records for a specific date */
    public List<AttendanceResponseDto> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date)
            .stream().map(AttendanceResponseDto::from).toList();
    }

    /** All attendance records for a specific member */
    public List<AttendanceResponseDto> getAttendanceByMember(Long memberId) {
        return attendanceRepository.findByMemberIdOrderByDateDesc(memberId)
            .stream().map(AttendanceResponseDto::from).toList();
    }

    /** All attendance records */
    public List<AttendanceResponseDto> getAllAttendance() {
        return attendanceRepository.findAll()
            .stream().map(AttendanceResponseDto::from).toList();
    }

    /** Mark attendance for a member */
    public AttendanceResponseDto markAttendance(AttendanceRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();

        // Update existing if already marked today
        if (attendanceRepository.existsByMemberIdAndDate(dto.getMemberId(), date)) {
            Attendance existing = attendanceRepository.findByMemberId(dto.getMemberId())
                .stream().filter(a -> a.getDate().equals(date)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
            existing.setStatus(dto.getStatus());
            existing.setRemarks(dto.getRemarks());
            return AttendanceResponseDto.from(attendanceRepository.save(existing));
        }

        Booking booking = null;
        if (dto.getBookingId() != null) {
            booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", dto.getBookingId()));
        }

        Attendance attendance = Attendance.builder()
            .member(member)
            .booking(booking)
            .date(date)
            .status(dto.getStatus())
            .remarks(dto.getRemarks())
            .build();
        return AttendanceResponseDto.from(attendanceRepository.save(attendance));
    }

    /** Compute attendance percentage for a member */
    public Map<String, Object> getMemberAttendanceStats(Long memberId) {
        long total = attendanceRepository.countByMemberId(memberId);
        long present = attendanceRepository.countByMemberIdAndStatus(memberId, Attendance.AttendanceStatus.PRESENT);
        double percentage = total > 0 ? (double) present / total * 100 : 0;
        Map<String, Object> stats = new HashMap<>();
        stats.put("memberId", memberId);
        stats.put("totalSessions", total);
        stats.put("presentCount", present);
        stats.put("absentCount", total - present);
        stats.put("attendancePercentage", Math.round(percentage * 10.0) / 10.0);
        return stats;
    }
}
