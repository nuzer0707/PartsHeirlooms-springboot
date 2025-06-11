package com.example.demo.controller;


import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.MessageDto;
import com.example.demo.model.dto.MessageSendDto;
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
	
	
	@PostMapping
	public ResponseEntity<ApiResponse<MessageDto>> sendMessage(
			@Valid @RequestBody MessageSendDto messageSendDto,HttpSession session){
		
		 UserCert userCert = (UserCert) session.getAttribute("userCert");
		 
		 if(userCert==null) return unauthorizedGenericResponse("請先登入以發送消息");
		 
		 
		 try {
			 MessageDto sentMessage =messageService.sendMessage(userCert.getUserId(), messageSendDto);
			 
			 return ResponseEntity.status(HttpStatus.CREATED)
					 .body(ApiResponse.success("消息發送成功", sentMessage));
			 
			
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "發送失敗："+e.getMessage()));
		}catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "發送失敗："+e.getMessage()));
		}catch(AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "發送失敗："+e.getMessage()));
		}catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "發送失敗："+e.getMessage()));
		}
	}
	

	@GetMapping("/conversation/{otherUserId}")
	public ResponseEntity<ApiResponse<List<MessageDto>>> getConversationWithUser(
			@PathVariable Integer otherUserId,HttpSession session){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		
		if(userCert==null) return unauthorizedGenericResponse("請先登入以發送消息");
		 
		
		try {
			List<MessageDto> conversation = 
					messageService.getConversationBetweenUsers(userCert.getUserId(), otherUserId, userCert.getUserId());
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
	
	
	
	
	private ResponseEntity<ApiResponse<Void>> unauthorizedResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}

	private <T> ResponseEntity<ApiResponse<T>> unauthorizedGenericResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}
	
}
