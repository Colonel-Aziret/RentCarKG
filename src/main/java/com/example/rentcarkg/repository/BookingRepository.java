package com.example.rentcarkg.repository;

import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.Booking;
import com.example.rentcarkg.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByCarIdAndStatus(Long carId, BookingStatus status);

    List<Booking> findByCarIdAndStatusIn(Long carId, List<BookingStatus> statuses);

    @Query("""
                SELECT b FROM Booking b
                JOIN FETCH b.car c
                JOIN FETCH c.owner o
                WHERE o.email = :ownerEmail AND b.status = 'EMAIL_CONFIRMED'
            """)
    List<Booking> findAllForOwnerEmailConfirmed(@Param("ownerEmail") String ownerEmail);


    List<Booking> findByUserAndStatus(User user, BookingStatus bookingStatus);
}

