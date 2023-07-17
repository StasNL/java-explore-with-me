package ru.practicum.ewm.event.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
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
    public String toString() {
        return "Location{" +
                "locId=" + locId +
                ", latitude=" + lat +
                ", longitude=" + lon +
                '}';
    }
}
