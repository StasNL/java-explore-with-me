package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loc_id")
    private Long locId;
    @Column(name = "latitude")
    private Float lat;
    @Column(name = "longitude")
    private Float lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(locId, location.locId)
                && Objects.equals(lat, location.lat)
                && Objects.equals(lon, location.lon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locId, lat, lon);
    }

    @Override
    public String toString() {
        return "Location{" +
                "locId=" + locId +
                ", latitude=" + lat +
                ", longitude=" + lon +
                '}';
    }
}
