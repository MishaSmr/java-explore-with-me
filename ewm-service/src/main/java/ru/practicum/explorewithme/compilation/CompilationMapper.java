package ru.practicum.explorewithme.compilation;

import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;
import ru.practicum.explorewithme.event.EventMapper;

import java.util.Collections;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                null,
                newCompilationDto.getTitle(),
                newCompilationDto.isPinned(),
                Collections.emptyList()
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                Collections.emptyList()
        );
    }

}
