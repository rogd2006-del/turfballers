package com.turfballers.backend.config;

import com.turfballers.backend.model.*;
import com.turfballers.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Seeds the database with a default admin user and demo data on first startup.
 * Default credentials: admin@turfballers.com / Admin@123
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdminUser();
        if (memberRepository.count() == 0) {
            seedDemoData();
        }
    }

    private void seedAdminUser() {
        String adminEmail = "admin@turfballers.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("Turf Admin")
                .phone("+91-9876543210")
                .role("ADMIN")
                .build();
            userRepository.save(admin);
            log.info("✅ Default admin seeded: {} / Admin@123", adminEmail);
        }
    }

    private void seedDemoData() {
        log.info("🌱 Seeding demo members, bookings, payments, and attendance...");

        // Demo members
        Member m1 = memberRepository.save(Member.builder()
            .fullName("Arjun Sharma").email("arjun@example.com").phone("+91-9876500001")
            .membershipPlan(Member.MembershipPlan.VIP).status(Member.MemberStatus.ACTIVE)
            .joinDate(LocalDate.now().minusMonths(5)).build());

        Member m2 = memberRepository.save(Member.builder()
            .fullName("Priya Patel").email("priya@example.com").phone("+91-9876500002")
            .membershipPlan(Member.MembershipPlan.STANDARD).status(Member.MemberStatus.ACTIVE)
            .joinDate(LocalDate.now().minusMonths(3)).build());

        Member m3 = memberRepository.save(Member.builder()
            .fullName("Rahul Singh").email("rahul@example.com").phone("+91-9876500003")
            .membershipPlan(Member.MembershipPlan.BASIC).status(Member.MemberStatus.ACTIVE)
            .joinDate(LocalDate.now().minusMonths(2)).build());

        Member m4 = memberRepository.save(Member.builder()
            .fullName("Sneha Reddy").email("sneha@example.com").phone("+91-9876500004")
            .membershipPlan(Member.MembershipPlan.STANDARD).status(Member.MemberStatus.INACTIVE)
            .joinDate(LocalDate.now().minusMonths(6)).build());

        Member m5 = memberRepository.save(Member.builder()
            .fullName("Vikram Nair").email("vikram@example.com").phone("+91-9876500005")
            .membershipPlan(Member.MembershipPlan.VIP).status(Member.MemberStatus.ACTIVE)
            .joinDate(LocalDate.now().minusMonths(1)).build());

        // Demo bookings
        Booking b1 = bookingRepository.save(Booking.builder()
            .member(m1).turfName("Football 7v7").bookingDate(LocalDate.now())
            .startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0))
            .amount(new BigDecimal("1200")).status(Booking.BookingStatus.CONFIRMED).build());

        Booking b2 = bookingRepository.save(Booking.builder()
            .member(m2).turfName("Cricket Box").bookingDate(LocalDate.now())
            .startTime(LocalTime.of(10, 0)).endTime(LocalTime.of(12, 0))
            .amount(new BigDecimal("800")).status(Booking.BookingStatus.CONFIRMED).build());

        Booking b3 = bookingRepository.save(Booking.builder()
            .member(m3).turfName("Football 5v5").bookingDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(16, 0)).endTime(LocalTime.of(18, 0))
            .amount(new BigDecimal("600")).status(Booking.BookingStatus.PENDING).build());

        Booking b4 = bookingRepository.save(Booking.builder()
            .member(m5).turfName("Pickleball Court").bookingDate(LocalDate.now().minusDays(1))
            .startTime(LocalTime.of(7, 0)).endTime(LocalTime.of(8, 0))
            .amount(new BigDecimal("400")).status(Booking.BookingStatus.CONFIRMED).build());

        Booking b5 = bookingRepository.save(Booking.builder()
            .member(m1).turfName("Football 7v7").bookingDate(LocalDate.now().minusDays(3))
            .startTime(LocalTime.of(18, 0)).endTime(LocalTime.of(20, 0))
            .amount(new BigDecimal("1200")).status(Booking.BookingStatus.CONFIRMED).build());

        // Demo payments
        paymentRepository.save(Payment.builder().member(m1).booking(b1)
            .amount(new BigDecimal("1200")).paymentMethod(Payment.PaymentMethod.UPI)
            .status(Payment.PaymentStatus.PAID).transactionRef("UPI-2024-001").build());

        paymentRepository.save(Payment.builder().member(m2).booking(b2)
            .amount(new BigDecimal("800")).paymentMethod(Payment.PaymentMethod.CASH)
            .status(Payment.PaymentStatus.PAID).build());

        paymentRepository.save(Payment.builder().member(m3).booking(b3)
            .amount(new BigDecimal("600")).paymentMethod(Payment.PaymentMethod.CARD)
            .status(Payment.PaymentStatus.PENDING).build());

        paymentRepository.save(Payment.builder().member(m5).booking(b4)
            .amount(new BigDecimal("400")).paymentMethod(Payment.PaymentMethod.UPI)
            .status(Payment.PaymentStatus.PAID).transactionRef("UPI-2024-002").build());

        // Demo attendance
        attendanceRepository.save(Attendance.builder().member(m1).booking(b1)
            .date(LocalDate.now()).status(Attendance.AttendanceStatus.PRESENT).build());
        attendanceRepository.save(Attendance.builder().member(m2).booking(b2)
            .date(LocalDate.now()).status(Attendance.AttendanceStatus.PRESENT).build());
        attendanceRepository.save(Attendance.builder().member(m5).booking(b4)
            .date(LocalDate.now().minusDays(1)).status(Attendance.AttendanceStatus.PRESENT).build());
        attendanceRepository.save(Attendance.builder().member(m4)
            .date(LocalDate.now().minusDays(2)).status(Attendance.AttendanceStatus.ABSENT).build());

        log.info("✅ Demo data seeded: 5 members, 5 bookings, 4 payments, 4 attendance records");
    }
}
