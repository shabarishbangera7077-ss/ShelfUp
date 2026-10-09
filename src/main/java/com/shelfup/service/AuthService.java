package com.shelfup.service;

import com.shelfup.dto.AuthResponse;
import com.shelfup.dto.LoginRequest;
import com.shelfup.dto.RegisterRequest;
import com.shelfup.dto.UserResponse;
import com.shelfup.entity.Role;
import com.shelfup.entity.User;
import com.shelfup.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setCourse(request.course());
        user.setSemester(request.semester());
        user.setRole(request.role() != null && request.role().equalsIgnoreCase("ADMIN") ? Role.ADMIN : Role.STUDENT);
        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(), user.getCourse(), user.getSemester(), user.isBlocked());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (user.isBlocked()) {
            throw new SecurityException("User is blocked");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getRole().name(), user.getEmail());
    }
}
