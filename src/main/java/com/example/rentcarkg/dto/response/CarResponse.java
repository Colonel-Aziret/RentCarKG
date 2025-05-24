package com.example.rentcarkg.dto.response;

import com.example.rentcarkg.model.Car;

import java.math.BigDecimal;

public record CarResponse(Long id, String brand, String model, BigDecimal pricePerDay, int year, String color,
                          int capacity,
                          String fuelType,
                          String transmission,
                          String description,
                          String imageUrl,
                          String title) {
    public CarResponse(Car car) {
        this(car.getId(), car.getBrand(), car.getModel(), car.getPricePerDay(), car.getYear(), car.getColor(), car.getCapacity(), car.getFuelType(), car.getTransmission(), car.getDescription(), car.getImageUrl(), car.getTitle());
    }
}

