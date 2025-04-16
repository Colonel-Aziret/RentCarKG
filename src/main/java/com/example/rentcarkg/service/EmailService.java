package com.example.rentcarkg.service;

import com.example.rentcarkg.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@rentcarkg.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendBookingConfirmationEmail(Booking booking) {
        if (booking.getCustomerDetails() == null || booking.getCustomerDetails().getEmail() == null) {
            throw new IllegalStateException("Email for customer is missing.");
        }

        String recipient = booking.getCustomerDetails().getEmail();
        String subject = "Confirm your car booking";
        String confirmUrl = "http://localhost:3000/confirm-booking?id=" + booking.getId();

        String message = """
                Hello, %s!
                
                You have booked a %s %s from %s to %s.
                
                Please confirm your booking by clicking the link below:
                %s
                
                If you did not make this booking, simply ignore this message.
                """.formatted(
                booking.getCustomerDetails().getFirstName(),
                booking.getCar().getBrand(),
                booking.getCar().getModel(),
                booking.getStartDate(),
                booking.getEndDate(),
                confirmUrl
        );

        sendEmail(recipient, subject, message);
    }
}
