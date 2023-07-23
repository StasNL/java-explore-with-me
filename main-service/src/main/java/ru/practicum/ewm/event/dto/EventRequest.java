package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.enums.StateAction;

import javax.validation.constraints.*;

import static ru.practicum.ewm.utils.Constants.STANDARD_TIME_REGEX;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequest {
    @Size(min = 20, max = 2000, message = "Слишком длинная или короткая аннотация события")
    String annotation;
    @Positive
    Long category;
    @Size(min = 20, max = 7000, message = "Слишком длинное или короткое описание события")
    String description;
    // Соответствие формата времени шаблону yyyy-MM-dd HH:mm:ss
    @Pattern(regexp = STANDARD_TIME_REGEX)
    String eventDate;
    Location location;
    @Size(min = 3, max = 120, message = "Слишком длинное или короткое название события")
    String title;
    Boolean paid;
    @PositiveOrZero
    Long participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
}
