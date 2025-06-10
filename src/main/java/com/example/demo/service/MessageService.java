package com.example.demo.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.MessageDto;
import com.example.demo.model.dto.MessageSendDto;

public interface MessageService {

	/**
     * 發送一條新消息。
     *
     * @param senderUserId   發送者ID
     * @param messageSendDto 包含接收者ID、內容和可選的交易ID的DTO
     * @return 已保存的消息DTO
     * @throws UserNotFoundException 如果找不到發送者或接收者
     * @throws ProductNotFoundException (或 TransactionNotFoundException) 如果提供了交易ID但交易不存在
     * @throws AccessDeniedException 如果發送者無權就此交易發送消息 (例如，不是買家也不是賣家)
     */
	MessageDto sendMessage(Integer senderUserId,MessageSendDto messageSendDto)throws UserNotFoundException,ProductNotFoundException,AccessDeniedException;
	
	  /**
     * 獲取兩個用戶之間的對話記錄。
     *
     * @param userId1        第一個用戶ID
     * @param userId2        第二個用戶ID
     * @param requestingUserId 當前請求用戶的ID (用於權限驗證)
     * @return 消息DTO列表
     * @throws UserNotFoundException 如果用戶不存在
     * @throws AccessDeniedException 如果請求者無權查看此對話
     */
	
	List<MessageDto> getConversationBetweenUsers(Integer userId1,Integer userId2,Integer requestingUserId)throws UserNotFoundException,AccessDeniedException;
	
	   /**
     * 獲取特定交易相關的對話記錄。
     *
     * @param transactionId  交易ID
     * @param requestingUserId 當前請求用戶的ID (用於權限驗證)
     * @return 消息DTO列表
	 * @throws UserNotFoundException 
     * @throws ProductNotFoundException (或 TransactionNotFoundException) 如果交易不存在
     * @throws AccessDeniedException 如果請求者無權查看此交易的對話
     */
	List<MessageDto> getMessagesForTransaction(Integer transactionId,Integer requestingUserId) throws UserNotFoundException;
	
	
	
	
	
}
