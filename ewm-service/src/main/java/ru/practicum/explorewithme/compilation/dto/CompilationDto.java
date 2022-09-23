package ru.practicum.explorewithme.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private boolean pinned;
    List<EventShortDto> events;
}
