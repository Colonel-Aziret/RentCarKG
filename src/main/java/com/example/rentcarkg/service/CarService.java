package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.request.CarRequest;
import com.example.rentcarkg.dto.response.CarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CarService {
    CarResponse addCar(CarRequest carRequest, MultipartFile image, String ownerEmail);

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
