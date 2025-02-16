package com.example.rentcarkg.dto;

import com.example.rentcarkg.model.Car;

import java.math.BigDecimal;

public record CarResponse(Long id, String brand, String model, BigDecimal pricePerDay) {
    public CarResponse(Car car) {
        this(car.getId(), car.getBrand(), car.getModel(), car.getPricePerDay());
    }
}

