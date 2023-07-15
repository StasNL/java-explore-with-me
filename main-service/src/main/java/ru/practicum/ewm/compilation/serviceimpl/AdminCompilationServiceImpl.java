package ru.practicum.ewm.compilation.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.CompilationRequest;
import ru.practicum.ewm.compilation.dto.CompilationResponse;
import ru.practicum.ewm.compilation.dto.NewCompilation;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.reporitories.CompilationRepository;
import ru.practicum.ewm.compilation.service.AdminCompilationService;
import ru.practicum.ewm.compilation.service.PublicCompilationService;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.ewm.utils.ExceptionMessages.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final PublicCompilationService compilationService;

    @Override
    public CompilationResponse createCompilation(NewCompilation newCompilation) {
        checkCompilationTitle(newCompilation.getTitle(), 0L);

        List<Event> events = new ArrayList<>();
        if (newCompilation.getEvents() != null && !newCompilation.getEvents().isEmpty())
                events = checkEventsById(newCompilation.getEvents());
        if (newCompilation.getPinned() == null)
            newCompilation.setPinned(false);

        Compilation compilation = CompilationMapper.newCompilationToCompilation(newCompilation, events);


        compilation = compilationRepository.save(compilation);

        return CompilationMapper.compilationToCompilationResponse(compilation);
    }

    @Override
    public CompilationResponse editCompilation(Long compId, CompilationRequest compilationRequest) {
        Compilation compilation = checkCompilationId(compId);

        String title = compilationRequest.getTitle();
        if (title != null) {
            checkCompilationTitle(title, compId);
            compilation.setTitle(title);
        }

        if (compilationRequest.getPinned() != null)
            compilation.setPinned(compilationRequest.getPinned());

        if (compilationRequest.getEvents() != null && !compilationRequest.getEvents().isEmpty()) {
            List<Event> events = checkEventsById(compilationRequest.getEvents());
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.compilationToCompilationResponse(compilation);
    }

    @Override
    public CompilationResponse getCompilationById(Long compId) {
        return compilationService.getCompilationById(compId);
    }

    @Override
    public void deleteCompilation(Long compId) {
        checkCompilationId(compId);
        compilationRepository.deleteById(compId);
    }

    private void checkCompilationTitle(String title, long compId) {
        Optional<Compilation> categoryOpt = compilationRepository.findByTitle(title);
        if (categoryOpt.isPresent() && categoryOpt.get().getCompId() != compId)
            throw new BadRequestException(COMPILATION_WRONG_TITLE);
    }

    private Compilation checkCompilationId(long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new BadDBRequestException(COMPILATION_NO_ID));
    }

    private List<Event> checkEventsById(List<Long> eventIds) {
        List<Event> events = eventRepository.findAllById(eventIds);

        if (events.size() != eventIds.size())
            throw new BadDBRequestException(COMPILATION_WRONG_EVENT_LIST);

        return events;
    }
}