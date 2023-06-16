package ru.practicum.ewm.exceptions;

public class BadDBRequestException extends RuntimeException {

    public BadDBRequestException(String message) {
        super(message);
    }
}