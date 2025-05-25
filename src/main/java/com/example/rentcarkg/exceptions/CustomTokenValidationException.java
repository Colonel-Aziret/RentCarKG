package com.example.rentcarkg.exceptions;

public class CustomTokenValidationException extends RuntimeException {
    public CustomTokenValidationException(String message) {
        super(message);
    }
}
