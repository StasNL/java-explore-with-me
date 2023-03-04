package ru.practicum.model;

import dto.HitDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HitMapper {
    public static Hit hitDtoToHit(HitDto hitDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(hitDto.getTimestamp(), formatter);
        return Hit.builder()
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .uri(hitDto.getUri())
                .timestamp(ldt)
                .build();
    }
}