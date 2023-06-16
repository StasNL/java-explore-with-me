package ru.practicum.ewm.event.services;

import ru.practicum.ewm.event.dto.EventRequest;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.dto.NewEventRequest;
import ru.practicum.ewm.event.dto.ShortEventResponse;

import java.util.List;

public interface PrivateEventService {

    FullEventResponse createEvent(Long userId, NewEventRequest newEvent);

    FullEventResponse editEvent(Long userId, Long eventId, EventRequest event);

    FullEventResponse getEventByUserIdAndEventId(long userID, long eventId);

    List<ShortEventResponse> getAllEventsByUserId(Long userId, Integer from, Integer size);

}
