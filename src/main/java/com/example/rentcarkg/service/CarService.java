package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.CarRequest;
import com.example.rentcarkg.dto.CarResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CarService {
    CarResponse addCar(CarRequest carRequest, String ownerEmail);

    CarResponse update(Long id, CarRequest carRequest, String ownerEmail);

    void deleteCar(Long id, String ownerEmail);

    List<CarResponse> getAllCars();

    CarResponse getCarById(Long id);

    List<CarResponse> getCarsByBrand(String brand);

    List<CarResponse> getCarsByModel(String model);

    List<CarResponse> getCarsByMaxPrice(BigDecimal maxPrice);

    List<CarResponse> getFilteredCars(BigDecimal minPrice, BigDecimal maxPrice, boolean available);
    List<CarResponse> getAvailableCars(LocalDate start, LocalDate end);
}
