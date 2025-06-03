package com.example.demo.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "product_content")
public class ProductContent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_id")
	private Integer contentId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id",nullable = false, unique = true)
	private Product product;

	@Column(name = "title",nullable = false,length = 255 )
	private String title;
	
	@Column(name = "short_description",nullable = false,length = 500 )
	private String shortDescription;
	
	@Lob
	@Column(name = "full_description",columnDefinition = "TEXT")
	private String fullDescription;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "updated_at",nullable = false)
	private LocalDateTime updatedAt;
	
	
}
