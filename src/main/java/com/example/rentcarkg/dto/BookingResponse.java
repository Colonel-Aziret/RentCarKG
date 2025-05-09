package com.example.rentcarkg.dto;

import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.Booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long carId,
        String carBrand,
        String carModel,
        String carImageUrl,
        String pickUpLocation,
        String dropOffLocation,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        BookingStatus status,
        LocalDateTime createdAt
) {
    public BookingResponse(Booking booking) {
        this(
                booking.getId(),
                booking.getCar().getId(),
                booking.getCar().getBrand(),
                booking.getCar().getModel(),
                booking.getCar().getImageUrl(),
                booking.getPickUpLocation().getCity(),
                booking.getDropOffLocation().getCity(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}

