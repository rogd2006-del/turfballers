package com.turfballers.backend.controller;

import com.turfballers.backend.dto.PaymentRequestDto;
import com.turfballers.backend.dto.PaymentResponseDto;
import com.turfballers.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Payment REST controller.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PaymentResponseDto>> getPendingPayments() {
        return ResponseEntity.ok(paymentService.getPendingPayments());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(paymentService.getPaymentsByMember(memberId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(@Valid @RequestBody PaymentRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(dto));
    }
}
