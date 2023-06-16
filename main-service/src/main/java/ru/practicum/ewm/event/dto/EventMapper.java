package ru.practicum.ewm.event.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.ShortEvent;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.model.enums.State.PENDING;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern(ru.practicum.ewm.utils.Constants.TIME_FORMAT);

    public static Event newEventToEvent(NewEventRequest newEvent, User initiator, Category category, Location location) {

        return Event.builder()
                .title(newEvent.getTitle())
                .annotation(newEvent.getAnnotation())
                .description(newEvent.getDescription())
                .eventDate(LocalDateTime.parse(newEvent.getEventDate(), dtf))
                .views(0L)
                .initiator(initiator)
                .createdOn(LocalDateTime.now())
                .category(category)
                .location(location)
                .publishedOn(LocalDateTime.now())
                .paid(newEvent.getPaid())
                .participantLimit(newEvent.getParticipantLimit())
                .requestModeration(newEvent.getRequestModeration())
                .state(PENDING)
                .build();
    }

    public static FullEventResponse eventToFullEventResponse(Event event, Long confirmedRequests) {

        return FullEventResponse.builder()
                .id(event.getEventId())
                .title(event.getTitle())
                .views(event.getViews())
                .paid(event.getPaid())
                .confirmedRequests(confirmedRequests)
                .initiator(event.getInitiator())
                .eventDate(event.getEventDate().format(dtf))
                .category(event.getCategory())
                .annotation(event.getAnnotation())
                .createdOn(event.getCreatedOn().format(dtf))
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(dtf))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .location(event.getLocation())
                .build();
    }

    public static ShortEventResponse shortEventToShortEventResponse(ShortEvent shortEvent) {
        return ShortEventResponse.builder()
                .id(shortEvent.getId())
                .title(shortEvent.getTitle())
                .views(shortEvent.getViews())
                .confirmedRequests(shortEvent.getConfirmedRequests())
                .eventDate(shortEvent.getEventDate().format(dtf))
                .annotation(shortEvent.getAnnotation())
                .paid(shortEvent.getPaid())
                .category(shortEvent.getCategory())
                .initiator(shortEvent.getInitiator())
                .build();
    }

    public static List<ShortEventResponse> shortEventToShortEventResponse(List<ShortEvent> shortEvents) {
        return shortEvents.stream()
                .map(EventMapper::shortEventToShortEventResponse)
                .collect(Collectors.toList());
    }
}