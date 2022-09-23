package ru.practicum.explorewithme.user;

import ru.practicum.explorewithme.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    void delete(Long id);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto update(Long id, UserDto userDto);
}
