package com.example.rentcarkg.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull Long carId,
        @NotBlank String pickUpLocation,
        @NotBlank String dropOffLocation,
        @FutureOrPresent LocalDate startDate,
        @Future LocalDate endDate,
        @NotBlank String customerName,
        @NotBlank String customerPhone,
        @NotBlank @Email String customerEmail
) {
    public void validate() {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}
