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
    @Size(min = 3)
    String annotation;
    @Positive
    Long category;
    @Size(min = 3)
    String description;
    // Соответствие формата времени шаблону yyyy-MM-dd HH:mm:ss
    @Pattern(regexp = STANDARD_TIME_REGEX)
    String eventDate;
    Location location;
    @Size(min = 3)
    String title;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
}
