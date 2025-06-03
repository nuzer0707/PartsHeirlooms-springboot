package com.example.demo.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "verification_images")
public class VerificationImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Integer imageId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "verification_id",nullable = false)
	private SellerVerification sellerVerification;
	
	@Column(name = "image_url",nullable = false,length = 500)
	private String imageUrl;
	
	@CreationTimestamp
	@Column(name = "update_at",nullable = false,updatable = false)
	private LocalDateTime updateAt;
	
}
