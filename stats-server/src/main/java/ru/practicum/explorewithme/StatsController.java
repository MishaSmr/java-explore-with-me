package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public EndpointHitDto create(@RequestBody EndpointHitDto endpointHitDto) {
        return statsService.create(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> get(@RequestParam(defaultValue = "2022-01-01 00:00:00") String start,
                               @RequestParam(defaultValue = "2042-01-01 00:00:00") String end,
                               @RequestParam List<String> uris,
                               @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.get(start, end, uris, unique);
    }
}
