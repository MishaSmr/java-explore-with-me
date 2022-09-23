package ru.practicum.explorewithme.compilation;

import ru.practicum.explorewithme.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto get(Long compId);
}
