package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortEventResponse {
    private Long id;
    private String title;
    private Long views;
    private Boolean paid;
    private Long confirmedRequests;
    private User initiator;
    private String eventDate;
    private Category category;
    private String annotation;
}