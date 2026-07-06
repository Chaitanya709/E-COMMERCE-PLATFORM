package com.example.ecommerce.service;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.reposistory.UserReposistory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserReposistory userReposistory;

    private static final Set<String> VALID_ROLES = Set.of("USER", "ADMIN");

    // Spring Security needs a way to load user from DB.

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userReposistory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole();
        if (role == null || !VALID_ROLES.contains(role)) {
            throw new IllegalStateException("Invalid role assigned to user: " + role);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}
