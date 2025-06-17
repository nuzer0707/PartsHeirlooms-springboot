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
import com.example.demo.model.dto.MessageDto;
import com.example.demo.model.dto.MessageSendDto;
import com.example.demo.service.MessageService;


@Controller
public class WebSocketChatController {

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	
  /**
   * 處理從用戶端發送到 "/app/chat.sendMessage" 的訊息。
   *
   * @param messageSendDto 包含訊息內容和接收者ID的 DTO
   * @param headerAccessor 用於獲取 session 屬性，從而得到發送者訊息
   */
	
	
	@MessageMapping("/chat.sendMessage")
	public void sendMessage(@Payload MessageSendDto  messageSendDto,SimpMessageHeaderAccessor headerAccessor ) {
	
		try {
		  
      // 1. 調用現有服務儲存訊息
      // **請在 `MessageSendDto` 中新增 `private Integer senderUserId;`**
			if (messageSendDto.getSenderUserId() == null) {
			  System.err.println("Error: senderUserId is missing in MessageSendDto");
        return;
			}
			MessageDto savedMessage = messageService.sendMessage(messageSendDto.getSenderUserId(), messageSendDto);

			// 2. 將儲存的訊息發送給指定的接收者
      // 我們將訊息發送到一個特定的 "queue"，只有訂閱了這個 queue 的用戶能收到。
      // 目的地格式：/queue/messages/{userId}
			String destination = "/queue/messages" + savedMessage.getReceiverUserId();
			
			messagingTemplate.convertAndSend(destination,savedMessage);
			
			 // (可選) 同時也將訊息發回給發送者，讓發送者的客戶端也能同步顯示
      String senderDestination = "/queue/messages/" + savedMessage.getSenderUserId();
      messagingTemplate.convertAndSend(senderDestination, savedMessage);
			
			
		} catch (UserNotFoundException | ProductNotFoundException | AccessDeniedException e) {
		// 處理錯誤，可以發送一個錯誤訊息給發送者
			String errorDestination = "/queue/errors/"+ messageSendDto.getSenderUserId();
			
			messagingTemplate.convertAndSend(errorDestination,e.getMessage());
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	
}
