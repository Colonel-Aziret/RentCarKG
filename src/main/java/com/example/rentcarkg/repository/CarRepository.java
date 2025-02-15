package com.example.rentcarkg.repository;

import com.example.rentcarkg.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    // Найти авто по ID
    Optional<Car> findById(Long id);

    // Найти все машины определенного бренда
    List<Car> findByBrand(String brand);

    // Найти все машины дешевле указанной цены
    List<Car> findByPricePerDayLessThanEqual(BigDecimal maxPrice);

    // Найти машины по модели
    List<Car> findByModelContainingIgnoreCase(String model);

}

