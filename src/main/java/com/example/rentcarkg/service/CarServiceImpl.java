package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.CarRequest;
import com.example.rentcarkg.dto.CarResponse;
import com.example.rentcarkg.model.Car;
import com.example.rentcarkg.repository.CarRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    @Override
    public CarResponse addCar(CarRequest carRequest) {
        Car car = new Car(carRequest);
        Car savedCar = carRepository.save(car);
        return new CarResponse(savedCar);
    }

    @Override
    public CarResponse update(Long id, CarRequest carRequest) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        existingCar.updateFromRequest(carRequest);
        Car updatedCar = carRepository.save(existingCar);
        return new CarResponse(updatedCar);
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public List<CarResponse> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(CarResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));
        return new CarResponse(car);
    }

    @Override
    public List<CarResponse> getCarsByBrand(String brand) {
        return carRepository.findByBrandIgnoreCase(brand)
                .stream()
                .map(CarResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarResponse> getCarsByModel(String model) {
        return carRepository.findByModelContainingIgnoreCase(model)
                .stream()
                .map(CarResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarResponse> getCarsByMaxPrice(BigDecimal maxPrice) {
        return carRepository.findByPricePerDayLessThanEqual(maxPrice)
                .stream()
                .map(CarResponse::new)
                .collect(Collectors.toList());
    }
}

