package com.example.demo.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.message.MessageDto;
import com.example.demo.model.dto.message.MessageSendDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.service.MessageService;

@Controller
public class WebSocketChatController {

	@Autowired
	private MessageService messageService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload MessageSendDto messageSendDto, SimpMessageHeaderAccessor headerAccessor) {

		UserCert userCert = (UserCert) headerAccessor.getSessionAttributes().get("userCert");

		if (userCert == null) {
			System.err.println("Critical Error: UserCert is null in WebSocket session for session ID: "
					+ headerAccessor.getSessionId());
			// 通常這種情況不應該發生，因為 interceptor 已經攔截了未登入的請求
			return;
		}

		Integer senderId = userCert.getUserId();

		try {

			MessageDto savedMessage = messageService.sendMessage(senderId, messageSendDto);

			// 廣播給接收者
			String receiverDestination = "/queue/messages/" + savedMessage.getReceiverUserId();
			messagingTemplate.convertAndSend(receiverDestination, savedMessage);

			// 回傳給發送者自己，用於多端同步和確認
			String senderDestination = "/queue/messages/" + savedMessage.getSenderUserId();
			messagingTemplate.convertAndSend(senderDestination, savedMessage);

		} catch (Exception e) {
			System.err.println("Error while sending message: " + e.getMessage());
			// 將錯誤訊息只發送給發送者本人
			String errorDestination = "/queue/errors/" + senderId;
			messagingTemplate.convertAndSend(errorDestination, "訊息發送失敗: " + e.getMessage());
			e.printStackTrace();
		}

	}

}
