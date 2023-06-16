package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.BadDBRequestException;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.user.dto.NewUser;
import ru.practicum.ewm.user.dto.UserMapper;
import ru.practicum.ewm.user.dto.UserResponse;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.ExceptionMessages.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(NewUser newUser) {
        Optional<User> wrongUser = userRepository.findByEmail(newUser.getEmail());
        if (wrongUser.isPresent())
                throw new BadRequestException(WRONG_EMAIL);

        User user = UserMapper.newUserToUser(newUser);

        user = userRepository.save(user);

        return UserMapper.userToUserResponse(user);
    }

    @Override
    public List<UserResponse> getUsersByIds(List<Long> userIds, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<UserResponse> users = userRepository.findAllByUserIdIn(userIds, pageable).stream()
                .map(UserMapper::userToUserResponse)
                .collect(Collectors.toList());

        if (users.size() < userIds.size())
            throw new BadRequestException(USER_WRONG_LIST);
        return users;
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> wrongUser = userRepository.findById(userId);
        if (wrongUser.isEmpty())
            throw new BadDBRequestException(USER_NO_ID);
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadDBRequestException("user.no.id"));
    }
}
