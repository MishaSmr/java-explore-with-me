package ru.practicum.explorewithme.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionDto {
    Long id;
    private Long userId;
    private Long followerId;
    private boolean approved;
}
