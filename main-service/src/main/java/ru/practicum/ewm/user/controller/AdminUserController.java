package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUser;
import ru.practicum.ewm.user.dto.UserResponse;
import ru.practicum.ewm.user.service.AdminUserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.ewm.utils.Constants.PAGINATION_FROM;
import static ru.practicum.ewm.utils.Constants.PAGINATION_SIZE;

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Validated
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService userService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid NewUser userRequest) {
        return userService.createUser(userRequest);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UserResponse> getUsersByIds(@RequestParam(required = false) List<Long> ids,
                                            @RequestParam(required = false, defaultValue = PAGINATION_FROM) Integer from,
                                            @RequestParam(required = false, defaultValue = PAGINATION_SIZE) Integer size) {
        return userService.getUsersByIds(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NotNull @Positive Long userId) {
        userService.deleteUser(userId);
    }
}
