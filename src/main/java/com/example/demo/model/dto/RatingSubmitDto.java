package com.example.demo.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingSubmitDto {
	@NotNull(message = "必須提供交易 ID")
    private Integer transactionId; // 用於關聯買家和賣家

    @NotNull(message = "分數不能為空")
    @Min(value = 1, message = "分數最低為 1 星")
    @Max(value = 5, message = "分數最高為 5 星")
    private Short score;

    private String comment;

}
