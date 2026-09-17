package com.turfballers.backend.repository;

import com.turfballers.backend.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Booking repository - includes double-booking detection and analytics queries.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByBookingDate(LocalDate date);

    long countByStatus(Booking.BookingStatus status);

    List<Booking> findByBookingDateBetween(LocalDate start, LocalDate end);

    Page<Booking> findByMemberId(Long memberId, Pageable pageable);

    /**
     * Detect overlapping bookings for the same turf on the same date.
     * Used to prevent double-booking.
     */
    @Query("""
        SELECT b FROM Booking b
        WHERE b.turfName = :turfName
          AND b.bookingDate = :date
          AND b.status != 'CANCELLED'
          AND (:excludeId IS NULL OR b.id != :excludeId)
          AND b.startTime < :endTime
          AND b.endTime > :startTime
        """)
    List<Booking> findOverlappingBookings(
        @Param("turfName")  String turfName,
        @Param("date")      LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime")   LocalTime endTime,
        @Param("excludeId") Long excludeId
    );

    /** Monthly revenue for dashboard chart - last 6 months */
    @Query("""
        SELECT FUNCTION('MONTH', b.bookingDate) as month,
               FUNCTION('YEAR',  b.bookingDate) as year,
               SUM(b.amount)                    as revenue
        FROM Booking b
        WHERE b.status = 'CONFIRMED'
          AND b.bookingDate >= :fromDate
        GROUP BY FUNCTION('YEAR', b.bookingDate), FUNCTION('MONTH', b.bookingDate)
        ORDER BY year, month
        """)
    List<Object[]> findMonthlyRevenue(@Param("fromDate") LocalDate fromDate);

    /** Total confirmed revenue */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Booking b WHERE b.status = 'CONFIRMED'")
    BigDecimal sumConfirmedRevenue();

    /** Revenue for current month */
    @Query("""
        SELECT COALESCE(SUM(b.amount), 0) FROM Booking b
        WHERE b.status = 'CONFIRMED'
          AND FUNCTION('MONTH', b.bookingDate) = FUNCTION('MONTH', CURRENT_DATE)
          AND FUNCTION('YEAR',  b.bookingDate) = FUNCTION('YEAR',  CURRENT_DATE)
        """)
    BigDecimal sumCurrentMonthRevenue();

    /** Bookings grouped by turf name for doughnut chart */
    @Query("SELECT b.turfName, COUNT(b) FROM Booking b GROUP BY b.turfName")
    List<Object[]> countByTurf();
}
