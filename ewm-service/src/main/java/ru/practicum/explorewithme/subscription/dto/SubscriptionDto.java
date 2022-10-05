package ru.practicum.explorewithme.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.subscription.ModerationStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {
    private Long id;
    private Long userId;
    private Long followerId;
    private ModerationStatus status;
}
