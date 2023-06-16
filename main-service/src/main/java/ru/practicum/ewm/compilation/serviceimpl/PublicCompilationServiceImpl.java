package ru.practicum.ewm.compilation.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.CompilationResponse;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.reporitories.CompilationRepository;
import ru.practicum.ewm.compilation.service.PublicCompilationService;
import ru.practicum.ewm.event.dao.EventDao;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.ShortEventResponse;
import ru.practicum.ewm.event.model.ShortEvent;
import ru.practicum.ewm.exceptions.BadDBRequestException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.ExceptionMessages.COMPILATION_NO_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventDao eventDao;

    @Override
    public CompilationResponse getCompilationById(Long compId) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new BadDBRequestException(COMPILATION_NO_ID));

        List<ShortEvent> events = eventDao.getAllEventsByCompilationId(compId);
        List<ShortEventResponse> eventResponses = EventMapper.shortEventToShortEventResponse(events);

        CompilationResponse compilationResponse = CompilationMapper.compilationToCompilationResponse(compilation);
        compilationResponse.setEvents(eventResponses);

        return compilationResponse;
    }

    @Override
    public List<CompilationResponse> getAllCompilations(Boolean pinned, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations;
        if (pinned != null)
            compilations = compilationRepository.findAll(pageable).toList();
        else
            compilations = compilationRepository.findAllByPinned(pinned, pageable).toList();

        List<Long> compilationIds = compilations.stream()
                .map(Compilation::getCompId)
                .collect(Collectors.toList());

        List<CompilationResponse> compilationResponses = compilations.stream()
                .map(CompilationMapper::compilationToCompilationResponse)
                .collect(Collectors.toList());

        Map<Long, List<ShortEvent>> shortEventMap = eventDao.getAllEventsByCompilations(compilationIds);

        for (CompilationResponse comp : compilationResponses) {
            List<ShortEventResponse> shortEvents = shortEventMap.get(comp.getId()).stream()
                    .map(EventMapper::shortEventToShortEventResponse)
                    .collect(Collectors.toList());
            comp.setEvents(shortEvents);
        }
        return compilationResponses;
    }
}