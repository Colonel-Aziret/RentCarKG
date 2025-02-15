package com.example.rentcarkg.service;

import com.example.rentcarkg.model.Car;

import java.util.List;

public interface CarService {
    Car addCar(Car car);

    Car update(Long id, Car car);

    void deleteCar(Long id);

    List<Car> getAllCars();

    Car getCarById(Long id);
}
