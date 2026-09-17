package com.turfballers.backend.repository;

import com.turfballers.backend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance repository with member statistics queries.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByMemberId(Long memberId);

    List<Attendance> findByMemberIdOrderByDateDesc(Long memberId);

    boolean existsByMemberIdAndDate(Long memberId, LocalDate date);

    long countByMemberIdAndStatus(Long memberId, Attendance.AttendanceStatus status);

    long countByMemberId(Long memberId);

    /** Attendance counts per day for last N days (for weekly chart) */
    @Query("""
        SELECT a.date, COUNT(a), SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END)
        FROM Attendance a
        WHERE a.date >= :fromDate
        GROUP BY a.date
        ORDER BY a.date
        """)
    List<Object[]> findAttendanceStats(@Param("fromDate") LocalDate fromDate);
}
