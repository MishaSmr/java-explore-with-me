package ru.practicum.explorewithme.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.AdminUpdateEventRequest;
import ru.practicum.explorewithme.event.dto.EventFullDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private final AdminEventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam List<Long> users,
                                        @RequestParam List<State> states,
                                        @RequestParam List<Long> categories,
                                        @RequestParam(defaultValue = "2022-01-01 00:00:00") String rangeStart,
                                        @RequestParam(defaultValue = "2052-01-01 00:00:00") String rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public EventFullDto editEvent(@PathVariable Long eventId,
                                  @RequestBody AdminUpdateEventRequest updateEventRequest) {
        return eventService.editEvent(eventId, updateEventRequest);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEvent(eventId);
    }
}
