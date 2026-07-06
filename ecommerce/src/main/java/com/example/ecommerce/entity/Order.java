package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "orders")
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItem;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private LocalDateTime placedAt;
    private boolean deleted = false;

}
/*
 * 
 * Order fetched
 * ↓
 * Hibernate session closes
 * ↓
 * Mapper tries order.getOrderItems()
 * ↓
 * orderItems is LAZY
 * ↓
 * LazyInitializationException
 * 
 * ## LazyInitializationException – Summary
 * 
 * ### What the Error Was
 * `failed to lazily initialize a collection of role: Order.orderItem: could not
 * initialize proxy - no Session`
 * 
 * ### Why It Happened
 * - Your `@OneToMany` mapping defaults to **`FetchType.LAZY`** when no fetch
 * type is specified
 * - Hibernate loads `orderItems` **on demand**, not immediately with the
 * `Order`
 * - When the code (or Jackson serializer) tried to access `orderItems`, the
 * **Hibernate session was already closed**
 * — causing the crash
 * 
 * ### What You Had
 * ```java
 * 
 * @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval =
 * true)
 * private List<OrderItem> orderItem; // LAZY by default — session closed before
 * access
 * ```
 * 
 * ### How to Fix It
 * 
 * | Fix | How | When to Use |
 * |---|---|---|
 * | `@Transactional` | Keeps session open during the method | Always a good
 * baseline |
 * | `JOIN FETCH` | Loads items in a single query | When you always need items |
 * | `FetchType.EAGER` | Loads items every time | Simple cases, small data |
 * 
 * ### Key Takeaway
 * > `@OneToMany` is **LAZY by default**. Always use `@Transactional` on your
 * service methods, or
 * use `JOIN FETCH` in your query — so Hibernate can access related collections
 * **before the session closes**.
 */
