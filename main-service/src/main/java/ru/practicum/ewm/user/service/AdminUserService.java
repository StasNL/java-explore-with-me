package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUser;
import ru.practicum.ewm.user.dto.UserResponse;
import ru.practicum.ewm.user.model.User;

import java.util.List;


public interface AdminUserService {

    UserResponse createUser(NewUser userRequest);

    List<UserResponse> getUsersByIds(List<Long> userIds, Integer from, Integer size);
    void deleteUser(Long userId);

    User getUserById(Long userId);
}
