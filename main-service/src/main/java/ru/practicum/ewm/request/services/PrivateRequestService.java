package ru.practicum.ewm.request.services;

import ru.practicum.ewm.request.dto.RequestResponse;
import ru.practicum.ewm.request.dto.RequestStatusDto;
import ru.practicum.ewm.request.dto.RequestStatusResponse;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

public interface PrivateRequestService {
    RequestStatusResponse editStatuses(Long userId, Long eventId, RequestStatusDto requestStatus);

    List<RequestResponse> getAllRequestsByUserIdAndEventId(Long userId, Long eventId);

    Request createRequest(long userId, long eventId);

    List<Request> getAllRequestsByUserId(Long userId);

    Request cancelRequestByRequester(long userId, long requestId);
}
