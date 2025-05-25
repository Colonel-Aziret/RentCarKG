package com.example.rentcarkg.controller;

import com.example.rentcarkg.dto.request.ContactMessageRequestDto;
import com.example.rentcarkg.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendTestEmail(@RequestParam String to,
                                                @RequestParam String subject,
                                                @RequestParam String text) {
        emailService.sendEmail(to, subject, text, true);
        return ResponseEntity.ok("Email sent to " + to);
    }

    @PostMapping("/contact")
    public ResponseEntity<String> sendContactMessage(@RequestBody ContactMessageRequestDto dto) {
        emailService.sendContactMessage(dto.fullName(), dto.email(), dto.email());
        return ResponseEntity.ok("Message sent successfully!");
    }

}
