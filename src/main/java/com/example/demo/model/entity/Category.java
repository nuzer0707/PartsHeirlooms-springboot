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
@Table(name = "categorys" ,
uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Integer categoryId;
	
	@Column(name = "name" ,nullable = false,length = 100)
	private String name;
	
	@CreationTimestamp
	@Column(name = "creatded_at" ,nullable = false,updatable = false)
	private LocalDateTime creatdedAt;
	
}
