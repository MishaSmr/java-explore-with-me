package ru.practicum.explorewithme.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.Event;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewithme.user.User;
import ru.practicum.explorewithme.user.UserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    /**
     * Метод сохраняет в базу данных сведения о подписке одного пользователя на другого
     *
     * @param followerId      - ID подписчика
     * @param subscriptionDto - Тело запроса, в котором содержится ID пользователя,
     *                        на которого осуществляется подписка
     */
    @Transactional
    public Subscription subscribe(long followerId, SubscriptionDto subscriptionDto) {
        User user = userRepository.getReferenceById(subscriptionDto.getUserId());
        User follower = userRepository.getReferenceById(followerId);
        Subscription subscription = new Subscription(null, user, follower, ModerationStatus.WAITING);
        log.info("User id={} subscribe to user id={}", followerId, user.getId());
        return subscriptionRepository.save(subscription);
    }

    /**
     * Метод, в котором пользователь одобряет подписку. После этого подписчик сможет видеть события, в которых
     * пользователь принимает участие
     *
     * @param userId - ID пользователя, одобряющего подписку
     * @param subId  - ID подписки
     */
    @Transactional
    public void approve(long userId, long subId) {
        Subscription subscription = subscriptionRepository.getReferenceById(subId);
        subscription.setStatus(ModerationStatus.APPROVED);
        subscriptionRepository.save(subscription);
    }

    /**
     * Метод, в котором пользователь отменяет одобрение.
     */
    @Transactional
    public void cancel(long userId, long subId) {
        Subscription subscription = subscriptionRepository.getReferenceById(subId);
        subscription.setStatus(ModerationStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    /**
     * Метод получает список событий для подписчика
     *
     * @param type - тип событий, которые надо получить
     *             type=INITIATOR - список актуальных событий, опубликованных пользователями,
     *             на которых подписан другой пользователь. Увидеть можно только опубликованные события.
     *             type=PARTICIPANT - список актуальных событий, в которых пользователи,
     *             на которых подписан другой пользователь, принимают участие.
     *             Увидеть можно только опубликованные события и только если подписка одобрена
     */

    public Map<Event, Long> getEvents(long followerId, Type type, int pageFrom, int pageSize) {
        int page = pageFrom / pageSize;
        Sort sort = Sort.by(Sort.Direction.DESC, "eventDate");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<Event> events;
        if (type == Type.INITIATOR) {
            events = eventRepository.findEventsByInitiatorForFollower(followerId, pageable);
        } else {
            events = eventRepository.findEventsByParticipantForFollower(followerId, pageable);
        }
        if (events.isEmpty()) return Collections.emptyMap();
        List<Long> views = statsClient.getViewsForList(events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList()));
        Map<Event, Long> result = new HashMap<>();
        for (int i = 0; i <= views.size() - 1; i++) {
            result.put(events.getContent().get(i), views.get(i));
        }
        log.info("Get {} events for follower, userId={}", type, followerId);
        return result;
    }
}
