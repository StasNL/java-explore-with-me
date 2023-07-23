package ru.practicum.ewm.event.dao;

import ru.practicum.ewm.event.dto.FullEventResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventDao {

    List<FullEventResponse> getAllEventsForAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            Boolean paid,
            Boolean onlyAvailable,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size);
}
