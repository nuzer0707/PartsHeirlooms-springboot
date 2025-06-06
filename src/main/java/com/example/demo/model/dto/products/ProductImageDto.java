package com.example.demo.model.dto.products;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {
	
	@NotBlank(message = "圖片 Base64 字串不能為空")
	private String imageBase64;
	
}
