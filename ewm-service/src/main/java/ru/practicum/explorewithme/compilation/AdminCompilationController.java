package ru.practicum.explorewithme.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.compilation.dto.NewCompilationDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {

    private final AdminCompilationService compilationService;

    @PostMapping
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationService.create(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEvent(@PathVariable Long compId,
                            @PathVariable Long eventId) {
        compilationService.deleteEvent(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEvent(@PathVariable Long compId,
                         @PathVariable Long eventId) {
        compilationService.addEvent(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void unpin(@PathVariable Long compId) {
        compilationService.unpin(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pin(@PathVariable Long compId) {
        compilationService.pin(compId);
    }
}
