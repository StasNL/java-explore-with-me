package ru.practicum.repository;

import dto.StatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new dto.StatsDto (h.uri, h.app, COUNT(DISTINCT h.ip)) FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTimeUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new dto.StatsDto(h.uri, h.app, COUNT(distinct h.ip)) FROM Hit AS h " +
            "WHERE h.uri IN :uris AND (h.timestamp BETWEEN :start AND :end) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTimeByUriUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);

    @Query("SELECT new dto.StatsDto(h.uri, h.app, COUNT(h.ip)) FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTime(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new dto.StatsDto(h.uri, h.app, COUNT(h.ip)) FROM Hit AS h " +
            "WHERE h.uri in :uris AND (h.timestamp BETWEEN :start AND :end) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTimeByUri(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris);
}