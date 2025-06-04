package com.example.demo.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.entity.enums.UserRole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
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
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
	@ToString.Exclude
	@Builder.Default
	private List<Favorite> favoriteByProduct = new ArrayList<>();
	
	@Column(name = "email_Token",length = 100,unique = true)// token 應該是唯一的，或至少在未激活時唯一
	private String emailToken;
	
}
