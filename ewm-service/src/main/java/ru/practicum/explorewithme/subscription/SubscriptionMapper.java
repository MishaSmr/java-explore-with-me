package ru.practicum.explorewithme.subscription;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubscriptionMapper {

    public static SubscriptionDto toSubscriptionDto(Subscription subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getFollower().getId(),
                subscription.getStatus()
        );
    }
}
