package ru.practicum.ewm.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestStatusResponse {
    List<RequestResponse> confirmedRequests;
    List<RequestResponse> rejectedRequests;
}
