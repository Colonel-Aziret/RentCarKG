package com.example.rentcarkg.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CarRequest(
        @NotBlank(message = "Brand is required") String brand,
        @NotBlank(message = "Model is required") String model,
        @NotNull(message = "Year is required") int year,
        @NotBlank(message = "Color is required") String color,
        @NotNull(message = "Capacity is required") @Min(value = 1, message = "Capacity must be at least 1") int capacity,
        @NotBlank(message = "Fuel type is required") String fuelType,
        @NotBlank(message = "Transmission type is required") String transmission,
        @NotBlank(message = "Description is required") String description,
        @NotNull(message = "Price per day is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal pricePerDay
) {
}


