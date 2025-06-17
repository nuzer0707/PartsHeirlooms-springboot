package com.example.demo.controller;


import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.message.ConversationDto;
import com.example.demo.model.dto.message.MessageDto;
import com.example.demo.model.dto.message.MessageSendDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.MessageService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class MessageController {
	
	@Autowired
	private MessageService messageService;
	
	
	
	

	@GetMapping("/conversation/{otherUserId}")
	public ResponseEntity<ApiResponse<List<MessageDto>>> getConversationWithUser(
			@PathVariable Integer otherUserId,HttpSession session){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		
		if(userCert==null) return unauthorizedGenericResponse("請先登入以發送消息");
		Integer currentUserId = userCert.getUserId();
		
		try {
			messageService.markConversationAsRead(otherUserId, currentUserId);
			
			List<MessageDto> conversation = 
					messageService.getConversationBetweenUsers(currentUserId, otherUserId, currentUserId);
			return ResponseEntity.ok(ApiResponse.success("查詢聊天記錄成功",conversation));
		} catch (UserNotFoundException e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}
		
	}
	
	@GetMapping("/transaction/{transactionId}")
	public ResponseEntity<ApiResponse<List<MessageDto>>> getMessagesForTransaction(
		@PathVariable Integer transactionId , HttpSession session) throws AccessDeniedException{
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		if(userCert==null) return unauthorizedGenericResponse("請先登入以發送消息");
		
		try {
			List<MessageDto> messageDtos = messageService.getMessagesForTransaction(transactionId, userCert.getUserId());
			
			return ResponseEntity.ok(ApiResponse.success("查詢交易相關消息成功", messageDtos));
			
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(),  "找不到交易：" + e.getMessage()));
		}	catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),  "找不到交易：" + e.getMessage()));
		}

	}
	
	@GetMapping("/conversations")
    public ResponseEntity<ApiResponse<Page<ConversationDto>>> getMyConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            HttpSession session){
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		if(userCert==null) return unauthorizedGenericResponse("請先登入以發送消息");
		
		try {
			 Pageable pageable = PageRequest.of(page, size);
			 
			 Page<ConversationDto> conversationsPage = messageService.getConversationsForUser(userCert.getUserId(), pageable);
			 String message = conversationsPage.isEmpty() ? "您還沒有任何對話" : "查詢對話列表成功";
			return ResponseEntity.ok(ApiResponse.success(message, conversationsPage));
			
		} catch (UserNotFoundException  e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),  "系統錯誤：" + e.getMessage()));
		}
		
		
	}
	
	
	
	
	private ResponseEntity<ApiResponse<Void>> unauthorizedResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}

	private <T> ResponseEntity<ApiResponse<T>> unauthorizedGenericResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}
	
}
