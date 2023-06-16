package ru.practicum.ewm.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static User newUserToUser(NewUser newUser) {
        return User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();
    }

    public static UserResponse userToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
