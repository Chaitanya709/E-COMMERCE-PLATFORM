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
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product; // This is called a unidirectional relationship from CartItem to Product.
    private int quantity;
    private BigDecimal priceAtTime;
    private boolean deleted = false;
}

/*
 * Why do we write @ManyToOne in CartItem?
 * 
 * A good answer is:
 * Because multiple CartItems can belong to a single Cart.
 * The foreign key cart_id is stored in the CartItem table, making CartItem the
 * owning side of the relationship.
 * We use @OneToMany(mappedBy = "cart") in Cart to represent the reverse
 * navigation.
 */
