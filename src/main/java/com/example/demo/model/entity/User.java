package com.example.demo.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.model.entity.enums.UserRole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "username", unique = true, nullable = false, length = 50)
	private String username;

	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "hash_salt", nullable = false, length = 255)
	private String hashSalt;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "active")
	private Boolean active;

	@Enumerated(EnumType.STRING)
	@Column(name = "primary_role", nullable = false, columnDefinition = "Enum('BUYER','SELLER','ADMIN','BLACK') DEFAULT 'BUYER'")
	private UserRole primaryRole; // BLACK 黑名單

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@ToString.Exclude
	@Builder.Default
	private List<Favorite> favoriteByProduct = new ArrayList<>();

	@Column(name = "email_token", length = 100, unique = true) // token 應該是唯一的，或至少在未激活時唯一
	private String emailToken;

	@Column(name = "last_email_sent_at")
	private LocalDateTime lastEmailSentAt;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "user", // "user" 是 CartItem Entity 中 User 型別的屬性名
			cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@ToString.Exclude // 避免在 toString() 中產生無限循環
	@Builder.Default
	private Set<CartItem> cartItems = new HashSet<>();

}
