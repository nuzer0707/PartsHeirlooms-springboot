package com.example.demo.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {

	 @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "cart_item_id")
   private Integer cartItemId;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "product_id", nullable = false)
   private Product product;

   @Column(name = "quantity", nullable = false)
   private Integer quantity;

   @CreationTimestamp
   @Column(name = "created_at", nullable = false, updatable = false)
   private LocalDateTime createdAt;

   @UpdateTimestamp
   @Column(name = "updated_at", nullable = false)
   private LocalDateTime updatedAt;

	
}
