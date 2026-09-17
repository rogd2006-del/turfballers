package com.turfballers.backend.service;

import com.turfballers.backend.dto.BookingRequestDto;
import com.turfballers.backend.dto.BookingResponseDto;
import com.turfballers.backend.exception.DoubleBookingException;
import com.turfballers.backend.exception.ResourceNotFoundException;
import com.turfballers.backend.model.Booking;
import com.turfballers.backend.model.Member;
import com.turfballers.backend.repository.BookingRepository;
import com.turfballers.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Booking service - manages turf reservations with double-booking prevention.
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MemberRepository memberRepository;

    /** Paginated booking list, ordered by date descending */
    public Page<BookingResponseDto> getBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("bookingDate").descending());
        return bookingRepository.findAll(pageable).map(BookingResponseDto::from);
    }

    /** Bookings for a date range (used by calendar view) */
    public List<BookingResponseDto> getBookingsByDateRange(LocalDate start, LocalDate end) {
        return bookingRepository.findByBookingDateBetween(start, end)
            .stream().map(BookingResponseDto::from).toList();
    }

    /** Get single booking by ID */
    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        return BookingResponseDto.from(booking);
    }

    /** Create a new booking with overlap validation */
    public BookingResponseDto createBooking(BookingRequestDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        // Prevent double booking
        checkConflict(dto.getTurfName(), dto.getBookingDate(),
            dto.getStartTime().toString(), dto.getEndTime().toString(), null, dto);

        Booking booking = Booking.builder()
            .member(member)
            .turfName(dto.getTurfName())
            .bookingDate(dto.getBookingDate())
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .amount(dto.getAmount())
            .status(dto.getStatus() != null ? dto.getStatus() : Booking.BookingStatus.CONFIRMED)
            .notes(dto.getNotes())
            .build();
        return BookingResponseDto.from(bookingRepository.save(booking));
    }

    /** Update booking - re-validates conflict (excludes self) */
    public BookingResponseDto updateBooking(Long id, BookingRequestDto dto) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("Member", dto.getMemberId()));

        checkConflict(dto.getTurfName(), dto.getBookingDate(),
            dto.getStartTime().toString(), dto.getEndTime().toString(), id, dto);

        booking.setMember(member);
        booking.setTurfName(dto.getTurfName());
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setAmount(dto.getAmount());
        if (dto.getStatus() != null) booking.setStatus(dto.getStatus());
        booking.setNotes(dto.getNotes());
        return BookingResponseDto.from(bookingRepository.save(booking));
    }

    /** Cancel a booking */
    public BookingResponseDto cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return BookingResponseDto.from(bookingRepository.save(booking));
    }

    /** Delete a booking */
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Booking", id);
        }
        bookingRepository.deleteById(id);
    }

    // ---- Helper ----

    private void checkConflict(String turfName, LocalDate date, String start, String end, Long excludeId, BookingRequestDto dto) {
        List<Booking> conflicts = bookingRepository.findOverlappingBookings(
            turfName, date, dto.getStartTime(), dto.getEndTime(), excludeId);
        if (!conflicts.isEmpty()) {
            throw new DoubleBookingException(turfName, date.toString(), start + " - " + end);
        }
    }
}
