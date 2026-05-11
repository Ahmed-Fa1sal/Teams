package com.teams.teams.service.impl;

import com.teams.teams.domain.Role;
import com.teams.teams.domain.User;
import com.teams.teams.dto.AuthResponse;
import com.teams.teams.dto.LoginRequest;
import com.teams.teams.dto.RegisterRequest;
import com.teams.teams.exception.BadRequestException;
import com.teams.teams.repository.RoleRepository;
import com.teams.teams.repository.UserRepository;
import com.teams.teams.security.JwtTokenProvider;
import com.teams.teams.service.AuthService;
import com.teams.teams.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final long jwtExpirationMs;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         RoleRepository roleRepository,
                         PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider,
                         UserService userService,
                         @Value("${app.jwt.expiration}") long jwtExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Override
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .active(true)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Assign default STANDARD_USER role
        Set<Role> roles = new HashSet<>();
        Role standardUserRole = roleRepository.findByName(Role.RoleType.STANDARD_USER.value)
                .orElseThrow(() -> new BadRequestException("Default role not found"));
        roles.add(standardUserRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new BadRequestException("Invalid username or password"));

            if (!user.isEnabled()) {
                throw new BadRequestException("User account is disabled");
            }

            if (!user.isAccountNonLocked()) {
                throw new BadRequestException("User account is locked due to too many failed login attempts");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            user.setLastLoginAt(LocalDateTime.now());
            user.resetFailedLoginAttempts();
            userRepository.save(user);

            String accessToken = jwtTokenProvider.generateAccessToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpirationMs / 1000)
                    .user(userService.toUserDto(user))
                    .build();
        } catch (Exception ex) {
            User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            if (user != null) {
                user.incrementFailedLoginAttempts();
                userRepository.save(user);
            }
            throw new BadRequestException("Invalid username or password");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .build();
    }

    @Override
    public void logout(String username) {
        // In a stateless JWT system, logout is typically handled on the client side
        // by removing the token. This is a placeholder for any server-side cleanup if needed.
    }
}

