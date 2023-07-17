package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.service.PublicCompilationService;
import ru.practicum.ewm.compilation.dto.CompilationResponse;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.utils.Constants.PAGINATION_FROM;
import static ru.practicum.ewm.utils.Constants.PAGINATION_SIZE;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final PublicCompilationService compilationService;

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CompilationResponse getCompilationById(@PathVariable(value = "id") @NotNull Long compId) {
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CompilationResponse> getAllCompilations(
            @RequestParam(required = false, defaultValue = "false") Boolean pinned,
            @RequestParam(required = false, defaultValue = PAGINATION_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = PAGINATION_SIZE) @Positive Integer size) {

        return compilationService.getAllCompilations(pinned, from, size);
    }
}