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
    @Size(min = 20, max = 2000, message = "Слишком длинная или короткая аннотация события")
    String annotation;
    @NotNull
    @Positive
    Long category;
    @NotBlank
    @Size(min = 20, max = 7000, message = "Слишком длинное или короткое описание события")
    String description;
    @Pattern(regexp = STANDARD_TIME_REGEX)
    String eventDate;
    NewLocation location;
    @NotBlank
    @Size(min = 3, max = 120, message = "Слишком длинное или короткое название события")
    String title;
    @NotNull
    @Builder.Default
    Boolean paid = false;
    @PositiveOrZero
    @Builder.Default
    Long participantLimit = 0L;
    @NotNull
    @Builder.Default
    Boolean requestModeration = true;
}