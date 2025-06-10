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
@Table(name = "messages")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_id")
	private Integer messageId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_user_id",nullable = false)
	private User senderUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_user_id",nullable = false)
	private User receiverUser;
	
	@Lob
	@Column(name = "content",nullable = false,columnDefinition = "TEXT")
	private String content;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;
	
	
}
