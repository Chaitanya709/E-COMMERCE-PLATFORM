package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.UpdateUserRequest;
import com.example.ecommerce.dto.response.UserResponse;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserReposistory userReposistory;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole("USER");
    }

    @Test
    @DisplayName("getById - success")
    void getById_success() {
        when(userReposistory.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(1L);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    @DisplayName("getById - throws ResourceNotFoundException when user not found")
    void getById_userNotFound_throwsException() {
        when(userReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    @DisplayName("getAll - returns list of users")
    void getAll_returnsList() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Doe");
        user2.setEmail("jane@example.com");
        user2.setRole("ADMIN");

        when(userReposistory.findAll()).thenReturn(List.of(user, user2));

        List<UserResponse> responses = userService.getAll();

        assertEquals(2, responses.size());
        assertEquals("John Doe", responses.get(0).getName());
        assertEquals("Jane Doe", responses.get(1).getName());
    }

    @Test
    @DisplayName("getAll - returns empty list")
    void getAll_emptyList() {
        when(userReposistory.findAll()).thenReturn(List.of());

        List<UserResponse> responses = userService.getAll();

        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("updateUser - success")
    void updateUser_success() {
        when(userReposistory.findById(1L)).thenReturn(Optional.of(user));
        when(userReposistory.existsByEmail("new@example.com")).thenReturn(false);
        when(userReposistory.save(any(User.class))).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest("New Name", "new@example.com", null, null);

        UserResponse response = userService.updateUser(1L, request);

        assertNotNull(response);
        verify(userReposistory).save(any(User.class));
    }

    @Test
    @DisplayName("updateUser - throws ResourceNotFoundException when user not found")
    void updateUser_userNotFound_throwsException() {
        when(userReposistory.findById(99L)).thenReturn(Optional.empty());

        UpdateUserRequest request = new UpdateUserRequest("Name", "a@b.com", null, null);

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(99L, request));
    }

    @Test
    @DisplayName("updateUser - throws BadRequestException for duplicate email")
    void updateUser_duplicateEmail_throwsBadRequest() {
        when(userReposistory.findById(1L)).thenReturn(Optional.of(user));
        when(userReposistory.existsByEmail("taken@example.com")).thenReturn(true);

        UpdateUserRequest request = new UpdateUserRequest(null, "taken@example.com", null, null);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> userService.updateUser(1L, request));

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    @DisplayName("updateUser - same email does not throw")
    void updateUser_sameEmail_noException() {
        when(userReposistory.findById(1L)).thenReturn(Optional.of(user));
        when(userReposistory.save(any(User.class))).thenReturn(user);

        UpdateUserRequest request = new UpdateUserRequest("Updated", "john@example.com", null, null);

        UserResponse response = userService.updateUser(1L, request);

        assertNotNull(response);
        verify(userReposistory, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("deleteById - success")
    void deleteById_success() {
        when(userReposistory.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userReposistory).delete(user);
    }

    @Test
    @DisplayName("deleteById - throws ResourceNotFoundException when user not found")
    void deleteById_userNotFound_throwsException() {
        when(userReposistory.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(99L));
        verify(userReposistory, never()).delete(any());
    }

    @Test
    @DisplayName("getCurrentUser - success")
    void getCurrentUser_success() {
        when(authenticatedUserService.getCurrentUser()).thenReturn(user);

        UserResponse response = userService.getCurrentUser();

        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());
    }
}
