package com.example.rentcarkg.dto;

import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.Booking;

import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long carId,
        String carBrand,
        String carModel,
        String renterEmail,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatus status
) {
    public BookingResponse(Booking booking) {
        this(
                booking.getId(),
                booking.getCar().getId(),
                booking.getCar().getBrand(),
                booking.getCar().getModel(),
                booking.getUser().getEmail(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus()
        );
    }
}
