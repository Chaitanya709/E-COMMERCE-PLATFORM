package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.UpdateUserRequest;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.reposistory.UserReposistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserReposistory userReposistory;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userReposistory.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userReposistory.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User existingUser = userReposistory.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getEmail() != null
                && !request.getEmail().equals(existingUser.getEmail())
                && userReposistory.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        UserMapper.updateEntity(request, existingUser);
        User savedUser = userReposistory.save(existingUser);
        return UserMapper.toResponse(savedUser);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userReposistory.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userReposistory.delete(user);
    }

    public UserResponse getCurrentUser() {
        User user = authenticatedUserService.getCurrentUser();
        return UserMapper.toResponse(user);
    }
}
