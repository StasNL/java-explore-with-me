package ru.practicum.ewm.compilation.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationMapper {

    public static CompilationResponse compilationToCompilationResponse(Compilation compilation) {

        return CompilationResponse.builder()
                .id(compilation.getCompId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation newCompilationToCompilation(NewCompilation newCompilation, List<Event> events) {
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .events(events)
                .pinned(newCompilation.getPinned())
                .build();
    }
}
