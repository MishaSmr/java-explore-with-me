package ru.practicum.explorewithme.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explorewithme.user.User;

import javax.persistence.*;

@Entity
@Table(name = "user_follower")
@Data
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;
    private Boolean approved;
    public Subscription() {

    }
}
