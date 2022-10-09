package ru.practicum.explorewithme.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.EventMapper;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/subs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public SubscriptionDto subscribe(@PathVariable("userId") long followerId,
                                     @RequestBody SubscriptionDto subscriptionDto) {
        return SubscriptionMapper.toSubscriptionDto(subscriptionService.subscribe(followerId, subscriptionDto));
    }

    @PutMapping("/{subId}/approve")
    public void approve(@PathVariable long userId,
                        @PathVariable long subId) {
        subscriptionService.approve(userId, subId);
    }

    @PutMapping("/{subId}/cancel")
    public void cancel(@PathVariable long userId,
                       @PathVariable long subId) {
        subscriptionService.cancel(userId, subId);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable("userId") long followerId,
                                                   @RequestParam Type type,
                                                   @RequestParam(defaultValue = "0") Integer pageFrom,
                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return subscriptionService.getEvents(followerId, type, pageFrom, pageSize)
                .entrySet()
                .stream()
                .map(EventMapper :: toEventShortDto)
                .collect(Collectors.toList());
    }
}
