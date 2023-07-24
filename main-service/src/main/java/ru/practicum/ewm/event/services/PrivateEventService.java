package ru.practicum.ewm.event.services;

import ru.practicum.ewm.event.dto.EventRequest;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.dto.NewEventRequest;
import ru.practicum.ewm.event.dto.ShortEventResponse;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface PrivateEventService {

    FullEventResponse createEvent(Long userId, NewEventRequest newEvent);

    FullEventResponse editEvent(Long userId, Long eventId, EventRequest event);

    FullEventResponse getEventByUserIdAndEventId(long userID, long eventId);

    List<ShortEventResponse> getAllEventsByUserId(Long userId, Integer from, Integer size);

    Event checkEventByOwnerId(long userId, long eventId);

    void addRating(Long userId, Long eventId, Integer rating);

    ShortEventResponse getEventById(Long eventId);
}
