package ru.practicum.exceptions;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
}
