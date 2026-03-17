package com.aistock.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String symbol;
    private BigDecimal price;        // price at which order was executed
    private BigDecimal quantity;
    private String type;             // "BUY" or "SELL"
    private LocalDateTime timestamp = LocalDateTime.now();
}
