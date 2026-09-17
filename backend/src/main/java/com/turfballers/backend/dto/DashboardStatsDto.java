package com.turfballers.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** Aggregated stats for the admin dashboard */
@Data
@Builder
public class DashboardStatsDto {

    // KPI counters
    private long totalMembers;
    private long activeMembers;
    private long totalBookings;
    private long todayBookings;
    private BigDecimal monthlyRevenue;
    private BigDecimal pendingPayments;
    private BigDecimal totalRevenue;

    // Charts data
    private List<String> revenueMonthLabels;
    private List<BigDecimal> revenueMonthValues;

    private List<String> turfLabels;
    private List<Long> turfBookingCounts;

    private List<String> attendanceDateLabels;
    private List<Double> attendanceRates;

    // Recent activity
    private List<BookingResponseDto> recentBookings;
}
