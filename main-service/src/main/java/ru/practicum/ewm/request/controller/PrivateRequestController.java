package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestResponse;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestMapper;
import ru.practicum.ewm.request.services.PrivateRequestService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final PrivateRequestService requestService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public RequestResponse createRequest(@PathVariable @NotNull @Positive Long userId,
                                         @RequestParam @NotNull @Positive Long eventId) {

        Request request = requestService.createRequest(userId, eventId);
        return RequestMapper.requestToRequestResponse(request);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<RequestResponse> getAllRequestsByUserId(@PathVariable @NotNull @Positive Long userId) {

        List<Request> requests = requestService.getAllRequestsByUserId(userId);

        return requests.stream()
                .map(RequestMapper::requestToRequestResponse)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(code = HttpStatus.OK)
    public RequestResponse cancelRequestByRequester(@PathVariable @NotNull @Positive Long userId,
                                                 @PathVariable @NotNull @Positive Long requestId) {

        Request request = requestService.cancelRequestByRequester(userId, requestId);
        return RequestMapper.requestToRequestResponse(request);
    }
}
