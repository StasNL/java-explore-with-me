package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(attributePaths = {"requests"})
    Optional<Event> findByEventId(long eventId);

    List<Event> findAllByCategory_CatId(Long catId);
}
