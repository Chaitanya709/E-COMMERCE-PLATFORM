package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.CreateUserRequest;
import com.example.ecommerce.dto.request.UpdateUserRequest;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.entity.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static User toEntity(CreateUserRequest createUserRequest) {
        User entity = new User();

        entity.setName(createUserRequest.getName());
        entity.setEmail(createUserRequest.getEmail());
        entity.setPassword(createUserRequest.getPassword());
        entity.setRole(createUserRequest.getRole());
        entity.setCreatedAt(LocalDateTime.now());

        return entity;
    }

    public static void updateEntity(UpdateUserRequest updateUserRequest, User existingUser) {
        if (updateUserRequest.getName() != null) {
            existingUser.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getEmail() != null) {
            existingUser.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getPassword() != null) {
            existingUser.setPassword(updateUserRequest.getPassword());
        }
        if (updateUserRequest.getRole() != null) {
            existingUser.setRole(updateUserRequest.getRole());
        }
    }

    public static UserResponse toResponse(User user) {
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());

        return userResponse;

    }
}
