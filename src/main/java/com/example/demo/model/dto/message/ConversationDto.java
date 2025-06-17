package com.example.demo.model.dto.message;

import lombok.Data;

@Data
public class ConversationDto {
	private ConversationUserDto otherParty;
	private LastMessageDto lastMessage;
	private int unreadCount;
}
