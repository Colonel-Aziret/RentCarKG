package com.example.rentcarkg.service.impl;

import com.example.rentcarkg.dto.request.CarRequest;
import com.example.rentcarkg.dto.response.CarResponse;
import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.Car;
import com.example.rentcarkg.model.User;
import com.example.rentcarkg.repository.BookingRepository;
import com.example.rentcarkg.repository.CarRepository;
import com.example.rentcarkg.repository.UserRepository;
import com.example.rentcarkg.service.BookingService;
import com.example.rentcarkg.service.CarService;
import com.example.rentcarkg.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {
    private final BookingService bookingService;
    private final FileStorageService fileStorageService;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CarResponse addCar(CarRequest carRequest, MultipartFile image, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

        String imageUrl = fileStorageService.saveFile(image);

        Car car = new Car(carRequest);
        car.setImageUrl(imageUrl);
        car.setOwner(owner);

        Car savedCar = carRepository.save(car);
        return new CarResponse(savedCar);
    }

    @Override
    public CarResponse update(Long id, CarRequest carRequest, String ownerEmail) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id: " + id));

        if (!existingCar.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        existingCar.updateFromRequest(carRequest);
        Car updatedCar = carRepository.save(existingCar);

        return new CarResponse(updatedCar);
    }

    @Override
    public void deleteCar(Long id, String ownerEmail) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        if (!car.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        carRepository.delete(car);
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

    public List<CarResponse> getFilteredCars(BigDecimal minPrice, BigDecimal maxPrice, boolean available) {
        List<Car> cars = carRepository.findAll().stream()
                .filter(car -> (minPrice == null || car.getPricePerDay().compareTo(minPrice) >= 0))
                .filter(car -> (maxPrice == null || car.getPricePerDay().compareTo(maxPrice) <= 0))
                .filter(car -> !available || isCarAvailable(car.getId()))
                .toList();

        return cars.stream().map(CarResponse::new).toList();
    }

    public List<CarResponse> getAvailableCars(LocalDate start, LocalDate end) {
        List<Car> allCars = carRepository.findAll();
        return allCars.stream()
                .filter(car -> bookingService.isCarAvailable(car.getId(), start, end))
                .map(CarResponse::new)
                .toList();
    }

    private boolean isCarAvailable(Long carId) {
        return bookingRepository.findByCarIdAndStatus(carId, BookingStatus.CONFIRMED).isEmpty();
    }
}

