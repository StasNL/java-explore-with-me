package ru.practicum.repository;

import dto.StatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("select new dto.StatsDto (h.uri, h.app, count(distinct h.ip)) from Hit as h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc ")
    List<StatsDto> getStatsBetweenStartAndEndTimeUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("select new dto.StatsDto(h.uri, h.app, count(distinct h.ip)) from Hit as h " +
            "where h.uri in :uris and (h.timestamp between :start and :end) " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc ")
    List<StatsDto> getStatsBetweenStartAndEndTimeByUriUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query("select new dto.StatsDto(h.uri, h.app, count(h.ip)) from Hit as h " +
            "where h.timestamp between :start and :end " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc ")
    List<StatsDto> getStatsBetweenStartAndEndTime(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("select new dto.StatsDto(h.uri, h.app, count(h.ip)) from Hit as h " +
            "where h.uri in :uris and (h.timestamp between :start and :end) " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc ")
    List<StatsDto> getStatsBetweenStartAndEndTimeByUri(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}