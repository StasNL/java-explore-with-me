package ru.practicum.ewm.request.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;
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
public class PrivateRequestServiceImpl implements PrivateRequestService{

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventDao eventDao;
    private final UserRepository userRepository;
    @Override
    public RequestStatusResponse editStatuses(Long userId, Long eventId, RequestStatusDto requestStatus) {

        List<Request> requests = requestRepository.findAllById(requestStatus.getRequestIds());

        if (requestStatus.getRequestIds().size() != requests.size())
            throw new BadRequestException(REQUEST_WRONG_LIST);

        checkRequestsByEvents(eventId, requests);

        if (CONFIRMED.equals(requestStatus.getStatus())) {
            Long participantLimit = eventDao.getConfirmedRequestsByEventId(eventId);
            Long confirmedRequests = requestRepository.getConfirmedRequestsByEvent_EventId(eventId);
            if (participantLimit < (confirmedRequests + requests.size()))
                throw new BadRequestException(CONFIRMED_REQUEST_LIMITS);
        }

        requests.forEach(request -> request.setStatus(requestStatus.getStatus()));

        return RequestMapper.mapToRequestStatusResponse(requests, requestStatus.getStatus());
    }

    @Override
    public List<RequestResponse> getAllRequestsByUserIdAndEventId(Long userId, Long eventId) {
        List<Request> requests = requestRepository.findAllByRequester_UserIdAndEvent_EventId(userId, eventId);

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
                .status(PENDING)
                .event(event)
                .created(LocalDateTime.now())
                .build();

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
        Event event = eventRepository.findByEventIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new BadDBRequestException(EVENT_NO_ID));

        if (event.getInitiator().getUserId() == userId)
            throw new BadRequestException(REQUESTER_IS_INITIATOR);

        boolean isRequests = !requestRepository.findAllByRequester_UserIdAndEvent_EventId(userId, eventId).isEmpty();
        if (isRequests)
            throw new BadRequestException(REQUESTER_EXISTS);

        Long confirmedRequests = event.getRequests().stream()
                .filter(request -> request.getStatus() == CONFIRMED)
                .count();

        if (event.getParticipantLimit() <= confirmedRequests)
            throw new BadRequestException(CONFIRMED_REQUEST_LIMITS);

        return event;
    }
}