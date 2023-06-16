package ru.practicum.ewm.event.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.service.PublicCategoryService;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.event.services.PrivateEventService;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.service.AdminUserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.model.enums.State.CANCELED;
import static ru.practicum.ewm.event.model.enums.State.PENDING;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;
import static ru.practicum.ewm.utils.ExceptionMessages.EVENT_NO_ID;
import static ru.practicum.ewm.utils.ExceptionMessages.EVENT_WRONG_INITIATOR;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final AdminUserService userService;
    private final RequestRepository requestRepository;
    private final PublicCategoryService categoryService;
    private final LocationRepository locationRepository;
    private final EventDao eventDao;

    @Override
    public FullEventResponse createEvent(Long userId, NewEventRequest newEvent) {

        User initiator = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(newEvent.getCategory());
        Location location = LocationMapper.newLocationToLocation(newEvent.getLocation());
        location = locationRepository.save(location);

        Event event = EventMapper.newEventToEvent(newEvent, initiator, category, location);
        event = eventRepository.save(event);

        Long confirmedRequests = requestRepository.getConfirmedRequestsByEvent_EventId(event.getEventId());

        return EventMapper.eventToFullEventResponse(event, confirmedRequests);

    }

    @Override
    public FullEventResponse editEvent(Long userId, Long eventId, EventRequest eventRequest) {

        Event event = checkEventByUserId(userId, eventId);

        if (eventRequest.getTitle() != null)
            event.setTitle(eventRequest.getTitle());

        if (eventRequest.getDescription() != null)
            event.setDescription(eventRequest.getDescription());

        if (eventRequest.getEventDate() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT);
            LocalDateTime eventDate = LocalDateTime.parse(eventRequest.getEventDate(), dtf);
            event.setEventDate(eventDate);
        }

        if (eventRequest.getLocation() != null) {
            Location location = locationRepository.save(eventRequest.getLocation());
            event.setLocation(location);
        }

        if (eventRequest.getAnnotation() != null)
            event.setAnnotation(eventRequest.getAnnotation());

        if (eventRequest.getPaid() != null)
            event.setPaid(eventRequest.getPaid());

        if (eventRequest.getParticipantLimit() != null)
            event.setParticipantLimit(eventRequest.getParticipantLimit());

        if (eventRequest.getRequestModeration() != null)
            event.setRequestModeration(eventRequest.getRequestModeration());

        if (eventRequest.getStateAction() != null)
            switch (eventRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
            }

        if (eventRequest.getCategory() != null) {
            Category category = categoryService.getCategoryById(eventRequest.getCategory());
            event.setCategory(category);
        }

        event = eventRepository.save(event);
        Long confirmedRequests = requestRepository.getConfirmedRequestsByEvent_EventId(event.getEventId());

        return EventMapper.eventToFullEventResponse(event, confirmedRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public FullEventResponse getEventByUserIdAndEventId(long userId, long eventId) {
        Event event = checkEventByUserId(userId, eventId);
        Long confirmedRequests = requestRepository.getConfirmedRequestsByEvent_EventId(event.getEventId());

        return EventMapper.eventToFullEventResponse(event, confirmedRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortEventResponse> getAllEventsByUserId(Long userId, Integer from, Integer size) {



        return eventDao.getAllEventsByUserId(userId, from, size).stream()
                .map(EventMapper::shortEventToShortEventResponse)
                .collect(Collectors.toList());
    }

    private Event checkEventByUserId(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BadDBRequestException(EVENT_NO_ID));

        if (event.getInitiator().getUserId() != userId)
            throw new BadRequestException(EVENT_WRONG_INITIATOR);
        return event;
    }
}
