package ru.practicum.explorewithme.subscription;

import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;

public class SubscriptionMapper {

    public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getFollower().getId(),
                subscription.getApproved()
        );
    }
}
