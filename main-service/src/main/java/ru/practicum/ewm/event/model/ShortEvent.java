package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortEvent {
    private Long id;
    private String title;
    private Long views;
    private Boolean paid;
    private Long confirmedRequests;
    private User initiator;
    private LocalDateTime eventDate;
    private Category category;
    private String annotation;
    private LocalDateTime publicationDate;
}
