package com.sintad.prueba_tecnica_fullstack.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.sintad.prueba_tecnica_fullstack.shared.filter.Operator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

     @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static final Map<String, Operator> ALLOWED_FILTERS = Map.of(
            "name", Operator.LIKE,
            "price", Operator.LIKE,
            "stock", Operator.EQ,
            "user_id", Operator.EQ,
            "description", Operator.LIKE,
            "category.name", Operator.LIKE,
            "user.username", Operator.LIKE
            );
}
