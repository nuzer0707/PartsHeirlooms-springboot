package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.message.MessageDto;
import com.example.demo.model.entity.Message;

@Component
public class MessageMapper {
	
	@Autowired
	private ModelMapper modelMapper;
	
	public MessageDto toDto(Message message) {
		
		if(message ==null) {
			return null;
		}
		
		MessageDto dto =modelMapper.map(message, MessageDto.class);
		
		if(message.getSenderUser()!=null) {
			dto.setSenderUserId(message.getSenderUser().getUserId());
			dto.setSenderUsername(message.getSenderUser().getUsername());
		}
		if(message.getReceiverUser()!=null) {
			dto.setReceiverUserId(message.getReceiverUser().getUserId());
			dto.setReceiverUsername(message.getReceiverUser().getUsername());
		}
		if(message.getTransaction()!=null){
			dto.setTransactionId(message.getTransaction().getTransactionId());
		}
		
		return dto;
		
	}
	
	// 注意：從 MessageSendDto 到 Message Entity 的轉換通常在 Service 層進行，
    // 因為它需要額外的信息，如 senderUser 和 receiverUser 實體。
    // 但如果只是部分映射，也可以在這裡提供一個輔助方法。
    // public Message sendDtoToEntity(MessageSendDto sendDto) {
    //     if (sendDto == null) {
    //         return null;
    //     }
    //     // 只映射 content，其他關聯對象由 Service 處理
    //     Message message = new Message();
    //     message.setContent(sendDto.getContent());
    //     return message;
    // }

	
}
