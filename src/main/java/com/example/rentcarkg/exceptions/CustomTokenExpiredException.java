package com.example.rentcarkg.exceptions;

public class CustomTokenExpiredException extends RuntimeException {
    public CustomTokenExpiredException(String message) {
        super(message);
    }
}
