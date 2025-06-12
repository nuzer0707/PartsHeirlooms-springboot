package com.example.demo.mapper;


import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.demo.model.dto.Cart.CartItemDto;
import com.example.demo.model.dto.Cart.ShoppingCartDto;
import com.example.demo.model.entity.CartItem;
import com.example.demo.model.entity.Product;



@Component
public class ShoppingCartMapper {

	public ShoppingCartDto toDto(List<CartItem>cartItems) {
		
		if(cartItems ==null || cartItems.isEmpty()) {
			ShoppingCartDto emptyCart = new ShoppingCartDto();
			emptyCart.setItems(Collections.emptyList());
			emptyCart.setTotalPrice(BigDecimal.ZERO);
			return emptyCart;
		}
		
		 // 將每個 CartItem 實體映射為 CartItemDto
		List<CartItemDto> itemDtos = cartItems.stream()
				.map(this::toCartItemDto)
				.collect(Collectors.toList());
		
		
		 // 計算總價
		 BigDecimal totalPrice = itemDtos.stream()
         .map(CartItemDto::getSubtotal)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
		 
		 ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
		 shoppingCartDto.setItems(itemDtos);
		 shoppingCartDto.setTotalPrice(totalPrice);

		 return shoppingCartDto;
	}
		
	 /**
   * 輔助方法：將單個 CartItem 實體轉換為 CartItemDto。
   *
   * @param cartItem 購物車項目的實體
   * @return 單個購物項的 DTO
   */
  private CartItemDto toCartItemDto(CartItem cartItem) {
      Product product = cartItem.getProduct();
      int quantity = cartItem.getQuantity();

      CartItemDto itemDto = new CartItemDto();
      itemDto.setProductId(product.getProductId());
      itemDto.setPrice(product.getPrice());
      itemDto.setQuantity(quantity);

      if (product.getProductContent() != null) {
          itemDto.setProductTitle(product.getProductContent().getTitle());
      }

      if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
          itemDto.setProductFirstImageBase64(product.getProductImages().get(0).getImageBase64());
      }

      BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
      itemDto.setSubtotal(subtotal);

      return itemDto;
  }
}

	

