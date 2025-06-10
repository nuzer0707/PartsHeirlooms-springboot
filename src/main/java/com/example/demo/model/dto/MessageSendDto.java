package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageSendDto {
	
	private Integer receiverUserId;
	
	private Integer transactionId; //與特定交易相關
	
	@NotBlank(message = "消息內容不能為空")
	@Size(max = 1000,message = "消息內容不能超過1000字符")
	private String content;
	
}
