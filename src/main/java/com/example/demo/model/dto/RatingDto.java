package com.example.demo.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RatingDto {

	private Integer ratingId;
	private Integer raterUserId;
	private String raterUsername;
	
	@NotBlank(message = "必須指定被評分的用戶")
	private Integer transactionId;
	
	private Integer ratedUserId;
	private String ratedUsername;
	
	@NotBlank(message = "分數不能為空")
	@Min(value = 1,message = "分數最低為 1 星")
	@Max(value = 5,message = "分數最高為 5 星")
	private Short score;
	
	private String comment;
	private LocalDateTime createdAt;
	
}
