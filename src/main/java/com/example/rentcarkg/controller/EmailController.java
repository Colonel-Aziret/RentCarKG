package com.example.rentcarkg.controller;

import com.example.rentcarkg.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public String sendTestEmail(@RequestParam String to,
                                @RequestParam String subject,
                                @RequestParam String text) {
        emailService.sendEmail(to, subject, text);
        return "Email sent to " + to;
    }
}
