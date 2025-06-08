package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.User;

@Component
public class TransactionMapper {
	
	@Autowired
	private ModelMapper modelMapper;
	
	public TransactionDto toDto(Transaction transaction) {
		if (transaction == null) {
			return null;
		}
		
		TransactionDto dto = modelMapper.map(transaction,TransactionDto.class);
		
		Product product = transaction.getProductId();
		if(product!=null) {
			dto.setProductId(product.getProductId());
			if(product.getProductContent()!=null) {
				dto.setProductTitle(product.getProductContent().getTitle());
			}
			if(product.getProductImages()!=null&&!product.getProductImages().isEmpty()) {
				dto.setProductFirstImageBases64(product.getProductImages().get(0).getImageBase64());
			}
		}
		
		User buyer = transaction.getBuyerUser();
		if(buyer!=null) {
			dto.setBuyerUserId(buyer.getUserId());
			dto.setBuyerUsername(buyer.getUsername());
		}
		
		
		return dto;
		
	}
	
}
