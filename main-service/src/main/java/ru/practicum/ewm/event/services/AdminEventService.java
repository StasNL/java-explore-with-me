package ru.practicum.ewm.event.services;

import ru.practicum.ewm.event.dto.EventRequest;
import ru.practicum.ewm.event.dto.FullEventResponse;

import java.util.List;

public interface AdminEventService {

    List<FullEventResponse> getAllEventsForAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            Boolean paid,
            Boolean onlyAvailable,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    FullEventResponse editEventByAdmin(Long eventId, EventRequest request);
}
