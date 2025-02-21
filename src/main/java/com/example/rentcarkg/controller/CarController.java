package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.CarRequest;
import com.example.rentcarkg.dto.CarResponse;
import com.example.rentcarkg.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Автомобили", description = "API для работы с автомобилями")
@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @Operation(summary = "Добавить новый автомобиль", description = "Доступно только владельцам (OWNER)")
    @PostMapping("/add-car")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CarResponse> addCar(
            @Valid @RequestBody CarRequest carRequest,
            @RequestParam @Parameter(description = "Email владельца") String ownerEmail) {

        return ResponseEntity.ok(carService.addCar(carRequest, ownerEmail));
    }

    @Operation(summary = "Обновить данные автомобиля", description = "Доступно только владельцам (OWNER)")
    @PutMapping("/update-car/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable @Parameter(description = "ID автомобиля") Long id,
            @Valid @RequestBody CarRequest carRequest,
            @RequestParam @Parameter(description = "Email владельца") String ownerEmail) {

        return ResponseEntity.ok(carService.update(id, carRequest, ownerEmail));
    }

    @Operation(summary = "Удалить автомобиль", description = "Доступно только владельцам (OWNER)")
    @DeleteMapping("/delete-car/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteCar(
            @PathVariable @Parameter(description = "ID автомобиля") Long id,
            @RequestParam @Parameter(description = "Email владельца") String ownerEmail) {

        carService.deleteCar(id, ownerEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить список всех автомобилей")
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @Operation(summary = "Получить автомобиль по ID")
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(
            @PathVariable @Parameter(description = "ID автомобиля") Long id) {

        return ResponseEntity.ok(carService.getCarById(id));
    }

    @Operation(summary = "Получить автомобили по бренду")
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<CarResponse>> getCarsByBrand(
            @PathVariable @Parameter(description = "Бренд автомобиля") String brand) {

        return ResponseEntity.ok(carService.getCarsByBrand(brand));
    }

    @Operation(summary = "Получить автомобили по модели")
    @GetMapping("/model/{model}")
    public ResponseEntity<List<CarResponse>> getCarsByModel(
            @PathVariable @Parameter(description = "Модель автомобиля") String model) {

        return ResponseEntity.ok(carService.getCarsByModel(model));
    }

    @Operation(summary = "Получить автомобили по диапазону цен и наличию",
            description = "Возвращает список автомобилей, удовлетворяющих заданному диапазону цен и доступности.")
    @GetMapping("/filter")
    public ResponseEntity<List<CarResponse>> getCarsByFilters(
            @RequestParam(required = false) @Parameter(description = "Минимальная цена аренды в день") BigDecimal minPrice,
            @RequestParam(required = false) @Parameter(description = "Максимальная цена аренды в день") BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Только доступные автомобили") boolean available) {

        return ResponseEntity.ok(carService.getFilteredCars(minPrice, maxPrice, available));
    }

    @Operation(summary = "Получить автомобили по максимальной цене аренды в день")
    @GetMapping("/price/{maxPrice}")
    public ResponseEntity<List<CarResponse>> getCarsByMaxPrice(
            @PathVariable @Parameter(description = "Максимальная цена в день") BigDecimal maxPrice) {

        return ResponseEntity.ok(carService.getCarsByMaxPrice(maxPrice));
    }
}
