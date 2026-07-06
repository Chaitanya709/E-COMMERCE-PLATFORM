package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private boolean deleted = false;
}
/*
 * id
 * order
 * product
 * quantity
 * priceAtPurchase
 */
