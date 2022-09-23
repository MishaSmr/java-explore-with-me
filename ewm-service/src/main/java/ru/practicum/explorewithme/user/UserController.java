package ru.practicum.explorewithme.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return userService.create(userDto);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        return userService.update(id, userDto);
    }
}
