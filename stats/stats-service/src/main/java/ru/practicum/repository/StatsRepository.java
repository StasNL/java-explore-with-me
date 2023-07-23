package ru.practicum.repository;

import ru.practicum.dto.StatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT new ru.practicum.dto.StatsDto (h.uri, h.app, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.uri, h.app " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTimeUnique(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatsDto (h.uri, h.app, COUNT(DISTINCT h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.uri IN (?3) AND (h.timestamp BETWEEN ?1 AND ?2) " +
            "GROUP BY h.uri, h.app " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTimeByUriUnique(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatsDto (h.uri, h.app, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.uri, h.app " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTime(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatsDto (h.uri, h.app, COUNT(h.ip)) " +
            "FROM Hit AS h " +
            "WHERE h.uri IN (?3) AND h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.uri, h.app " +
            "ORDER BY COUNT(h.ip) DESC ")
    List<StatsDto> getStatsBetweenStartAndEndTimeByUri(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uri);
}