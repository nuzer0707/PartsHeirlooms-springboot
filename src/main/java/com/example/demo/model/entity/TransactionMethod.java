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
@Table(name = "transaction_methods",
uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class TransactionMethod {
	
	// ▼▼▼ 在 Entity 中定義公開的靜態常數 ▼▼▼
    public static final Integer ID_SHIPPING = 1; // 物流
    public static final Integer ID_MEETUP = 2;   // 面交
    // ▲▲▲ 定義結束 ▲▲▲

    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "method_id")
	private Integer methodId;
	
	@Column(name = "name",nullable = false,length = 50)
	private String name;
	
	@Column(name = "description",length = 255)
	private String description;
	
	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
}
