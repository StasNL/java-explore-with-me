package ru.practicum.ewm.event.dao;

import ru.practicum.ewm.event.model.ShortEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventDao {

    List<ShortEvent> getFilteredEvents(String text,
                                       List<Long> categories,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Integer from,
                                       Integer size,
                                       String sortBy,
                                       Boolean onlyAvailable,
                                       Integer minRating);

    List<ShortEvent> getAllEventsByUserId(Long initiatorId, Integer from, Integer size);

    List<ShortEvent> getAllEventsByCompilationId(Long compId);

    Map<Long, List<ShortEvent>> getAllEventsByCompilations(List<Long> compIds);

    Long getConfirmedRequestsByEventId(Long eventId);

    void updateViews(Map<Long, Long> views);

    void addRating(Long userId, Long eventId, Integer rating);

    void countRating(Long userId, Long eventId);
}