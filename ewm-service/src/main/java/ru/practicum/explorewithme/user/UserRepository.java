package ru.practicum.explorewithme.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;


public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByIdIn(Collection<Long> id, Pageable pageable);
}