package com.example.demo.model.dto.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductContentDto {

	@NotBlank(message = "標題不能為空")
	@Size(max = 255,message = "標題長度不能超過 255 個字元")
	private String title;
	
	@NotBlank(message = "簡短描述不能為空")
	@Size(max = 500,message = "標題長度不能超過 500 個字元")
	private String shortDescription;
	
	private String fullDescription;
	
}
