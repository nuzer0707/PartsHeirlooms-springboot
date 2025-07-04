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
@Table(name = "ratings")
public class Rating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rating_id")
	private Integer ratingId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rater_user_id",nullable = false)
	private User raterUserId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rated_user_id",nullable = false)
	private User ratedUserId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;
	
	@Column(name = "score",nullable = false)
	private Short score;
	
	@Lob
	@Column(name = "comment", columnDefinition = "TEXT")
	private String comment;
	
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
}
