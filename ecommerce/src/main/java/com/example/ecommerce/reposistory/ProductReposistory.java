package com.example.ecommerce.reposistory;

import com.example.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReposistory extends JpaRepository<Product,Long> {

    List<Product> findByCategoryId(long categoryId);

    List<Product> findByNameContainingIgnoreCase(String keyword);
}

/*
Query derivation in Spring Data (specifically Spring Data JPA) is a mechanism that automatically generates
database queries based on the naming convention of repository interface methods,
 eliminating the need to write explicit SQL or JPQL.

⚙️ How It Works
When you define a method in a repository interface, Spring Data parses the method name at application
startup to construct the corresponding query.
The method name is divided into two main parts separated by the keyword By:

Subject (Prefix): Defines the operation type (e.g., find, read, query, count, exists, delete).
Predicate (Criteria): Defines the WHERE clause conditions using entity property names and logical operators.
For example, the method findByLastNameAndFirstName is parsed to generate a query equivalent to:
SELECT ... FROM Entity WHERE lastName = ? AND firstName = ?
 */