package ru.practicum.ewm.event.serviceimpl;

import dto.HitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats_client.StatsClient;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.dto.ShortEventResponse;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.ShortEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.services.PublicEventService;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.request.RequestRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;
import static ru.practicum.ewm.utils.ExceptionMessages.EVENT_NO_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicEventServiceImpl implements PublicEventService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventDao eventDao;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT);

    @Override
    public List<ShortEventResponse> getAllEvents(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sortBy,
                                                 Integer from,
                                                 Integer size,
                                                 HttpServletRequest httpRequest) {

        LocalDateTime rangeStartLdt = LocalDateTime.parse(rangeStart, dtf);
        LocalDateTime rangeEndLdt = LocalDateTime.parse(rangeEnd, dtf);

        List<ShortEvent> events = eventDao.getFilteredEvents(
                text,
                categories,
                paid,
                rangeStartLdt,
                rangeEndLdt,
                from,
                size,
                sortBy,
                onlyAvailable
        );

        events = events.stream()
                .peek(event -> {
                    String request = httpRequest.getRequestURI() + "/" + event.getId();
                    event.setViews(event.getViews() + 1);
                    HitDto hit = makeHit(request, httpRequest.getRemoteAddr());
                    statsClient.saveStats(hit);
                })
                .collect(Collectors.toList());

        List<Long> eventIds = events.stream()
                .map(ShortEvent::getId)
                .collect(Collectors.toList());

        eventDao.increaseViews(eventIds);

        return EventMapper.shortEventToShortEventResponse(events);
    }

    @Override
    public FullEventResponse getFullEvent(Long eventId, HttpServletRequest httpRequest) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BadDBRequestException(EVENT_NO_ID));

        Long confirmedRequests = requestRepository.getConfirmedRequestsByEvent_EventId(eventId);

        eventDao.increaseViews(List.of(eventId));

        HitDto hit = makeHit(httpRequest.getRequestURI(), httpRequest.getRemoteAddr());
        statsClient.saveStats(hit);
        event.setViews(event.getViews() + 1);

        return EventMapper.eventToFullEventResponse(event, confirmedRequests);
    }

    private HitDto makeHit(String requestURI, String remoteAddr) {

        return HitDto.builder()
                .app("Explore with me")
                .uri(requestURI)
                .ip(remoteAddr)
                .timestamp(LocalDateTime.now().format(dtf))
                .build();
    }
}