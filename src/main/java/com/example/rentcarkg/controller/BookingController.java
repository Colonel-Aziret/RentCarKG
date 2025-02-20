package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.BookingResponse;
import com.example.rentcarkg.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/book")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<BookingResponse> bookCar(@RequestParam Long carId,
                                                   @RequestParam LocalDate start,
                                                   @RequestParam LocalDate end,
                                                   @RequestParam String userEmail) {
        return ResponseEntity.ok(bookingService.bookCar(carId, start, end, userEmail));
    }
}
