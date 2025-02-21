package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.BookingResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BookingService {
    BookingResponse bookCar(Long carId, LocalDate start, LocalDate end, String userEmail);

    boolean isCarAvailable(Long carId, LocalDate start, LocalDate end);

    BookingResponse confirmBooking(Long bookingId, String ownerEmail);

    BigDecimal cancelBooking(Long bookingId, String userEmail);
}
