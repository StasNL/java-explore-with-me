package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationRequest;
import ru.practicum.ewm.compilation.dto.CompilationResponse;
import ru.practicum.ewm.compilation.dto.NewCompilation;

public interface AdminCompilationService {

    CompilationResponse createCompilation(NewCompilation newCompilation);

    CompilationResponse editCompilation(Long compId, CompilationRequest compilationRequest);

    CompilationResponse getCompilationById(Long compId);

    void deleteCompilation(Long compId);
}
