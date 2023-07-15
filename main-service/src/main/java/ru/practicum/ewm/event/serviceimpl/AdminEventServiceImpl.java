package ru.practicum.ewm.event.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dao.AdminEventDao;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventRequest;
import ru.practicum.ewm.event.dto.FullEventResponse;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.event.services.AdminEventService;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.DataConflictException;
import ru.practicum.ewm.request.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static io.micrometer.core.instrument.util.StringUtils.isBlank;
import static ru.practicum.ewm.event.model.enums.State.PUBLISHED;
import static ru.practicum.ewm.event.model.enums.State.REJECTED;
import static ru.practicum.ewm.utils.Constants.STANDARD_TIME_REGEX;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;
import static ru.practicum.ewm.utils.ExceptionMessages.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final AdminEventDao eventDao;
    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT);

    @Override
    public List<FullEventResponse> getAllEventsForAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            Boolean paid,
            Boolean onlyAvailable,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {

        LocalDateTime rangeStartLdt = null;
        if (rangeStart != null)
            rangeStartLdt = LocalDateTime.parse(rangeStart, dtf);

        LocalDateTime rangeEndLdt = null;
        if (rangeEnd != null)
            rangeEndLdt = LocalDateTime.parse(rangeEnd, dtf);

        if (rangeStartLdt != null && rangeEndLdt != null && rangeEndLdt.isBefore(rangeStartLdt))
            throw new BadRequestException(START_DT_AFTER_RND_DT);

        return eventDao.getAllEventsForAdmin(
                users,
                states,
                categories,
                paid,
                onlyAvailable,
                rangeStartLdt,
                rangeEndLdt,
                from,
                size);
    }

    @Override
    public FullEventResponse editEventByAdmin(Long eventId, EventRequest eventRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BadDBRequestException(EVENT_NO_ID));

        if (!isBlank(eventRequest.getAnnotation()))
            event.setAnnotation(eventRequest.getAnnotation());

        if (eventRequest.getCategory() != null) {
            Category category = categoryRepository.findById(eventRequest.getCategory())
                    .orElseThrow(() -> new BadDBRequestException(CATEGORY_NO_ID));
            event.setCategory(category);
        }

        if (!isBlank(eventRequest.getDescription()))
            event.setDescription(eventRequest.getDescription());

        if (!isBlank(eventRequest.getEventDate()) && eventRequest.getEventDate().matches(STANDARD_TIME_REGEX)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT);
            LocalDateTime ld = LocalDateTime.parse(eventRequest.getEventDate(), dtf);
            checkEventDate(ld);
            event.setEventDate(ld);
        }

        if (eventRequest.getLocation() != null && eventRequest.getLocation().getLocId() != null) {
            Location location = locationRepository.save(eventRequest.getLocation());
            event.setLocation(location);
        }

        if (eventRequest.getPaid() != null)
            event.setPaid(eventRequest.getPaid());

        if (eventRequest.getParticipantLimit() != null)
            event.setParticipantLimit(eventRequest.getParticipantLimit());

        if (eventRequest.getRequestModeration() != null)
            event.setRequestModeration(eventRequest.getRequestModeration());

        if (eventRequest.getStateAction() != null)
            switch (eventRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState().equals(PUBLISHED) || event.getState().equals(REJECTED))
                        throw new DataConflictException(WRONG_PUBLISHING);
                    event.setState(PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(PUBLISHED))
                        throw new DataConflictException(WRONG_REJECTING);
                    event.setState(REJECTED);
                    break;
            }

        if (!isBlank(eventRequest.getTitle()))
            event.setTitle(eventRequest.getTitle());

        event = eventRepository.save(event);

        Long confirmedRequests = requestRepository.getConfirmedRequestsByEventId(eventId);

        return EventMapper.eventToFullEventResponse(event, confirmedRequests);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now()))
            throw new BadRequestException(EVENT_DATE_IN_PAST);
    }
}