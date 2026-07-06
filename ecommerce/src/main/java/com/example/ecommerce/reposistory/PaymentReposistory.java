package com.example.ecommerce.reposistory;

import com.example.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentReposistory extends JpaRepository<Payment,Long> {
}
