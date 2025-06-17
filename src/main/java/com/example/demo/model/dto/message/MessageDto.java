package com.example.demo.model.dto.message;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public  class MessageDto {
	
	private Integer messageId;
	
	private Integer senderUserId;
	
	private String senderUsername;
	
	private Integer receiverUserId;
	
	private String receiverUsername;
	
	private String content;
	
	private LocalDateTime createdAt;
	
	private Integer transactionId; //與交易綁定
	
}
