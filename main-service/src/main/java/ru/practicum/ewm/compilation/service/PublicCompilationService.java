package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationResponse;

import java.util.List;

public interface PublicCompilationService {

    CompilationResponse getCompilationById(Long compId);

    List<CompilationResponse> getAllCompilations(Boolean pinned, Integer from, Integer size);
}
