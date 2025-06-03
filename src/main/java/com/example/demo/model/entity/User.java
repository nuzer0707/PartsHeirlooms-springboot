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
	
	@Column(name = "username",unique = true, nullable = false,length = 50)
	private String username;
	
	@Column(name = "password_hash",nullable = false,length = 255)
	private String passwordHash;
	
	@Column(name = "hash_salt",nullable = false,length = 255)
	private String hashSalt;
	
	@Column(name = "email",nullable = false,length = 255)
	private String email;
	
	@Column(name = "active")
	private Boolean active;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "primary_role",nullable = false,columnDefinition = "Enum('BUYER','SELLER','ADMIN','BLACK') DEFAULT 'BUYER'")
	private UserRole primaryRole; //BLACK 黑名單
	

	
}
