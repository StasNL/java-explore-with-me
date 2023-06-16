package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationRequest;
import ru.practicum.ewm.compilation.dto.CompilationResponse;
import ru.practicum.ewm.compilation.dto.NewCompilation;
import ru.practicum.ewm.compilation.service.AdminCompilationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final AdminCompilationService compilationService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationResponse createCompilation(@RequestBody @Valid NewCompilation newCompilation) {

        CompilationResponse compilation = compilationService.createCompilation(newCompilation);
        return compilationService.getCompilationById(compilation.getId());
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CompilationResponse editCompilation(@PathVariable @NotNull @Positive Long compId,
                                               @RequestBody @Valid CompilationRequest compilationRequest) {

        CompilationResponse compilation = compilationService.editCompilation(compId, compilationRequest);
        return compilationService.getCompilationById(compilation.getId());
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @NotNull @Positive Long compId) {

        compilationService.deleteCompilation(compId);
    }
}