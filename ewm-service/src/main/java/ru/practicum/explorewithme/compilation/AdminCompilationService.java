package ru.practicum.explorewithme.compilation;

import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;

public interface AdminCompilationService {


    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(Long compId);

    void deleteEvent(Long compId, Long eventId);

    void addEvent(Long compId, Long eventId);

    void pin(Long compId);

    void unpin(Long compId);

}
