package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.dto.ShortEventResponse;
import ru.practicum.ewm.event.services.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class PublicEventController {
    private final PublicEventService eventService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ShortEventResponse> getAllEvents(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(required = false) Boolean onlyAvailable,
                                                 @RequestParam(required = false) Integer minRating,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                                 HttpServletRequest request) {
        return eventService.getAllEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                minRating,
                sort,
                from,
                size,
                request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public FullEventResponse getFullEvent(@NotNull @PathVariable(value = "id") Long eventId,
                                          HttpServletRequest httpRequest) {
        return eventService.getFullEvent(eventId, httpRequest);
    }
}