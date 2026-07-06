package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.reposistory.UserReposistory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserReposistory userReposistory;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("User is not authenticated");
    }

        String email = authentication.getName();

        return userReposistory.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));
    }

    /*
    SecurityContextHolder is a thread-local storage provided by Spring Security that stores the
    Authentication object of the currently authenticated user for the lifetime of the request,
    allowing any layer of the application to access the logged-in user's details without passing
    user information explicitly

     SecurityContextHolder
        ↓
     SecurityContext
        ↓
     Authentication
        ↓
     UsernamePasswordAuthenticationToken
        ↓
     UserDetails
        ↓
     Email
     Role
     Authorities
     */
}
