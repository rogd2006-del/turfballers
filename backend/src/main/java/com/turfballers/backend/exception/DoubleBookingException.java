package com.turfballers.backend.exception;

public class DoubleBookingException extends RuntimeException {
    public DoubleBookingException(String turfName, String date, String time) {
        super(String.format(
            "Turf '%s' is already booked on %s during %s. Please choose a different time slot.",
            turfName, date, time));
    }
}
