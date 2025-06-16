package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.demo.model.entity.enums.TransactionStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

	
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_id")
  private Integer transactionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product productId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_user_id", nullable = false)
  private User sellerUser;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_user_id", nullable = false)
  private User buyerUser;



  @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal finalPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "ENUM('Pending Payment','Paid','Processing','Shipped','Completed','Cancelled') DEFAULT 'Pending Payment'")
  private TransactionStatus status;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToOne(
	        mappedBy = "transaction",
	        cascade = CascadeType.ALL,
	        fetch = FetchType.LAZY,
	        orphanRemoval = true
	    )
	    private TransactionShipmentDetail shipmentDetail;

  
}
