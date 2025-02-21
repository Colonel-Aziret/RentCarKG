package com.example.rentcarkg.model;

import com.example.rentcarkg.dto.CarRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // 💡 Конструктор для создания Car из CarRequest
    public Car(CarRequest request) {
        this.brand = request.brand();
        this.model = request.model();
        this.pricePerDay = request.pricePerDay();
    }

    public void updateFromRequest(CarRequest request) {
        this.brand = request.brand();
        this.model = request.model();
        this.pricePerDay = request.pricePerDay();
    }
}
