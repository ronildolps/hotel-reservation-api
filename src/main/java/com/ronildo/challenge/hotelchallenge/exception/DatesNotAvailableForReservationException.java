package com.ronildo.challenge.hotelchallenge.exception;

public class DatesNotAvailableForReservationException extends RuntimeException {
    public DatesNotAvailableForReservationException() {
        super("Dates are not available for reservation");
    }
}
