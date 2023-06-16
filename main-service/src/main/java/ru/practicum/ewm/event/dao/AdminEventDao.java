package ru.practicum.ewm.event.dao;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.ShortEvent;
import ru.practicum.ewm.event.model.enums.State;

import java.util.List;

public interface AdminEventDao {

    List<ShortEvent> getAllEventsForAdmin(
            List<Long> users,
            List<State> states,
            List<Category> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);
}
