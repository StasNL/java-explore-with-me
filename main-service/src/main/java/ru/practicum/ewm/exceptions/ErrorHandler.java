package ru.practicum.ewm.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.*;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleValidationException(final MethodArgumentNotValidException exception) {

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Validation failed")
                .timestamp(LocalDateTime.now().format(dtf))
                .status(BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleValidationException(final RuntimeException exception) {

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Validation failed")
                .timestamp(LocalDateTime.now().format(dtf))
                .status(BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler(BadDBRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleBadDBRequestException(final BadDBRequestException exception) {
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Bad request to db")
                .timestamp(LocalDateTime.now().format(dtf))
                .status(BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(BAD_REQUEST)
    public ExceptionResponse handleBadRequestException(final BadRequestException exception) {
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Bad data")
                .timestamp(LocalDateTime.now().format(dtf))
                .status(BAD_REQUEST.name())
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ExceptionResponse handleNotFoundException(final NotFoundException exception) {

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Validation failed")
                .timestamp(LocalDateTime.now().format(dtf))
                .status(NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(CONFLICT)
    public ExceptionResponse handleDataConflictException(final DataConflictException exception) {
        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .reason("Validation failed")
                .timestamp(LocalDateTime.now().format(dtf))
                .status(CONFLICT.name())
                .build();
    }
}
