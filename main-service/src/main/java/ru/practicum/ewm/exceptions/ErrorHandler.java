package ru.practicum.ewm.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(TIME_FORMAT);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleValidationException(final MethodArgumentNotValidException exception) {

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Validation failed")
                .timestamp(LocalDateTime.now().format(DTF))
                .status(BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler(BadDBRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleBadDBRequestException(final BadDBRequestException exception) {
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Bad request to db")
                .timestamp(LocalDateTime.now().format(DTF))
                .status(BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(CONFLICT)
    public ExceptionResponse handleBadRequestException(final BadRequestException exception) {
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Bad data")
                .timestamp(LocalDateTime.now().format(DTF))
                .status(CONFLICT.name())
                .build();
    }
}
