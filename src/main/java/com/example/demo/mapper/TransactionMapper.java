package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.TransactionShipmentDetailDto;
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

		TransactionDto dto = modelMapper.map(transaction, TransactionDto.class);

		// 映射買賣雙方資訊
		User buyer = transaction.getBuyerUser();
		if (buyer != null) {
			dto.setBuyerUserId(buyer.getUserId());
			dto.setBuyerUsername(buyer.getUsername());
		}
		
		User  seller = transaction.getSellerUser();
		if(seller != null) {
			dto.setSellerUserId(seller.getUserId());
			dto.setSellerUsername(seller.getUsername());
		}
		
		Product product = transaction.getProductId();
		  // 映射關聯的商品ID和預覽圖
		if (product != null) {
			dto.setProductId(product.getProductId());
			 // 預覽圖資訊仍然需要從原始商品獲取，因為快照裡沒存
			if(product.getProductImages() != null&& !product.getProductImages().isEmpty()) {
				dto.setProductFirstImageBases64(product.getProductImages().get(0).getImageBase64());
			}
			
		}
		  // 映射完整的快照資訊
		if (transaction.getShipmentDetail() != null) {
			dto.setShipmentDetail(modelMapper.map(transaction.getShipmentDetail(), TransactionShipmentDetailDto.class));
		}

		return dto;

	}

}
