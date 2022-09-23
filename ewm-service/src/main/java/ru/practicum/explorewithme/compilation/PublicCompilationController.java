package ru.practicum.explorewithme.compilation;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.compilation.dto.CompilationDto;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private final PublicCompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam Boolean pinned,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable Long compId) {
        return compilationService.get(compId);
    }
}
