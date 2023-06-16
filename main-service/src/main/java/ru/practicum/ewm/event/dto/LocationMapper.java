package ru.practicum.ewm.event.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {

    public static Location newLocationToLocation(NewLocation newLocation) {
        return Location.builder()
                .lon(newLocation.getLon())
                .lat(newLocation.getLat())
                .build();
    }
}
