package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.CarRequest;
import com.example.rentcarkg.dto.CarResponse;
import com.example.rentcarkg.model.Car;

import java.math.BigDecimal;
import java.util.List;

public interface CarService {
    CarResponse addCar(CarRequest carRequest);

    CarResponse update(Long id, CarRequest carRequest);

    void deleteCar(Long id);

    List<CarResponse> getAllCars();

    CarResponse getCarById(Long id);

    List<CarResponse> getCarsByBrand(String brand);

    List<CarResponse> getCarsByModel(String model);

    List<CarResponse> getCarsByMaxPrice(BigDecimal maxPrice);
}
