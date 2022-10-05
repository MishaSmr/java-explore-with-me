package ru.practicum.explorewithme.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.category.Category;
import ru.practicum.explorewithme.compilation.Compilation;
import ru.practicum.explorewithme.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String description;
    @Column(name = "date_event")
    private LocalDateTime eventDate;
    @Column(name = "created")
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "confirmed_requests")
    private int confirmedRequests;
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "published")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(name = "location_lat")
    private float locationLat;
    @Column(name = "location_lon")
    private float locationLon;
    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;

    public Event() {

    }
}
