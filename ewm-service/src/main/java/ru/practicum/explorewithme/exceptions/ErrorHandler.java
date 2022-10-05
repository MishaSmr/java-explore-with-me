package ru.practicum.explorewithme.exceptions;

import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.EWMDateTimeFormatter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    private static final DateTimeFormatter formatter = EWMDateTimeFormatter.FORMATTER;

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        return new ApiError(
                HttpStatus.BAD_REQUEST.toString(),
                "Incorrectly made request.",
                String.format("During [newEventDto] validation %d errors were found", e.getMessages().size()),
                e.getMessages(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiError handleEventNotFoundException(final EventNotFoundException e) {
        return new ApiError(
                HttpStatus.NOT_FOUND.toString(),
                "The required object was not found.",
                e.getMessage(),
                Collections.emptyList(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiError handleEventUpdateException(final EventUpdateException e) {
        return new ApiError(
                HttpStatus.FORBIDDEN.toString(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                Collections.emptyList(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiError handleEventPublicationException(final EventPublicationException e) {
        return new ApiError(
                HttpStatus.FORBIDDEN.toString(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                Collections.emptyList(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiError handleEventNotPublishedException(final EventNotPublishedException e) {
        return new ApiError(
                HttpStatus.FORBIDDEN.toString(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                Collections.emptyList(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiError handleParticipationRequestException(final ParticipationRequestException e) {
        return new ApiError(
                HttpStatus.FORBIDDEN.toString(),
                "For the requested operation the conditions are not met.",
                e.getMessage(),
                Collections.emptyList(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiError handlePSQLException(final ConstraintViolationException e) {
        return new ApiError(
                HttpStatus.CONFLICT.toString(),
                "Integrity constraint has been violated",
                e.getSQLException().getMessage(),
                Collections.emptyList(),
                LocalDateTime.now().format(formatter)
        );
    }
}
