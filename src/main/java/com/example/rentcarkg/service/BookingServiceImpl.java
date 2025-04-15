package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.BookingRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final EmailService emailService;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        // Валидация запроса
        if (request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Проверка доступности автомобиля
        if (!isCarAvailable(request.carId(), request.startDate(), request.endDate())) {
            throw new IllegalStateException("Car is already booked for these dates");
        }

        // Расчет стоимости
        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        // Создание бронирования
        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(user);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        booking.setPickUpLocation(request.pickUpLocation());
        booking.setDropOffLocation(request.dropOffLocation());
        booking.setCustomerName(request.customerName());
        booking.setCustomerPhone(request.customerPhone());
        booking.setCustomerEmail(request.customerEmail());

        Booking savedBooking = bookingRepository.save(booking);

        // Отправка уведомления
        emailService.sendEmail(
                user.getEmail(),
                "Booking Created",
                "Your booking for " + car.getBrand() + " " + car.getModel() + " has been created."
        );

        return new BookingResponse(savedBooking);
    }

    @Override
    public BookingResponse confirmBooking(Long bookingId, String ownerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Проверка прав владельца
        if (!booking.getCar().getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        // Проверка статуса
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }

        // Проверка доступности авто
        if (!isCarAvailable(booking.getCar().getId(), booking.getStartDate(), booking.getEndDate())) {
            throw new IllegalStateException("Car is no longer available for these dates");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking confirmedBooking = bookingRepository.save(booking);

        // Уведомление клиенту
        emailService.sendEmail(
                booking.getCustomerEmail(),
                "Booking Confirmed",
                "Your booking for " + booking.getCar().getBrand() + " " + booking.getCar().getModel() +
                        " from " + booking.getStartDate() + " to " + booking.getEndDate() + " has been confirmed."
        );

        return new BookingResponse(confirmedBooking);
    }

    @Override
    public BookingResponse rejectBooking(Long bookingId, String ownerEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Проверка прав владельца
        if (!booking.getCar().getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not the owner of this car");
        }

        // Проверка статуса
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected");
        }

        booking.setStatus(BookingStatus.REJECTED);
        Booking rejectedBooking = bookingRepository.save(booking);

        // Уведомление клиенту
        emailService.sendEmail(
                booking.getCustomerEmail(),
                "Booking Rejected",
                "Your booking for " + booking.getCar().getBrand() + " " + booking.getCar().getModel() +
                        " has been rejected by the owner."
        );

        return new BookingResponse(rejectedBooking);
    }

    @Override
    public BookingResponse cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Проверка прав пользователя
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You can only cancel your own booking");
        }

        // Проверка статуса
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Расчет штрафа
        long hoursUntilStart = ChronoUnit.HOURS.between(LocalDateTime.now(),
                booking.getStartDate().atStartOfDay());

        BigDecimal penalty = BigDecimal.ZERO;
        if (hoursUntilStart < 24) {
            penalty = booking.getTotalPrice().multiply(new BigDecimal("0.5"));
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPenalty(penalty);
        Booking cancelledBooking = bookingRepository.save(booking);

        // Уведомление клиенту
        String penaltyMessage = penalty.compareTo(BigDecimal.ZERO) > 0 ?
                "A penalty of " + penalty + " has been applied." :
                "No penalty was applied.";

        emailService.sendEmail(
                booking.getCustomerEmail(),
                "Booking Cancelled",
                "Your booking for " + booking.getCar().getBrand() + " " + booking.getCar().getModel() +
                        " has been cancelled. " + penaltyMessage
        );

        return new BookingResponse(cancelledBooking);
    }

    @Override
    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return bookingRepository.findByUser(user).stream()
                .map(BookingResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCarAvailable(Long carId, LocalDate start, LocalDate end) {
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatusIn(
                carId,
                List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)
        );

        return activeBookings.stream().noneMatch(booking ->
                start.isBefore(booking.getEndDate()) && end.isAfter(booking.getStartDate())
        );
    }
}
