package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.BookingResponse;
import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.Booking;
import com.example.rentcarkg.model.Car;
import com.example.rentcarkg.model.User;
import com.example.rentcarkg.repository.BookingRepository;
import com.example.rentcarkg.repository.CarRepository;
import com.example.rentcarkg.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public BookingResponse bookCar(Long carId, LocalDate start, LocalDate end, String userEmail) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!isCarAvailable(carId, start, end)) {
            throw new IllegalStateException("Car is already booked for these dates");
        }

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(user);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setStatus(BookingStatus.PENDING);

        return new BookingResponse(bookingRepository.save(booking));
    }

    public boolean isCarAvailable(Long carId, LocalDate start, LocalDate end) {
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatus(carId, BookingStatus.CONFIRMED);
        return activeBookings.stream().noneMatch(b ->
                (start.isBefore(b.getEndDate()) && end.isAfter(b.getStartDate())));
    }
}
