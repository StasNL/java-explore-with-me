package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventRequest;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.services.AdminEventService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.utils.Constants.*;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping("/admin/events")
public class AdminEventController {

    private final AdminEventService eventService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<FullEventResponse> getAllEventsForAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) @Pattern(regexp = STANDARD_TIME_REGEX) String rangeStart,
            @RequestParam(required = false) @Pattern(regexp = STANDARD_TIME_REGEX) String rangeEnd,
            @RequestParam(required = false, defaultValue = PAGINATION_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = PAGINATION_SIZE) @Positive Integer size) {

        return eventService.getAllEventsForAdmin(
                users,
                states,
                categories,
                paid,
                onlyAvailable,
                rangeStart,
                rangeEnd,
                from,
                size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public FullEventResponse editEventByAdmin(@PathVariable @NotNull @Positive Long eventId,
                                              @RequestBody @Valid EventRequest request) {

        return eventService.editEventByAdmin(eventId, request);
    }
}