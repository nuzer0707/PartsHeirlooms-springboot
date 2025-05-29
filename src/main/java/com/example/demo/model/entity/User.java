package com.example.demo.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.model.entity.enums.UserRole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
		})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="user_id")
	private Integer userId;
	
	@Column(name = "username", nullable = false,length = 50)
	private String username;
	
	@Column(name = "email",nullable = false,length = 255)
	private String email;
	
	@Column(name = "password_hash",nullable = false,length = 255)
	private String passwordHash;
	
	@Column(name = "hash_salt",nullable = false,length = 255)
	private String hashSalt;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "primary_role",nullable = false,columnDefinition = "Enum('BUYER','SELLER','ADMIN') DEFAULT 'BUYER'")
	private UserRole primaryRole;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false,updatable = false)
	private LocalDateTime createdAt;
	
	
}
