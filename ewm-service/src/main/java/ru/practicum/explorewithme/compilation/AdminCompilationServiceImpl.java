package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.event.EventMapper;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.dto.EventShortDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        compilation.setEvents(eventRepository.findAllById(newCompilationDto.getEvents()));
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        List<EventShortDto> eventsForCompilation = compilation.getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        eventsForCompilation.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
        compilationDto.setEvents(eventsForCompilation);
        log.info("Creating compilation {}", compilation.getTitle());
        return compilationDto;
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        log.info("Delete compilation, compilationId={}", compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public void deleteEvent(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.getEvents().remove(eventRepository.getReferenceById(eventId));
        compilationRepository.save(compilation);
        log.info("Delete event from compilation, compilationId={}, eventId={}", compId, eventId);
    }

    @Transactional
    @Override
    public void addEvent(Long compId, Long eventId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.getEvents().add(eventRepository.getReferenceById(eventId));
        log.info("Add event to compilation, compilationId={}, eventId={}", compId, eventId);
        compilationRepository.save(compilation);
    }

    @Transactional
    @Override
    public void unpin(Long compId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        log.info("Unpin compilation, compilationId={}", compId);
    }

    @Transactional
    @Override
    public void pin(Long compId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
        log.info("Pin compilation, compilationId={}", compId);
    }
}
