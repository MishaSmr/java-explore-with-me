package ru.practicum.explorewithme.subscription;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.Category;
import ru.practicum.explorewithme.category.CategoryRepository;
import ru.practicum.explorewithme.client.StatsClient;
import ru.practicum.explorewithme.event.Event;
import ru.practicum.explorewithme.event.EventRepository;
import ru.practicum.explorewithme.event.State;
import ru.practicum.explorewithme.event.dto.EventShortDto;
import ru.practicum.explorewithme.request.ParticipationRequest;
import ru.practicum.explorewithme.request.ParticipationRequestRepository;
import ru.practicum.explorewithme.request.Status;
import ru.practicum.explorewithme.user.User;
import ru.practicum.explorewithme.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SubscriptionServiceImplTest {

    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CategoryRepository categoryRepository;
    @MockBean
    StatsClient statsClient;

    User user1 = new User(null, "First", "1@1.ru");
    User user2 = new User(null, "Second", "2@2.ru");
    User user3 = new User(null, "Third", "3@3.ru");
    Category category = new Category(1L, "cat");
    Event event = new Event(
            1L,
            "title",
            "annotation",
            category,
            "description",
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().minusDays(1),
            user1,
            0,
            true,
            100,
            LocalDateTime.now().minusHours(10),
            false,
            State.PUBLISHED,
            0.0F,
            0.0F,
            Collections.emptyList()
    );
    ParticipationRequest participationRequest = new ParticipationRequest(
            1L,
            LocalDateTime.now().minusHours(1),
            event,
            user3,
            Status.CONFIRMED
    );

    @Test
    void testSubscribe() {
        long userId = userRepository.save(user1).getId();
        long followerId = userRepository.save(user2).getId();
        long subId = subscriptionService.subscribe(followerId, userId).getId();
        Subscription subscription = subscriptionRepository.getReferenceById(subId);
        assertThat(subscription.getUser().getId()).isEqualTo(userId);
        assertThat(subscription.getFollower().getId()).isEqualTo(followerId);
    }

    @Test
    void testGetInitiatorsEventsForFollower() {
        when(statsClient.getViews(Mockito.anyLong()))
                .thenReturn(999L);
        long userId = userRepository.save(user1).getId();
        long followerId = userRepository.save(user2).getId();
        event.setCategory(categoryRepository.save(category));
        eventRepository.save(event);
        subscriptionService.subscribe(followerId, userId);
        List<EventShortDto> events = subscriptionService.getEvents(followerId, "initiators", 0, 10);
        assertThat(events.get(0).getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    void testGetParticipantsEventsForFollower() {
        when(statsClient.getViews(Mockito.anyLong()))
                .thenReturn(999L);
        long userId = userRepository.save(user3).getId();
        long followerId = userRepository.save(user1).getId();
        event.setCategory(categoryRepository.save(category));
        participationRequest.setEvent(eventRepository.save(event));
        participationRequestRepository.save(participationRequest);
        long subId = subscriptionService.subscribe(followerId, userId).getId();
        subscriptionService.approve(userId, subId);
        List<EventShortDto> events = subscriptionService.getEvents(followerId, "participants", 0, 10);
        assertThat(events.get(0).getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    void testGetParticipantsEventsForNotApprovedFollower() {
        long userId = userRepository.save(user1).getId();
        long followerId = userRepository.save(user3).getId();
        event.setCategory(categoryRepository.save(category));
        eventRepository.save(event);
        participationRequest.setEvent(eventRepository.save(event));
        subscriptionService.subscribe(followerId, userId);
        List<EventShortDto> events = subscriptionService.getEvents(followerId, "participants", 0, 10);
        assertThat(events.isEmpty()).isTrue();
    }

}