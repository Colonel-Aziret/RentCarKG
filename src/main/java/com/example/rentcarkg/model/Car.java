package com.example.rentcarkg.model;

import com.example.rentcarkg.dto.request.CarRequest;
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
    private int capacity;

    @Column(nullable = false)
    private String fuelType;

    @Column(nullable = false)
    private String transmission;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // 💡 Конструктор для создания Car из CarRequest
    public Car(CarRequest request) {
        this.brand = request.brand();
        this.model = request.model();
        this.year = request.year();
        this.color = request.color();
        this.description = request.description();
        this.pricePerDay = request.pricePerDay();
    }

    public void updateFromRequest(CarRequest request) {
        this.brand = request.brand();
        this.model = request.model();
        this.pricePerDay = request.pricePerDay();
    }

    public String getTitle() {
        return brand + " " + model;
    }

    public boolean isOwnedBy(User user) {
        return this.owner.equals(user);
    }
}
