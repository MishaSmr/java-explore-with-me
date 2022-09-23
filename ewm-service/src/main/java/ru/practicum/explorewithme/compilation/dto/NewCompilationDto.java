package ru.practicum.explorewithme.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.event.Event;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class NewCompilationDto {
    @NotNull
    @NotEmpty
    private String title;
    private boolean pinned;
    List<Long> events;
}
