package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.BookingRequest;
import com.example.rentcarkg.dto.BookingResponse;
import com.example.rentcarkg.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Бронирование", description = "API для бронирования автомобилей")
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @Operation(summary = "Забронировать автомобиль",
            description = "Доступно только для пользователей с ролью CLIENT")
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        return ResponseEntity.ok(bookingService.createBooking(request, userEmail));
    }

    @Operation(summary = "Получить все бронирования пользователя")
    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyRole('CLIENT', 'OWNER')")
    public ResponseEntity<List<BookingResponse>> getUserBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getUserBookings(authentication.getName()));
    }

    @Operation(summary = "Подтверждение бронирования по email-ссылке")
    @PatchMapping("/{id}/email-confirm")
    @PermitAll
    public ResponseEntity<BookingResponse> confirmByEmail(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmByEmail(id));
    }

    @Operation(summary = "Заявки для владельца")
    @GetMapping("/owner-requests")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<BookingResponse>> getRequestsForOwner(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getRequestsForOwner(authentication.getName()));
    }

    @Operation(summary = "Подтвердить бронирование",
            description = "Доступно для владельцев автомобилей")
    @PatchMapping("/{id}/confirm")
//    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable Long id,
                                                          Authentication authentication) {
        return ResponseEntity.ok(bookingService.confirmBooking(id, authentication.getName()));
    }

    @Operation(summary = "Отклонить бронирование",
            description = "Доступно для владельцев автомобилей")
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<BookingResponse> rejectBooking(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(bookingService.rejectBooking(id, authentication.getName()));
    }

    @Operation(summary = "Отменить бронирование",
            description = "Доступно для клиентов")
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(bookingService.cancelBooking(id, authentication.getName()));
    }
}
