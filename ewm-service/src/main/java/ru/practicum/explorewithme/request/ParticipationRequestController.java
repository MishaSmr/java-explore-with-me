package ru.practicum.explorewithme.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class ParticipationRequestController {

    private final ParticipationRequestService participationRequestService;

    @PostMapping
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        return participationRequestService.create(userId, eventId);
    }

    @GetMapping
    public List<ParticipationRequestDto> get(@PathVariable Long userId) {
        return participationRequestService.get(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        return participationRequestService.cancel(userId, requestId);
    }
}
