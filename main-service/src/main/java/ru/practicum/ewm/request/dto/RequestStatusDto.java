package ru.practicum.ewm.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestStatusDto {

    List<Long> requestIds;
    RequestStatus status;
}
