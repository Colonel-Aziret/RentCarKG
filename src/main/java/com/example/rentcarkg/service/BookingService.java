package com.example.rentcarkg.service;

import com.example.rentcarkg.dto.BookingRequest;
import com.example.rentcarkg.dto.BookingResponse;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request, String userEmail);

    List<BookingResponse> getUserBookings(String userEmail);

    BookingResponse confirmByEmail(Long id);

    BookingResponse confirmBooking(Long bookingId, String ownerEmail);

    BookingResponse cancelBooking(Long bookingId, String userEmail);

    List<BookingResponse> getRequestsForOwner(String ownerEmail);

    BookingResponse rejectBooking(Long bookingId, String ownerEmail);

    boolean isCarAvailable(Long carId, LocalDate start, LocalDate end);
}
