package ru.practicum.ewm.request.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.services.PrivateEventService;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.DataConflictException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.dto.RequestResponse;
import ru.practicum.ewm.request.dto.RequestStatusDto;
import ru.practicum.ewm.request.dto.RequestStatusResponse;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.model.enums.State.PUBLISHED;
import static ru.practicum.ewm.request.model.RequestStatus.*;
import static ru.practicum.ewm.utils.ExceptionMessages.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventDao eventDao;
    private final UserRepository userRepository;
    private final PrivateEventService eventService;

    @Override
    public RequestStatusResponse editStatuses(Long userId, Long eventId, RequestStatusDto requestStatus) {

        List<Request> requests = requestRepository.findAllById(requestStatus.getRequestIds());

        if (requestStatus.getRequestIds().size() != requests.size())
            throw new BadRequestException(REQUEST_WRONG_LIST);

        checkRequestsByEvents(eventId, requests);

        if (CONFIRMED.equals(requestStatus.getStatus())) {
            Long participantLimit = eventDao.getConfirmedRequestsByEventId(eventId);
            Long confirmedRequests = requestRepository.getConfirmedRequestsByEventId(eventId);
            if (participantLimit < (confirmedRequests + requests.size()))
                throw new DataConflictException(CONFIRMED_REQUEST_LIMITS);
        }

        if (REJECTED.equals(requestStatus.getStatus())) {
            for (Request request : requests) {
                if (request.getStatus().equals(CONFIRMED))
                    throw new DataConflictException(REJECT_CONFIRMED_REQUEST);
            }
        }
        requests.forEach(request -> request.setStatus(requestStatus.getStatus()));
        requests = requestRepository.saveAll(requests);
        return RequestMapper.mapToRequestStatusResponse(requests, requestStatus.getStatus());
    }

    @Override
    public List<RequestResponse> getAllRequestsByUserIdAndEventId(Long userId, Long eventId) {
        eventService.checkEventByOwnerId(userId, eventId);

        List<Request> requests = requestRepository.findAllByEvent_EventId(eventId);

        return requests.stream()
                .map(RequestMapper::requestToRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Request createRequest(long userId, long eventId) {
        Event event = checkBeforeCreating(userId, eventId);
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new BadDBRequestException(USER_NO_ID));

        Request request = Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .build();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration())
            request.setStatus(CONFIRMED);
        else
            request.setStatus(PENDING);
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getAllRequestsByUserId(Long userId) {
        return requestRepository.findAllByRequester_UserId(userId);
    }

    @Override
    public Request cancelRequestByRequester(long userId, long requestId) {

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new BadDBRequestException(REQUEST_NO_ID));

        if (request.getRequester().getUserId() != userId)
            throw new BadRequestException(WRONG_REQUESTER);

        request.setStatus(CANCELED);

        return requestRepository.save(request);
    }

    private void checkRequestsByEvents(long eventId, List<Request> requests) {
        List<String> wrongIds = new ArrayList<>();
        for (Request request : requests) {
            if (request.getEvent().getEventId() != eventId)
                wrongIds.add(String.valueOf(request.getReqId()));
        }

        if (!wrongIds.isEmpty()) {
            String ids = String.join(", ", wrongIds);
            throw new BadRequestException("Запросы с id: " + ids + " не принадлежат данному событию.");
        }
    }

    private Event checkBeforeCreating(long userId, long eventId) {
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new BadDBRequestException(EVENT_NO_ID));

        if (!event.getState().equals(PUBLISHED)) {
            throw new DataConflictException(REQUEST_UNPUBLISHED);
        }

        if (event.getInitiator().getUserId() == userId)
            throw new DataConflictException(REQUESTER_IS_INITIATOR);

        boolean isRequests = !requestRepository.findAllByRequesterIdAndEventId(userId, eventId).isEmpty();
        if (isRequests)
            throw new DataConflictException(REQUESTER_EXISTS);

        Long confirmedRequests = event.getRequests().stream()
                .filter(request -> request.getStatus() == CONFIRMED)
                .count();

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new DataConflictException(CONFIRMED_REQUEST_LIMITS);
        }

        return event;
    }
}