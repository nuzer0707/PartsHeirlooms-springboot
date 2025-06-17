package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.model.dto.message.ConversationDto;
import com.example.demo.model.dto.message.ConversationUserDto;
import com.example.demo.model.dto.message.LastMessageDto;
import com.example.demo.model.dto.message.MessageDto;
import com.example.demo.model.dto.message.MessageSendDto;
import com.example.demo.model.entity.Message;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private MessageMapper messageMapper;

	@Override
	@Transactional
	public MessageDto sendMessage(Integer senderUserId, MessageSendDto messageSendDto)
			throws UserNotFoundException, ProductNotFoundException, AccessDeniedException {
		User sender = userRepository.findById(senderUserId)
				.orElseThrow(() -> new UserNotFoundException("找不到接收者用戶，ID: " + senderUserId));

		User receiver = userRepository.findById(messageSendDto.getReceiverUserId())
				.orElseThrow(() -> new UserNotFoundException("找不到接收者用戶，ID: " + messageSendDto.getReceiverUserId()));

		if (senderUserId.equals(messageSendDto.getReceiverUserId())) {
			throw new IllegalArgumentException("發送者和接收者不能是同一人");
		}

		Transaction transaction = null;

		if (messageSendDto.getTransactionId() != null) {
			transaction = transactionRepository.findById(messageSendDto.getTransactionId()).orElseThrow(
					() -> new ProductNotFoundException("找不到關聯的交易，ID: " + messageSendDto.getTransactionId()));

			boolean isBuyer = transaction.getBuyerUser().getUserId().equals(senderUserId);
			boolean isSeller = transaction.getSellerUser().getUserId().equals(senderUserId);
			boolean receiverIsBuyer = transaction.getBuyerUser().getUserId().equals(messageSendDto.getReceiverUserId());
			boolean receiverIsSeller = transaction.getSellerUser().getUserId()
					.equals(messageSendDto.getReceiverUserId());

			if (!((isBuyer && receiverIsSeller) || (isSeller && receiverIsBuyer))) {
				throw new AccessDeniedException("您無權就此交易與該用戶發送消息，或接收者非此交易的另一方");
			}
		}
		Message message = Message.builder().senderUser(sender).receiverUser(receiver)
				.content(messageSendDto.getContent()).transaction(transaction).build();
		Message savedMessage = messageRepository.save(message);
		return messageMapper.toDto(savedMessage);

	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageDto> getConversationBetweenUsers(Integer userId1, Integer userId2, Integer requestingUserId)
			throws UserNotFoundException, AccessDeniedException {
		if (!requestingUserId.equals(userId1) && !requestingUserId.equals(userId2)) {
			throw new AccessDeniedException("您無權查看此對話記錄");
		}

		if (!userRepository.existsById(userId1)) {
			throw new UserNotFoundException("找不到用戶，ID:" + userId1);
		}
		if (!userRepository.existsById(userId2)) {
			throw new UserNotFoundException("找不到用戶，ID:" + userId2);
		}

		return messageRepository.findConversationBetweenUsers(userId1, userId2).stream().map(messageMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageDto> getMessagesForTransaction(Integer transactionId, Integer requestingUserId)
			throws UserNotFoundException {

		Transaction transaction = transactionRepository.findById(transactionId)
				.orElseThrow(() -> new ProductNotFoundException("找不到交易，ID: " + transactionId));

		boolean isByer = transaction.getBuyerUser().getUserId().equals(requestingUserId);
		boolean isSeller = transaction.getSellerUser().getUserId().equals(requestingUserId);

		if (!isByer && !isSeller) {
			throw new AccessDeniedException("您無權查看此交易的對話記錄");
		}

		userRepository.findById(requestingUserId)
				.orElseThrow(() -> new UserNotFoundException("請求用戶不存在，ID: " + requestingUserId));

		return messageRepository.findByTransaction_TransactionIdOrderByCreatedAtAsc(transactionId).stream()
				.map(messageMapper::toDto).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ConversationDto> getConversationsForUser(Integer userId,Pageable pageable) throws UserNotFoundException {
		
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("找不到用戶 ID: " + userId);
		}

		List<Message> allMessages = messageRepository.findAllMessagesForUser(userId);
		
		Map<Integer, ConversationDto> conversationMap = new LinkedHashMap<>();

		// 遍歷所有訊息來計算未讀數

		for (Message message : allMessages) {
			
			User otherPartyUser = message.getSenderUser().getUserId().equals(userId) 
					? message.getReceiverUser()
					: message.getSenderUser();

			Integer otherPartyId = otherPartyUser.getUserId();

			// 使用 computeIfAbsent 來初始化 ConversationDto
			ConversationDto conversation = conversationMap.computeIfAbsent(otherPartyId, id -> {
				ConversationDto convDto = new ConversationDto();

				ConversationUserDto otherPartyDto = new ConversationUserDto();

				otherPartyDto.setUserId(otherPartyUser.getUserId());
				otherPartyDto.setUsername(otherPartyUser.getUsername());
				convDto.setOtherParty(otherPartyDto);

				// 因為是第一次遇到（按時間倒序），所以這就是最後一條訊息
				LastMessageDto lastMessageDto = new LastMessageDto();
				lastMessageDto.setContent(message.getContent());
				lastMessageDto.setCreatedAt(message.getCreatedAt());
				// 這裡的 isRead 反映的是【最後一條訊息】的讀取狀態
				// 如果是對方發來的，就用實際的 isRead 值；如果是自己發的，就永遠是 true (已讀)
				lastMessageDto.setRead(message.getReceiverUser().getUserId().equals(userId) ? message.isRead() : true);
				convDto.setLastMessage(lastMessageDto);

				convDto.setUnreadCount(0);
				return convDto;

			});
			// 計算未讀訊息的邏輯
			// 條件：這條訊息是對方發給我的，並且我還沒有讀
			if (message.getReceiverUser().getUserId().equals(userId) && !message.isRead()) {
				conversation.setUnreadCount(conversation.getUnreadCount()+1);
			}

		}
		List<ConversationDto>allConversations = new ArrayList<>(conversationMap.values());
		
		int start = (int) pageable.getOffset();
		int end = Math.min((start+pageable.getPageSize()), allConversations.size());
		
		List<ConversationDto> pageContent = (start<=end)?allConversations.subList(start, end):Collections.emptyList(); 
		
		return new PageImpl<>(pageContent,pageable,allConversations.size());
	}

	@Override
	@Transactional
	public void markConversationAsRead(Integer otherPartyId, Integer currentUserId) {
		messageRepository.markAsRead(otherPartyId, currentUserId);
		
	}

}
