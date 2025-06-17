package com.example.demo.model.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageSendDto {
	
	@NotNull(message = "發送者 ID 不能為空")
	private Integer senderUserId;   
	
	@NotNull(message = "接收者 ID 不能為空")
	private Integer receiverUserId;
	
	private Integer transactionId; //與特定交易相關
	
	
	
	@NotBlank(message = "消息內容不能為空")
	@Size(max = 1000,message = "消息內容不能超過1000字符")
	private String content;
	
	
	
	
	
}
