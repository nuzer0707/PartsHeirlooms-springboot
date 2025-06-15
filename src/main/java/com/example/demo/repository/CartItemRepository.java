package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer>{

  /**
   * 根據使用者 ID 查找其所有的購物車項目。
   */
  List<CartItem> findByUser_UserId(Integer userId);

  /**
   * 根據使用者 ID 和商品 ID 查找特定的購物車項目。
   */
  Optional<CartItem> findByUser_UserIdAndProduct_ProductId(Integer userId, Integer productId);

  /**
   * 根據使用者 ID 刪除其所有的購物車項目（清空購物車）。
   */
  void deleteByUser_UserId(Integer userId);

  List<CartItem> findByUser_UserIdAndProduct_ProductIdIn(Integer userId, List<Integer> productIds);

  void deleteByUser_UserIdAndProduct_ProductIdIn(Integer userId, List<Integer> productIds);
}
