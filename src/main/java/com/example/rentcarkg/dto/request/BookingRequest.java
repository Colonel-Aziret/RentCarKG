package com.example.rentcarkg.dto.request;

import com.example.rentcarkg.dto.BookingCustomerInfoDto;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull Long carId,
        @NotNull Long pickUpLocationId,
        @NotNull Long dropOffLocationId,
        @FutureOrPresent LocalDate startDate,
        @Future LocalDate endDate,
        @NotNull BookingCustomerInfoDto userInfo
) {
    public void validate() {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }
}


