package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT COUNT(r) " +
            "FROM Request AS r " +
            "WHERE r.event.eventId = ?1 AND r.status = 'CONFIRMED' ")
    Long getConfirmedRequestsByEvent_EventId(Long eventId);

    List<Request> findAllByRequester_UserIdAndEvent_EventId(Long userId, Long eventId);

    List<Request> findAllByRequester_UserId(Long userId);
}
