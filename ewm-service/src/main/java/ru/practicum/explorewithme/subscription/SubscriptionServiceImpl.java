package ru.practicum.explorewithme.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.EventMapper;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewithme.user.User;
import ru.practicum.explorewithme.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    /**
     * Метод сохраняет в базу данных сведения о подписке одного пользователя на другого
     *
     * @param followerId - ID подписчика
     * @param userId     - ID пользователя, на которого осущетсвляется подписка
     */
    @Transactional
    @Override
    public SubscriptionDto subscribe(Long followerId, Long userId) {
        User user = userRepository.getReferenceById(userId);
        User follower = userRepository.getReferenceById(followerId);
        Subscription subscription = new Subscription(null, user, follower, false);
        log.info("User id={} subscribe to user id={}", followerId, userId);
        return SubscriptionMapper.toSubscriptionDto(subscriptionRepository.save(subscription));
    }

    /**
     * Метод, в котором пользователь одобряет подписку. После этого подписчик сможет видеть события, в которых
     * пользователь принимает участие
     *
     * @param userId - ID пользователя, одобряющего подписку
     * @param subId  - ID подписки
     */
    @Transactional
    @Override
    public void approve(Long userId, Long subId) {
        Subscription subscription = subscriptionRepository.getReferenceById(subId);
        subscription.setApproved(true);
        subscriptionRepository.save(subscription);
    }

    /**
     * Метод, в котором пользователь отменяет одобрение.
     */
    @Transactional
    @Override
    public void cancel(Long userId, Long subId) {
        Subscription subscription = subscriptionRepository.getReferenceById(subId);
        subscription.setApproved(false);
        subscriptionRepository.save(subscription);
    }

    /**
     * Метод получает список событий для подписчика
     * @param type - тип событий, которые надо получить
     *             type=initiators - список актуальных событий, опубликованных пользователями,
     *             на которых подписан другой пользователь. Увидеть можно только опубликованные будущие события.
     *             type=participants - список актуальных событий, в которых пользователи,
     *             на которых подписан другой пользователь, принимают участие.
     *             Увидеть можно только опубликованные будущие события и только если подписка одобрена
     */
    @Override
    public List<EventShortDto> getEvents(Long followerId, String type, Integer from, Integer size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.DESC, "eventDate");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<EventShortDto> result;
        if (type.equals("initiators")) {
            result = eventRepository.findEventsByInitiatorForFollower(followerId, pageable)
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        } else {
            result = eventRepository.findEventsByParticipantForFollower(followerId, pageable)
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }
        result.forEach(e -> e.setViews(statsClient.getViews(e.getId())));
        log.info("Get {} events for follower, userId={}", type, followerId);
        return result;
    }
}
