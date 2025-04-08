package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.BookingResponse;
import com.example.rentcarkg.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Бронирование", description = "API для бронирования автомобилей")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Забронировать автомобиль", description = "Доступно только для пользователей с ролью CLIENT")
    @PostMapping("/book")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BookingResponse> bookCar(
            @RequestParam @Parameter(description = "ID автомобиля") Long carId,
            @RequestParam @Parameter(description = "Дата начала бронирования (формат: YYYY-MM-DD)") LocalDate start,
            @RequestParam @Parameter(description = "Дата окончания бронирования (формат: YYYY-MM-DD)") LocalDate end,
            @RequestParam @Parameter(description = "Email пользователя") String userEmail) {

        return ResponseEntity.ok(bookingService.bookCar(carId, start, end, userEmail));
    }

    @Operation(
            summary = "Подтвердить бронирование",
            description = "Доступно только для владельцев автомобилей (OWNER). "
                    + "Позволяет владельцу подтвердить бронирование автомобиля."
    )
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable @Parameter(description = "ID бронирования") Long id,
            @RequestParam @Parameter(description = "Email владельца автомобиля") String ownerEmail) {

        return ResponseEntity.ok(bookingService.confirmBooking(id, ownerEmail));
    }

    @Operation(
            summary = "Отменить бронирование",
            description = "Доступно только для клиентов (CLIENT). "
                    + "Позволяет отменить бронирование автомобиля. "
                    + "Если до начала бронирования менее 24 часов, взимается штраф (50% стоимости)."
    )
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> cancelBooking(
            @PathVariable @Parameter(description = "ID бронирования") Long id,
            @RequestParam @Parameter(description = "Email клиента") String userEmail) {

        BigDecimal penalty = bookingService.cancelBooking(id, userEmail);
        return ResponseEntity.ok("Booking cancelled. Penalty: " + penalty + " KGS");
    }

    @PostMapping("/reject/{bookingId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {
        String ownerEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.rejectBooking(bookingId, ownerEmail));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.getBookingsByUser(userEmail));
    }

}
