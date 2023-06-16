package ru.practicum.ewm.event.services;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventRequest;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.dto.ShortEventResponse;
import ru.practicum.ewm.event.model.enums.State;

import java.util.List;

public interface AdminEventService {

    List<ShortEventResponse> getAllEventsForAdmin(
            List<Long> users,
            List<State> states,
            List<Category> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    FullEventResponse editEventByAdmin(Long eventId, EventRequest request);
}
