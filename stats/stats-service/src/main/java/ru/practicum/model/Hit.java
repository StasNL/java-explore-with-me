package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "hits")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app")
    private String app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @Column(name = "created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hit hit = (Hit) o;
        return Objects.equals(id, hit.id)
                && Objects.equals(app, hit.app)
                && Objects.equals(uri, hit.uri)
                && Objects.equals(ip, hit.ip)
                && Objects.equals(timestamp, hit.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, app, uri, ip, timestamp);
    }

    @Override
    public String toString() {
        return "Hit{" +
                "id=" + id +
                ", app='" + app + '\'' +
                ", uri='" + uri + '\'' +
                ", ip='" + ip + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}