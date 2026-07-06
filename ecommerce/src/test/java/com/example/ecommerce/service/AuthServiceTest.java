package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateUserRequest;
import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.response.AuthResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.UserReposistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserReposistory userReposistory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User user;
    private CreateUserRequest createRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");

        createRequest = new CreateUserRequest("John Doe", "john@example.com", "password123", null);
        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Register - success with new email")
    void register_success() {
        when(userReposistory.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userReposistory.save(any(User.class))).thenReturn(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com").password("encodedPassword").roles("USER").build();
        when(customUserDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse response = authService.register(createRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("1", response.getUserId());
        verify(userReposistory).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("Register - throws BadRequestException for duplicate email")
    void register_duplicateEmail_throwsBadRequest() {
        when(userReposistory.existsByEmail("john@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> authService.register(createRequest));

        assertEquals("Email already exists", exception.getMessage());
        verify(userReposistory, never()).save(any());
    }

    @Test
    @DisplayName("Login - success")
    void login_success() {
        when(userReposistory.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com").password("encodedPassword").roles("USER").build();
        when(customUserDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("1", response.getUserId());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Login - throws ResourceNotFoundException for non-existent user")
    void login_userNotFound_throwsResourceNotFound() {
        when(userReposistory.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        LoginRequest badRequest = new LoginRequest();
        badRequest.setEmail("unknown@example.com");
        badRequest.setPassword("password123");

        assertThrows(ResourceNotFoundException.class, () -> authService.login(badRequest));
    }

    @Test
    @DisplayName("Register - sets role to USER by default")
    void register_setsDefaultRole() {
        when(userReposistory.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userReposistory.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            assertEquals("USER", saved.getRole());
            return saved;
        });

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com").password("encoded").roles("USER").build();
        when(customUserDetailsService.loadUserByUsername("john@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token");

        authService.register(createRequest);
        verify(userReposistory).save(any(User.class));
    }
}
