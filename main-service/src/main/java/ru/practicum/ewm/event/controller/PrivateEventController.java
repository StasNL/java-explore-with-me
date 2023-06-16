package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.services.PrivateEventService;
import ru.practicum.ewm.request.dto.RequestResponse;
import ru.practicum.ewm.request.dto.RequestStatusDto;
import ru.practicum.ewm.request.dto.RequestStatusResponse;
import ru.practicum.ewm.request.services.PrivateRequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.utils.Constants.PAGINATION_FROM;
import static ru.practicum.ewm.utils.Constants.PAGINATION_SIZE;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final PrivateEventService eventService;
    private final PrivateRequestService requestService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public FullEventResponse createEvent(@PathVariable @NotNull @Positive Long userId,
                                         @RequestBody @Valid NewEventRequest eventRequest) {

        return eventService.createEvent(userId, eventRequest);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public FullEventResponse editEvent(@PathVariable @NotNull @Positive Long userId,
                                       @PathVariable @NotNull @Positive Long eventId,
                                       @RequestBody @Valid EventRequest eventRequest) {

        return eventService.editEvent(userId, eventId, eventRequest);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public FullEventResponse getEventByUserIdAndEventId(@PathVariable @NotNull @Positive Long userId,
                                                        @PathVariable @NotNull @Positive Long eventId) {

        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ShortEventResponse> getAllEventsByUserId(
            @PathVariable @NotNull @Positive Long userId,
            @RequestParam(required = false, defaultValue = PAGINATION_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = PAGINATION_SIZE) @PositiveOrZero Integer size) {

        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public RequestStatusResponse editStatuses(@PathVariable @NotNull @Positive Long userId,
                                              @PathVariable @NotNull @Positive Long eventId,
                                              @RequestBody @Valid RequestStatusDto requestStatus) {

        return requestService.editStatuses(userId, eventId, requestStatus);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public List<RequestResponse> getAllRequestsByUserIdAndEventId(@PathVariable @NotNull @Positive Long userId,
                                                                  @PathVariable @NotNull @Positive Long eventId) {

        return requestService.getAllRequestsByUserIdAndEventId(userId, eventId);
    }
}