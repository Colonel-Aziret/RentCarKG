package com.example.rentcarkg.service.impl;

import com.example.rentcarkg.dto.BookingCustomerInfoDto;
import com.example.rentcarkg.dto.BookingRequest;
import com.example.rentcarkg.dto.BookingResponse;
import com.example.rentcarkg.enums.BookingStatus;
import com.example.rentcarkg.model.*;
import com.example.rentcarkg.repository.BookingRepository;
import com.example.rentcarkg.repository.CarRepository;
import com.example.rentcarkg.repository.LocationRepository;
import com.example.rentcarkg.repository.UserRepository;
import com.example.rentcarkg.service.BookingService;
import com.example.rentcarkg.service.EmailService;
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
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        request.validate();

        Car car = carRepository.findById(request.carId())
                .orElseThrow(() -> new EntityNotFoundException("Car not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Location pickUpLocation = locationRepository.findById(request.pickUpLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Pick-up location not found"));

        Location dropOffLocation = locationRepository.findById(request.dropOffLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Drop-off location not found"));

        if (!isCarAvailable(request.carId(), request.startDate(), request.endDate())) {
            throw new IllegalStateException("Car is already booked for these dates");
        }

        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate());
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(user);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setPickUpLocation(pickUpLocation);
        booking.setDropOffLocation(dropOffLocation);
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);

        // Создание вложенных деталей клиента
        BookingCustomerInfoDto info = request.userInfo();
        BookingCustomerDetails details = new BookingCustomerDetails();
        details.setFirstName(info.firstName());
        details.setLastName(info.lastName());
        details.setPhone(info.phone());
        details.setEmail(info.email());
        details.setAge(info.age());
        details.setAddress(info.address());
        details.setCity(info.city());
        details.setZipCode(info.zipCode());
        details.setBooking(booking);

        booking.setCustomerDetails(details);

        Booking savedBooking = bookingRepository.save(booking);

        emailService.sendBookingConfirmationEmail(savedBooking);

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

        if (booking.getCustomerDetails() == null || booking.getCustomerDetails().getEmail() == null) {
            throw new IllegalStateException("Customer email is missing for this booking");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking confirmedBooking = bookingRepository.save(booking);

        emailService.sendEmail(
                booking.getCustomerDetails().getEmail(),
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

        if (booking.getCustomerDetails() == null || booking.getCustomerDetails().getEmail() == null) {
            throw new IllegalStateException("Customer email is missing for this booking");
        }

        booking.setStatus(BookingStatus.REJECTED);
        Booking rejectedBooking = bookingRepository.save(booking);

        emailService.sendEmail(
                booking.getCustomerDetails().getEmail(),
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

        if (booking.getCustomerDetails() == null || booking.getCustomerDetails().getEmail() == null) {
            throw new IllegalStateException("Customer email is missing for this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPenalty(penalty);
        Booking cancelledBooking = bookingRepository.save(booking);

        String penaltyMessage = penalty.compareTo(BigDecimal.ZERO) > 0 ?
                "A penalty of " + penalty + " has been applied." :
                "No penalty was applied.";

        emailService.sendEmail(
                booking.getCustomerDetails().getEmail(),
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
