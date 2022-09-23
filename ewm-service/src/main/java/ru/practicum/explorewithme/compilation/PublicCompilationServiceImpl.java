package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.event.EventMapper;
import ru.practicum.explorewithme.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicCompilationServiceImpl implements PublicCompilationService {

    private final CompilationRepository compilationRepository;

    private final StatsClient statsClient;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Compilation> compilations = compilationRepository.findByPin(pinned, pageable);
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation c : compilations) {
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(c);
            List<EventShortDto> eventsForCompilation = c.getEvents()
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
            eventsForCompilation.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
            compilationDto.setEvents(eventsForCompilation);
            result.add(compilationDto);
        }
        log.info("Get compilations");
        return result;
    }

    @Override
    public CompilationDto get(Long compId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        List<EventShortDto> eventsForCompilation = compilation.getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        eventsForCompilation.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
        compilationDto.setEvents(eventsForCompilation);
        log.info("Get compilation, compId={}", compId);
        return compilationDto;
    }
}
