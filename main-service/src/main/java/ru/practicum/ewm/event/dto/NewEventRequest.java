package ru.practicum.ewm.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;

import static ru.practicum.ewm.utils.Constants.STANDARD_TIME_REGEX;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventRequest {
    @NotBlank
    String annotation;
    @NotNull
    @Positive
    Long category;
    @NotBlank
    String description;
    @Pattern(regexp = STANDARD_TIME_REGEX)
    String eventDate;
    NewLocation location;
    @NotBlank
    String title;
    @NotNull
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    @NotNull
    Boolean requestModeration;
}