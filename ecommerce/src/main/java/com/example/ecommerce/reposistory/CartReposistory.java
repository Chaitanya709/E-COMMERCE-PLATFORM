package com.example.ecommerce.reposistory;

import com.example.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartReposistory extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUserId(Long id);
}
