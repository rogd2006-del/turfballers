package com.turfballers.backend.service;

import com.turfballers.backend.dto.PaymentRequestDto;
import com.turfballers.backend.dto.PaymentResponseDto;
import com.turfballers.backend.exception.ResourceNotFoundException;
import com.turfballers.backend.model.Booking;
import com.turfballers.backend.model.Member;
import com.turfballers.backend.model.Payment;
import com.turfballers.backend.repository.BookingRepository;
import com.turfballers.backend.repository.MemberRepository;
import com.turfballers.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Payment service - record, list, and analyse financial transactions.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final BookingRepository bookingRepository;

    /** All payments */
    public List<PaymentResponseDto> getAllPayments() {
        return paymentRepository.findAll()
            .stream().map(PaymentResponseDto::from).toList();
    }

    /** Pending payments only */
    public List<PaymentResponseDto> getPendingPayments() {
        return paymentRepository.findByStatus(Payment.PaymentStatus.PENDING)
            .stream().map(PaymentResponseDto::from).toList();
    }

    /** Payments for a specific member */
    public List<PaymentResponseDto> getPaymentsByMember(Long memberId) {
        return paymentRepository.findByMemberId(memberId)
            .stream().map(PaymentResponseDto::from).toList();
    }

    /** Record a new payment */
    public PaymentResponseDto createPayment(PaymentRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        Booking booking = null;
        if (dto.getBookingId() != null) {
            booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", dto.getBookingId()));
        }

        Payment payment = Payment.builder()
            .member(member)
            .booking(booking)
            .amount(dto.getAmount())
            .paymentMethod(dto.getPaymentMethod())
            .paymentDate(dto.getPaymentDate())
            .status(dto.getStatus() != null ? dto.getStatus() : Payment.PaymentStatus.PAID)
            .transactionRef(dto.getTransactionRef())
            .build();
        return PaymentResponseDto.from(paymentRepository.save(payment));
    }
}
