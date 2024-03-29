package ru.practicum.ewm.request.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "req_id")
    private Long reqId;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User requester;

    @Column(name = "creation_date")
    private LocalDateTime created;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Override
    public String toString() {
        return "Request{" +
                "reqId=" + reqId +
                ", requester=" + requester +
                ", created=" + created +
                ", status=" + status +
                ", event=" + event +
                '}';
    }
}
