package com.example.rentcarkg.service;

import com.example.rentcarkg.model.Booking;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content, boolean isHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, isHtml);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendBookingConfirmationEmail(Booking booking) {
        if (booking.getCustomerDetails() == null || booking.getCustomerDetails().getEmail() == null) {
            throw new IllegalStateException("Email for customer is missing.");
        }

        String recipient = booking.getCustomerDetails().getEmail();
        String subject = "Confirm your car booking";
        String confirmUrl = "http://localhost:3000/confirm-booking?id=" + booking.getId();

        String htmlMessage = """
                <html>
                <body>
                    <p>Hello, <b>%s</b>!</p>
                    <p>You have booked a <strong>%s %s</strong> from <strong>%s</strong> to <strong>%s</strong>.</p>
                    <p>
                        Please confirm your booking by clicking the link below:<br>
                        <a href="%s">%s</a>
                    </p>
                    <p>If you did not make this booking, simply ignore this message.</p>
                </body>
                </html>
                """.formatted(
                booking.getCustomerDetails().getFirstName(),
                booking.getCar().getBrand(),
                booking.getCar().getModel(),
                booking.getStartDate(),
                booking.getEndDate(),
                confirmUrl,
                confirmUrl
        );

        sendEmail(recipient, subject, htmlMessage, true);
    }

    public void sendContactMessage(String fullName, String fromEmail, String messageContent) {
        String to = "aziret5265@gmail.com"; // email, куда придёт сообщение
        String subject = "New Contact Message from " + fullName;
        String html = """
        <html>
        <body>
            <h3>New message from %s (%s)</h3>
            <p>%s</p>
        </body>
        </html>
        """.formatted(fullName, fromEmail, messageContent);

        sendEmail(to, subject, html, true);
    }
}
