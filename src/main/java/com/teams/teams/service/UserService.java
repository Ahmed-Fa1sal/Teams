package com.teams.teams.service;

import com.teams.teams.domain.User;
import com.teams.teams.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    UserDto createUser(User user);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    UserDto getUserByUsername(String username);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    Page<UserDto> getAllUsers(Pageable pageable);
    Page<UserDto> searchUsers(String query, Pageable pageable);
    void activateUser(Long id);
    void deactivateUser(Long id);
    Optional<User> findByUsername(String username);
    UserDto toUserDto(User user);
}

