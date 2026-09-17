package com.turfballers.backend.service;

import com.turfballers.backend.dto.BookingResponseDto;
import com.turfballers.backend.dto.DashboardStatsDto;
import com.turfballers.backend.model.Member;
import com.turfballers.backend.repository.AttendanceRepository;
import com.turfballers.backend.repository.BookingRepository;
import com.turfballers.backend.repository.MemberRepository;
import com.turfballers.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard service - aggregates KPIs, chart data, and recent activity.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MemberRepository memberRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;

    public DashboardStatsDto getStats() {
        LocalDate today = LocalDate.now();
        LocalDate sixMonthsAgo = today.minusMonths(6).withDayOfMonth(1);

        // KPI Counters
        long totalMembers  = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(Member.MemberStatus.ACTIVE);
        long totalBookings = bookingRepository.count();
        long todayBookings = bookingRepository.countByBookingDate(today);
        BigDecimal monthlyRevenue  = bookingRepository.sumCurrentMonthRevenue();
        BigDecimal pendingPayments = paymentRepository.sumPendingAmount();
        BigDecimal totalRevenue    = bookingRepository.sumConfirmedRevenue();

        // Monthly revenue chart - last 6 months
        List<Object[]> monthlyData = bookingRepository.findMonthlyRevenue(sixMonthsAgo);
        List<String> revenueLabels = new ArrayList<>();
        List<BigDecimal> revenueValues = new ArrayList<>();
        // Fill gaps with 0 for months with no bookings
        Map<String, BigDecimal> revenueMap = new LinkedHashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate d = today.minusMonths(i);
            String key = d.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + d.getYear();
            revenueMap.put(key, BigDecimal.ZERO);
        }
        for (Object[] row : monthlyData) {
            int month = ((Number) row[0]).intValue();
            int year  = ((Number) row[1]).intValue();
            BigDecimal rev = (BigDecimal) row[2];
            String key = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
            if (revenueMap.containsKey(key)) revenueMap.put(key, rev);
        }
        revenueMap.forEach((k, v) -> { revenueLabels.add(k); revenueValues.add(v); });

        // Turf breakdown for doughnut chart
        List<Object[]> turfData = bookingRepository.countByTurf();
        List<String> turfLabels = new ArrayList<>();
        List<Long> turfCounts = new ArrayList<>();
        for (Object[] row : turfData) {
            turfLabels.add((String) row[0]);
            turfCounts.add(((Number) row[1]).longValue());
        }

        // Attendance rate chart - last 7 days
        List<Object[]> attendanceData = attendanceRepository.findAttendanceStats(today.minusDays(6));
        List<String> attendanceDateLabels = new ArrayList<>();
        List<Double> attendanceRates = new ArrayList<>();
        for (Object[] row : attendanceData) {
            attendanceDateLabels.add(row[0].toString());
            long total   = ((Number) row[1]).longValue();
            long present = ((Number) row[2]).longValue();
            double rate = total > 0 ? (double) present / total * 100 : 0;
            attendanceRates.add(Math.round(rate * 10.0) / 10.0);
        }

        // Recent 5 bookings
        List<BookingResponseDto> recentBookings = bookingRepository
            .findAll(PageRequest.of(0, 5, Sort.by("createdAt").descending()))
            .stream().map(BookingResponseDto::from).toList();

        return DashboardStatsDto.builder()
            .totalMembers(totalMembers)
            .activeMembers(activeMembers)
            .totalBookings(totalBookings)
            .todayBookings(todayBookings)
            .monthlyRevenue(monthlyRevenue)
            .pendingPayments(pendingPayments)
            .totalRevenue(totalRevenue)
            .revenueMonthLabels(revenueLabels)
            .revenueMonthValues(revenueValues)
            .turfLabels(turfLabels)
            .turfBookingCounts(turfCounts)
            .attendanceDateLabels(attendanceDateLabels)
            .attendanceRates(attendanceRates)
            .recentBookings(recentBookings)
            .build();
    }
}
