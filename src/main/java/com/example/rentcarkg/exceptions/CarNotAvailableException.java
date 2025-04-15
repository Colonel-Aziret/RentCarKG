package com.example.rentcarkg.exceptions;

import java.time.LocalDate;

public class CarNotAvailableException extends BookingException {
    public CarNotAvailableException(LocalDate start, LocalDate end) {
        super(String.format("Car is not available from %s to %s", start, end));
    }
}
