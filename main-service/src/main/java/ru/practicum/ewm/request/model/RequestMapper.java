package ru.practicum.ewm.request.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.RequestResponse;
import ru.practicum.ewm.request.dto.RequestStatusResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.request.model.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.utils.Constants.TIME_FORMAT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestMapper {

    public static RequestResponse requestToRequestResponse(Request request) {
        return RequestResponse.builder()
                .id(request.getReqId())
                .requester(request.getRequester().getUserId())
                .event(request.getEvent().getEventId())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern(TIME_FORMAT)))
                .status(request.getStatus().name())
                .build();
    }

    public static RequestStatusResponse mapToRequestStatusResponse(List<Request> requests, RequestStatus status) {
        RequestStatusResponse response = new RequestStatusResponse();

        List<RequestResponse> requestResponses = requests.stream()
                .map(RequestMapper::requestToRequestResponse)
                .collect(Collectors.toList());

        if (CONFIRMED.equals(status))
            response.setConfirmedRequests(requestResponses);
        else
            response.setRejectedRequests(requestResponses);

        return response;
    }
}
