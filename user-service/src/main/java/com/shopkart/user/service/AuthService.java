package com.shopkart.user.service;

import com.shopkart.user.dto.AuthResponse;
import com.shopkart.user.dto.LoginRequest;
import com.shopkart.user.dto.RegisterRequest;
import com.shopkart.user.entity.Role;
import com.shopkart.user.entity.User;
import com.shopkart.user.exception.EmailAlreadyExistsException;
import com.shopkart.user.repository.UserRepository;
import com.shopkart.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .build();

        User saved = userRepository.save(user);

        String token = jwtService.generateToken(saved, Map.of(
                "userId", saved.getId(),
                "role", saved.getRole().name()
        ));

        return AuthResponse.of(token, saved.getId(), saved.getEmail(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        // Delegates credential checking to Spring Security's AuthenticationManager,
        // which uses CustomUserDetailsService + PasswordEncoder under the hood.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("User vanished after successful authentication"));

        String token = jwtService.generateToken(user, Map.of(
                "userId", user.getId(),
                "role", user.getRole().name()
        ));

        return AuthResponse.of(token, user.getId(), user.getEmail(), user.getRole().name());
    }
}
