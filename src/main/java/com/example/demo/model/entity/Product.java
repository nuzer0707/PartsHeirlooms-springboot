package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.model.entity.enums.ProductStatus;

import jakarta.persistence.*;


@Entity
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
	@Column(name = "status",nullable = false,columnDefinition = "ENUM('For Sale','Sold','Removed') DEFAULT 'For Sale' ")
	private ProductStatus status;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@OneToOne(mappedBy = "product" , cascade = CascadeType.ALL,fetch = FetchType.LAZY,optional = false) 
	private ProductContent productContent;
	
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<ProductImage> productImages;
	
	@OneToMany(mappedBy = "product",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<ProductTransactionDetail> transactionDetails;
	
	
}
