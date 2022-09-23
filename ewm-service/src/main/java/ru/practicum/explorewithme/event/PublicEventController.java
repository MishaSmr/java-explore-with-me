package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.EventFullDto;
import ru.practicum.explorewithme.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {

    private final PublicEventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam String text,
                                         @RequestParam List<Long> categories,
                                         @RequestParam Boolean paid,
                                         @RequestParam String rangeStart,
                                         @RequestParam(defaultValue = "2052-01-01 00:00:00") String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam SortEnum sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        return eventService.getEvents(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                ip,
                path);
    }

    @GetMapping("/{id}")
    public EventFullDto get(@PathVariable Long id,
                            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        return eventService.get(id, ip, path);
    }
}
