package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.model.Hit;
import ru.practicum.model.HitMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start.isAfter(end))
            throw new BadRequestException("Неверно указаны даты начала и конца отрезка для поиска");

        if (unique) {
            if (uris == null || uris.isEmpty())
                return repository.getStatsBetweenStartAndEndTimeUnique(start, end);
            else {
                return repository.getStatsBetweenStartAndEndTimeByUriUnique(start, end, uris);
            }
        } else {
            if (uris == null || uris.isEmpty())
                return repository.getStatsBetweenStartAndEndTime(start, end);
            else {
                return repository.getStatsBetweenStartAndEndTimeByUri(start, end, uris);
            }
        }
    }

    @Transactional
    @Override
    public Hit saveStats(HitDto hitDto) {
        Hit hit = HitMapper.hitDtoToHit(hitDto);
        hit = repository.save(hit);
        log.info("Статистика сохранена");
        return hit;
    }
}