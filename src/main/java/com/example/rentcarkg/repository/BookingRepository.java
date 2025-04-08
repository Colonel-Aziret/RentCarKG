package com.example.rentcarkg.repository;

import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.Booking;
import com.example.rentcarkg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByCarIdAndStatus(Long carId, BookingStatus status);

    List<Booking> findByUserAndStatus(User user, BookingStatus bookingStatus);
}

