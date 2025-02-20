package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.CarRequest;
import com.example.rentcarkg.dto.CarResponse;
import com.example.rentcarkg.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping("/add-car")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CarResponse> addCar(@Valid @RequestBody CarRequest carRequest,
                                              @RequestParam String ownerEmail) {
        return ResponseEntity.ok(carService.addCar(carRequest, ownerEmail));
    }

    @PutMapping("/update-car/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CarResponse> updateCar(@PathVariable Long id,
                                                 @Valid @RequestBody CarRequest carRequest,
                                                 @RequestParam String ownerEmail) {
        return ResponseEntity.ok(carService.update(id, carRequest, ownerEmail));
    }

    @DeleteMapping("/delete-car/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id,
                                          @RequestParam String ownerEmail) {
        carService.deleteCar(id, ownerEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<CarResponse>> getCarsByBrand(@PathVariable String brand) {
        return ResponseEntity.ok(carService.getCarsByBrand(brand));
    }

    @GetMapping("/model/{model}")
    public ResponseEntity<List<CarResponse>> getCarsByModel(@PathVariable String model) {
        return ResponseEntity.ok(carService.getCarsByModel(model));
    }

    @GetMapping("/price/{maxPrice}")
    public ResponseEntity<List<CarResponse>> getCarsByMaxPrice(@PathVariable BigDecimal maxPrice) {
        return ResponseEntity.ok(carService.getCarsByMaxPrice(maxPrice));
    }
}

