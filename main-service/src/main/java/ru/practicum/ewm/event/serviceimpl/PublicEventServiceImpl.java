package ru.practicum.ewm.event.serviceimpl;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.NotFoundException;
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
import java.util.*;

import static ru.practicum.ewm.event.model.enums.State.PUBLISHED;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;
import static ru.practicum.ewm.utils.ExceptionMessages.EVENT_NO_ID;
import static ru.practicum.ewm.utils.ExceptionMessages.START_DT_AFTER_RND_DT;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final StatsClient statsClient;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventDao eventDao;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT);

    @Transactional
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

        LocalDateTime rangeStartLdt = null;
        if (rangeStart != null)
            rangeStartLdt = LocalDateTime.parse(rangeStart, dtf);

        LocalDateTime rangeEndLdt = null;
        if (rangeEnd != null)
            rangeEndLdt = LocalDateTime.parse(rangeEnd, dtf);

        if (rangeStartLdt != null && rangeEndLdt != null && rangeEndLdt.isBefore(rangeStartLdt))
            throw new BadRequestException(START_DT_AFTER_RND_DT);

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

        // Учитываем просмотры
        events.forEach(event -> {
            String request = httpRequest.getRequestURI() + "/" + event.getId();
            HitDto hit = makeHit(request, httpRequest.getRemoteAddr());
            statsClient.saveStats(hit);
        });

        // Запрашиваем просмотры
        Map<Long, Long> views = getViews(events);

        eventDao.updateViews(views);

        events = eventDao.getFilteredEvents(
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

        return EventMapper.shortEventToShortEventResponse(events);
    }


    @Transactional
    @Override
    public FullEventResponse getFullEvent(Long eventId, HttpServletRequest httpRequest) {

        Event event = getEventById(eventId);

        if (!event.getState().equals(PUBLISHED))
            throw new NotFoundException("Попытка получения неопубликованного события");

        // Учитываем просмотр
        HitDto hit = makeHit(httpRequest.getRequestURI(), httpRequest.getRemoteAddr());
        statsClient.saveStats(hit);

        // Запрашиваем просмотры
        Map<Long, Long> views = getViews(List.of(EventMapper.eventToShortEvent(event)));

        eventDao.updateViews(views);
        event.setViews(views.get(eventId));

        Long confirmedRequests = requestRepository.getConfirmedRequestsByEventId(eventId);
        return EventMapper.eventToFullEventResponse(event, confirmedRequests);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BadDBRequestException(EVENT_NO_ID));
    }

    private HitDto makeHit(String requestURI, String remoteAddr) {

        return HitDto.builder()
                .app("Explore with me")
                .uri(requestURI)
                .ip(remoteAddr)
                .timestamp(LocalDateTime.now().format(dtf))
                .build();
    }

    public Map<Long, Long> getViews(List<ShortEvent> events) {
        Map<String, Long> uris = new HashMap<>();
        LocalDateTime minPublishedDt = LocalDateTime.now();

        for (ShortEvent event : events) {
            uris.put("/events/" + event.getId(), event.getId());

            if (event.getPublicationDate().isBefore(minPublishedDt)) {
                minPublishedDt = event.getPublicationDate();
            }
        }

        List<StatsDto> stats = statsClient.getStats(minPublishedDt.format(dtf),
                LocalDateTime.now().format(dtf),
                new ArrayList<>(uris.keySet()),
                true);

        Map<Long, Long> views = new HashMap<>();

        for (StatsDto stat : stats) {
            String uri = stat.getUri();
            views.put(uris.get(uri), stat.getHits());
        }

        return views;
    }
}