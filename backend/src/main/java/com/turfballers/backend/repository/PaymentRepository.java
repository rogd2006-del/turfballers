package com.turfballers.backend.repository;

import com.turfballers.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Payment repository with revenue aggregation queries.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByMemberId(Long memberId);

    /** Total revenue across all paid transactions */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID'")
    BigDecimal sumPaidRevenue();

    /** Total pending amount */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PENDING'")
    BigDecimal sumPendingAmount();

    /** Revenue by payment method for analytics */
    @Query("SELECT p.paymentMethod, COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<Object[]> revenueByPaymentMethod();
}
