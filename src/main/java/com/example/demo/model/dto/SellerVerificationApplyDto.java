package com.example.demo.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SellerVerificationApplyDto {

	@NotEmpty(message = "至少需要提交一張驗證圖片")
	private List<String> verificationImageBases64;
	
}
