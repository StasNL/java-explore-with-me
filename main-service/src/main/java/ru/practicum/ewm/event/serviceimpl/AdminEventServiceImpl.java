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
import ru.practicum.ewm.event.dto.ShortEventResponse;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.enums.State;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.services.AdminEventService;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.request.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static io.micrometer.core.instrument.util.StringUtils.isBlank;
import static ru.practicum.ewm.event.model.enums.State.PUBLISHED;
import static ru.practicum.ewm.event.model.enums.State.REJECTED;
import static ru.practicum.ewm.utils.Constants.STANDARD_TIME_REGEX;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;
import static ru.practicum.ewm.utils.ExceptionMessages.CATEGORY_NO_ID;
import static ru.practicum.ewm.utils.ExceptionMessages.EVENT_NO_ID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final AdminEventDao eventDao;
    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ShortEventResponse> getAllEventsForAdmin(
            List<Long> users,
            List<State> states,
            List<Category> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {

        return eventDao.getAllEventsForAdmin(
                        users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd,
                        from,
                        size).stream()
                .map(EventMapper::shortEventToShortEventResponse)
                .collect(Collectors.toList());
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
            event.setEventDate(ld);
        }

        if (eventRequest.getLocation() != null)
            event.setLocation(eventRequest.getLocation());

        if (eventRequest.getPaid() != null)
            event.setPaid(eventRequest.getPaid());

        if (eventRequest.getParticipantLimit() != null)
            event.setParticipantLimit(eventRequest.getParticipantLimit());

        if (eventRequest.getRequestModeration() != null)
            event.setRequestModeration(eventRequest.getRequestModeration());

        if (eventRequest.getStateAction() != null)
            switch (eventRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(REJECTED);
                    break;
            }

        if (!isBlank(eventRequest.getTitle()))
            event.setTitle(eventRequest.getTitle());

        Long confirmedRequests = requestRepository.getConfirmedRequestsByEvent_EventId(event.getEventId());

        return EventMapper.eventToFullEventResponse(event, confirmedRequests);
    }
}