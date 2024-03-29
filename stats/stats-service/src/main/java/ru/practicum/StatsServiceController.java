package ru.practicum;

import org.springframework.http.HttpStatus;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Hit;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class StatsServiceController {

    private final StatsService service;

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam(name = "start")
                                   String start,
                                   @RequestParam(name = "end")
                                   String end,
                                   @RequestParam(name = "uris", required = false)
                                   List<String> uris,
                                   @RequestParam(name = "unique", required = false, defaultValue = "false")
                                   Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startLDT = LocalDateTime.parse(start, formatter);
        LocalDateTime endLDT = LocalDateTime.parse(end, formatter);

        return service.getStats(startLDT, endLDT, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Hit saveStats(@RequestBody @Valid HitDto hitDto) {
        return service.saveStats(hitDto);
    }
}