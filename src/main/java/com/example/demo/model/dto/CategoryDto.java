package com.example.demo.model.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
	
	@NotNull(message = "{categoryDto.categoryId.notNull}")
	@Range(min = 1, max = 9999, message = "{categoryDto.categoryId.range}")
	private Integer categoryId;
	
	@NotBlank(message = "{categoryDto.categoryName.notBlank}")
	@Size(min = 1, max = 50, message = "{categoryDto.categoryName.size}")
	private String categoryName;
	
}
