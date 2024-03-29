package ru.practicum.ewm.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.enums.State;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ev_id")
    Long eventId;
    String title;
    String description;
    Boolean paid;
    Long views;
    @Column(name = "creation_date")
    LocalDateTime createdOn;
    @Column(name = "publication_date")
    LocalDateTime publishedOn;
    @Column(name = "participants_limit")
    Long participantLimit;
    @Column(name = "request_moderation")
    Boolean requestModeration;
    String annotation;
    @Column(name = "event_date")
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "user_id", nullable = false)
    User initiator;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false, referencedColumnName = "loc_id")
    Location location;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    State state;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER)
    List<Request> requests;

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", paid=" + paid +
                ", views=" + views +
                ", createdOn=" + createdOn +
                ", publishedOn=" + publishedOn +
                ", participantLimit=" + participantLimit +
                ", requestModeration=" + requestModeration +
                ", annotation='" + annotation + '\'' +
                ", eventDate=" + eventDate +
                ", initiator=" + initiator +
                ", category=" + category +
                ", location=" + location +
                ", state=" + state +
                '}';
    }
}