package ru.practicum.explorewithme.subscription;

import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;

import java.util.List;


public interface SubscriptionService {

    SubscriptionDto subscribe(Long followerId, Long userId);

    void approve(Long userId, Long subId);

    void cancel(Long userId, Long subId);

    List<EventShortDto> getEvents(Long followerId, String type, Integer from, Integer size);

}
