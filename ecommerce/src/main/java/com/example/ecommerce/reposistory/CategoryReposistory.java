package com.example.ecommerce.reposistory;

import com.example.ecommerce.entity.Category;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Function;

@Repository
public interface CategoryReposistory extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

}
