package ru.practicum.ewm.event.services;

import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.dto.ShortEventResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicEventService {

    List<ShortEventResponse> getAllEvents(String text,
                                          List<Long> categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          Boolean onlyAvailable,
                                          String sort,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest request);

    FullEventResponse getFullEvent(Long eventId, HttpServletRequest request);
}
