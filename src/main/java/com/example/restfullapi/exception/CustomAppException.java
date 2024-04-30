package com.example.restfullapi.exception;

public class CustomAppException extends RuntimeException {
    public CustomAppException(String message) {
        super(message);
    }
}