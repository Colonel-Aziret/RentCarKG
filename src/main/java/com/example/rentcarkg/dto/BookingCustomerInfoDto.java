package com.example.rentcarkg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BookingCustomerInfoDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String phone,
        @NotBlank @Email String email,
        Integer age,
        String address,
        String city,
        String zipCode
) {
}


