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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final EmailService emailService;
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
        BigDecimal totalCost = calculateRentalCost(carId, start, end);
        booking.setTotalPrice(totalCost);
        booking.setStatus(BookingStatus.PENDING);

        return new BookingResponse(bookingRepository.save(booking));
    }

    public BookingResponse confirmBooking(Long bookingId, String ownerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking is already confirmed");
        }

        if (!booking.getCar().getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // ✅ Уведомление клиенту
        emailService.sendEmail(
                booking.getUser().getEmail(),
                "Booking Confirmed",
                "Your booking for car '" + booking.getCar().getTitle() + "' from " +
                        booking.getStartDate() + " to " + booking.getEndDate() + " has been confirmed."
        );

        return new BookingResponse(booking);
    }

    public BigDecimal cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You can only cancel your own booking");
        }

        long hoursUntilStart = ChronoUnit.HOURS.between(LocalDateTime.now(), booking.getStartDate().atStartOfDay());
        if (hoursUntilStart < 2) {
            throw new IllegalStateException("You cannot cancel the booking less than 2 hours before start time.");
        }

        BigDecimal penalty = BigDecimal.ZERO;

        if (hoursUntilStart < 24) {
            penalty = booking.getTotalPrice().multiply(BigDecimal.valueOf(0.5)); // 50% penalty
        }

        booking.setPenalty(penalty);
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // ✅ Уведомление клиенту
        emailService.sendEmail(
                booking.getUser().getEmail(),
                "Booking Cancelled",
                "Your booking for car '" + booking.getCar().getTitle() + "' has been cancelled. " +
                        (penalty.compareTo(BigDecimal.ZERO) > 0 ?
                                "A penalty of " + penalty + " has been applied." :
                                "No penalty was applied.")
        );

        return penalty;
    }

    public List<BookingResponse> getBookingsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream().map(BookingResponse::new).toList();
    }

    public BookingResponse rejectBooking(Long bookingId, String ownerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (!booking.getCar().getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new IllegalStateException("Booking is already cancelled or rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        // Email уведомление
        emailService.sendEmail(
                booking.getUser().getEmail(),
                "Booking Rejected",
                "Your booking for car '" + booking.getCar().getBrand() + " " + booking.getCar().getModel() +
                        "' has been rejected by the owner."
        );

        return new BookingResponse(booking);
    }

    public List<BookingResponse> getCancelledBookingsWithPenalty(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Booking> cancelledBookings = bookingRepository.findByUserAndStatus(user, BookingStatus.CANCELLED);
        return cancelledBookings.stream()
                .filter(booking -> booking.getPenalty() != null && booking.getPenalty().compareTo(BigDecimal.ZERO) > 0)
                .map(BookingResponse::new)
                .toList();
    }

    public boolean isCarAvailable(Long carId, LocalDate start, LocalDate end) {
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatus(carId, BookingStatus.CONFIRMED);
        return activeBookings.stream().noneMatch(b ->
                (start.isBefore(b.getEndDate()) && end.isAfter(b.getStartDate())));
    }

    public BigDecimal calculateRentalCost(Long carId, LocalDate start, LocalDate end) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));
        long days = ChronoUnit.DAYS.between(start, end);
        return car.getPricePerDay().multiply(BigDecimal.valueOf(days));
    }
}
