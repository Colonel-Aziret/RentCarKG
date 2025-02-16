package com.example.rentcarkg.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CarRequest(
        @NotBlank(message = "Brand is required") String brand,
        @NotBlank(message = "Model is required") String model,
        @NotNull(message = "Price per day is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal pricePerDay
) {
}

