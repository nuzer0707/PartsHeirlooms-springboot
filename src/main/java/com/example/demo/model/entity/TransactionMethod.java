package com.example.demo.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction_methods",
uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class TransactionMethod {
	
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
