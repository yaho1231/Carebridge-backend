package com.example.carebridge.exception;

public class HospitalInformationException extends RuntimeException {
    public HospitalInformationException(String message) {
        super(message);
    }

    public HospitalInformationException(String message, Throwable cause) {
        super(message, cause);
    }
} 