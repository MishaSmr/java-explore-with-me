package ru.practicum.explorewithme.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/subs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{userToSubId}")
    public SubscriptionDto subscribe(@PathVariable("userId") Long followerId,
                                     @PathVariable Long userToSubId) {
        return subscriptionService.subscribe(followerId, userToSubId);
    }

    @PutMapping("/{subId}/approve")
    public void approve(@PathVariable Long userId,
                        @PathVariable Long subId) {
        subscriptionService.approve(userId, subId);
    }

    @PutMapping("/{subId}/cancel")
    public void cancel(@PathVariable Long userId,
                       @PathVariable Long subId) {
        subscriptionService.cancel(userId, subId);
    }

    @GetMapping
    public List<EventShortDto> getInitiatorsEvents(@PathVariable("userId") Long followerId,
                                                   @RequestParam String type,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return subscriptionService.getEvents(followerId, type, from, size);
    }
}
