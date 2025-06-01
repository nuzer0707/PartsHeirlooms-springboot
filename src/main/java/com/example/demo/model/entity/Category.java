package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categorys" ,
uniqueConstraints = @UniqueConstraint(columnNames = "category_name"))
public class Category {

	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY) //room_id 會從 1 開始自動生成, 每次 +1, 過號不補
	@Column(name = "category_id")
	private Integer categoryId;
	
	@Column(name = "category_name" ,nullable = false,length = 50)
	private String categoryName;

}
