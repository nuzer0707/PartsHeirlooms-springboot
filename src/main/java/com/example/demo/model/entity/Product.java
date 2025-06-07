package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.model.entity.enums.ProductStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_user_id" ,nullable = false)
	private User sellerUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id" ,nullable = false)
	private Category category;
	
	@Column(name = "price",nullable = false,precision = 10,scale = 2)
	private BigDecimal price;
	
	@Column(name = "quantity",nullable = false,columnDefinition = "INT UNSIGNED DEFAULT 1")
	private Integer quantity;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status",nullable = false,columnDefinition = "ENUM('For_Sale','Sold','Removed') DEFAULT 'For_Sale' ")
	private ProductStatus status;
	
	@OneToOne(mappedBy = "product" , cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true) 
	private ProductContent productContent;
	
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@Builder.Default
	private List<ProductImage> productImages = new ArrayList<>();
	
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@Builder.Default
	private List<ProductTransactionDetail> transactionDetails = new ArrayList<>();
	
	
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
	@ToString.Exclude
	@Builder.Default
	private List<Favorite> favoriteByUser = new ArrayList<>();;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
}
