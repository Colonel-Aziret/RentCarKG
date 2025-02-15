package com.example.rentcarkg.service;

import com.example.rentcarkg.model.Car;
import com.example.rentcarkg.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;

    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    public Car update(Long id, Car updatedCar) {
        Car existingCar = getCarById(id);

        existingCar.setBrand(updatedCar.getBrand());
        existingCar.setModel(updatedCar.getModel());
        existingCar.setPricePerDay(updatedCar.getPricePerDay());

        return carRepository.save(existingCar);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
    }
}
