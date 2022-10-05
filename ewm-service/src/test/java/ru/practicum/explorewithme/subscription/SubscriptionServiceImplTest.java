package ru.practicum.explorewithme.subscription;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.explorewithme.request.ParticipationRequest;
import ru.practicum.explorewithme.request.ParticipationRequestRepository;
import ru.practicum.explorewithme.request.Status;
import ru.practicum.explorewithme.subscription.dto.SubscriptionDto;
import ru.practicum.explorewithme.user.User;
import ru.practicum.explorewithme.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    private static final User user1 = new User(null, "First", "1@1.ru");
    private static final User user2 = new User(null, "Second", "2@2.ru");
    private static final User user3 = new User(null, "Third", "3@3.ru");
    private static final Category category = new Category(1L, "cat");
    private static final Event event = new Event(
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
    private static final ParticipationRequest participationRequest = new ParticipationRequest(
            1L,
            LocalDateTime.now().minusHours(1),
            event,
            user3,
            Status.CONFIRMED
    );
    private static final SubscriptionDto subscriptionDto1 = new SubscriptionDto();
    private static final SubscriptionDto subscriptionDto2 = new SubscriptionDto();

    @BeforeEach
    public void beforeEach() {
        user1.setId(userRepository.save(user1).getId());
        user2.setId(userRepository.save(user2).getId());
        user3.setId(userRepository.save(user3).getId());
        subscriptionDto1.setFollowerId(user2.getId());
        subscriptionDto2.setFollowerId(user2.getId());
        subscriptionDto1.setUserId(user1.getId());
        subscriptionDto2.setUserId(user3.getId());
    }

    @Test
    void testSubscribe() {
        long subId = subscriptionService.subscribe(user2.getId(), subscriptionDto1).getId();
        Subscription subscription = subscriptionRepository.getReferenceById(subId);
        assertThat(subscription.getUser().getId()).isEqualTo(user1.getId());
        assertThat(subscription.getFollower().getId()).isEqualTo(user2.getId());
    }

    @Test
    void testGetInitiatorsEventsForFollower() {
        when(statsClient.getViewsForList(Mockito.anyList()))
                .thenReturn(List.of(999L));
        event.setCategory(categoryRepository.save(category));
        eventRepository.save(event);
        subscriptionService.subscribe(user2.getId(), subscriptionDto1);
        Map<Event, Long> events = subscriptionService.getEvents(user2.getId(), Type.INITIATOR, 0, 10);
        assertThat(events.keySet().stream().findFirst().get().getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    void testGetParticipantsEventsForFollower() {
        when(statsClient.getViewsForList(Mockito.anyList()))
                .thenReturn(List.of(999L));
        event.setCategory(categoryRepository.save(category));
        participationRequest.setEvent(eventRepository.save(event));
        participationRequestRepository.save(participationRequest);
        long subId = subscriptionService.subscribe(user2.getId(), subscriptionDto2).getId();
        subscriptionService.approve(user1.getId(), subId);
        Map<Event, Long> events = subscriptionService.getEvents(user2.getId(), Type.PARTICIPANT, 0, 10);
        assertThat(events.keySet().stream().findFirst().get().getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    void testGetParticipantsEventsForNotApprovedFollower() {
        event.setCategory(categoryRepository.save(category));
        eventRepository.save(event);
        participationRequest.setEvent(eventRepository.save(event));
        subscriptionService.subscribe(user2.getId(), subscriptionDto2);
        Map<Event, Long> events = subscriptionService.getEvents(user2.getId(), Type.PARTICIPANT, 0, 10);
        assertThat(events.isEmpty()).isTrue();
    }
}