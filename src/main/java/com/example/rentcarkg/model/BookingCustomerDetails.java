package com.example.rentcarkg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_customer_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCustomerDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Integer age;
    private String address;
    private String city;
    private String zipCode;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}

