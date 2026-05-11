package com.teams.teams.service;

import com.teams.teams.domain.User;
import com.teams.teams.dto.AuthResponse;
import com.teams.teams.dto.LoginRequest;
import com.teams.teams.dto.RegisterRequest;

public interface AuthService {
    User register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse refreshToken(String refreshToken);
    void logout(String username);
}

