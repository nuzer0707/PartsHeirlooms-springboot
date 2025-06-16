package com.example.demo.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.CategoryNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.model.dto.products.ProductAddDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.products.ProductUpdateDto;
import com.example.demo.model.entity.Category;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getAllPublicProduct() {
		return productRepository.findByStatus(ProductStatus.For_Sale)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getAllProductsFotAdmin() {
		return productRepository.findAll()
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProuctById(Integer productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(()->new ProductNotFoundException("找不到產品，ID: "+productId));
		if(product.getStatus()!=ProductStatus.For_Sale) {
			throw new ProductNotFoundException("產品 ID: " + productId + " 目前不可見或已售出");
		}
		return productMapper.toDto(product);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProductByIdForOwnerOrAdmin(Integer productId, Integer requestingUserId,
			UserRole requestingUserRole) {
		Product product = productRepository.findById(productId)
				.orElseThrow(()->new ProductNotFoundException("找不到產品 ID: " + productId));
		
		boolean isAdmin = UserRole.ADMIN.equals(requestingUserRole);
		boolean isOwner = product.getSellerUser().getUserId().equals(requestingUserId);
		
		if(!isAdmin && !isOwner) {
			 throw new AccessDeniedException("您無權查看此產品的詳細資訊");
		}
		return productMapper.toDto(product);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getProductsBySeller(Integer sellerUserId) throws UserNotFoundException{
		if(!userRepository.existsById(sellerUserId)) {
			 throw new UserNotFoundException("找不到賣家，ID: " + sellerUserId);
		}
		return productRepository.findBySellerUser_UserIdAndStatus(sellerUserId, ProductStatus.For_Sale)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getProductsByCategory(Integer categoryId) {
		if(!categoryRepository.existsById(categoryId)) {
			throw new CategoryNotFoundException("找不到分類 ID: " + categoryId);
		}
		return productRepository.findByCategory_CategoryIdAndStatus(categoryId, ProductStatus.For_Sale)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> findProductsByTitle(String keyword) {
		return productRepository.findByTitleContainingIgnoreCase(keyword)
				.stream()
				.filter(p->p.getStatus()==ProductStatus.For_Sale)
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}
	
	@Override
    @Transactional(readOnly = true)
    public List<ProductSummaryDto> findProductsByTitleOrCategoryName(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        List<Product> products = productRepository.findByTitleOrCategoryNameContainingIgnoreCaseAndStatusForSale(keyword);
        return products.stream()
              .map(productMapper::toSummaryDto)
              .collect(Collectors.toList());
    }
	
	@Override
	@Transactional
	public ProductDto addProduct(ProductAddDto addDto, Integer sellerUserId) throws UserNotFoundException {
		User seller = userRepository.findById(sellerUserId)
				.orElseThrow(()-> new UserNotFoundException("找不到賣家 ID: " + sellerUserId));
		if(seller.getPrimaryRole()!=UserRole.SELLER && seller.getPrimaryRole()!=UserRole.ADMIN) {
		    throw new AccessDeniedException("只有賣家或管理員可以刊登產品");	
		}
		
		Category category = categoryRepository.findById(addDto.getCategoryId())
				.orElseThrow(()-> new CategoryNotFoundException("找不到分類 ID: " + addDto.getCategoryId()));
		try {
			Product product = productMapper.addDtoToEntity(addDto, seller, category);
			Product saveProduct = productRepository.save(product);
			return productMapper.toDto(saveProduct);
		} catch (IllegalArgumentException e) {
			throw new ProductOperationException("創建產品失敗: " + e.getMessage());
		}catch (Exception e) {
			throw new ProductOperationException("創建產品時發生未知錯誤，請稍後再試: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public ProductDto updateProduct(Integer productId, ProductUpdateDto updateDto, Integer currentUserId,
			UserRole currentUserRole) {
		Product product = productRepository.findById(productId)
				.orElseThrow(()->new ProductNotFoundException("找不到要更新的產品 ID: " + productId));
		
		if(!product.getSellerUser().getUserId().equals(currentUserId) && currentUserRole != UserRole.ADMIN) {
			throw new AccessDeniedException("您無權修改此產品");
		}
		
		if(currentUserRole != UserRole.ADMIN && product.getSellerUser().getUserId().equals(currentUserId)) {
			if(updateDto.getStatus()!=null &&
			   updateDto.getStatus() != ProductStatus.For_Sale &&
			   updateDto.getStatus() != ProductStatus.Removed) {
				throw new AccessDeniedException("賣家只能將產品狀態設置為 'For_Sale' (待售) 或 'Removed' (已下架)");
			}
			
			if(updateDto.getStatus() == ProductStatus.Sold) {
				throw new AccessDeniedException("產品狀態不能由賣家直接設置為 'Sold' (已售出)");
			}
		}

        // 業務邏輯檢查：如果前端試圖修改交易方式，則檢查商品是否有活躍訂單
		if (updateDto.getTransactionDetails() != null) {
			// 1. 定義哪些是「活躍」的訂單狀態
			List<TransactionStatus> activeStatuses = Arrays.asList(
				TransactionStatus.Pending_Payment,
				TransactionStatus.Paid,
				TransactionStatus.Processing,
				TransactionStatus.Shipped
			);

			// 2. 使用新的 repository 方法進行檢查
			boolean hasActiveTransactions = transactionRepository.existsByProductId_ProductIdAndStatusIn(productId, activeStatuses);

			// 3. 如果存在活躍訂單，則禁止修改交易方式
			if (hasActiveTransactions) {
				throw new ProductOperationException("更新失敗：此商品尚有未完成的訂單，無法修改其交易方式。");
			}
		}
		
		Category category = null;
		if(updateDto.getCategoryId() != null) {
			category = categoryRepository.findById(updateDto.getCategoryId())
					.orElseThrow(()->new CategoryNotFoundException("找不到分類 ID: "+ updateDto.getCategoryId()));
		}
		
		try {
			productMapper.updateEntityFromDto(product, updateDto, category);
			Product updatedProduct = productRepository.save(product);
			return productMapper.toDto(updatedProduct);
		} catch (IllegalArgumentException e) {
			throw new ProductOperationException("更新產品失敗: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
            throw new ProductOperationException("更新產品時發生未知錯誤，請稍後再試: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public void deleteProduct(Integer productId, Integer currentUserId, UserRole currentUserRole) {
		Product product = productRepository.findById(productId)
						.orElseThrow(()->new ProductNotFoundException("找不到要刪除的產品 ID:" + productId));
		
		if(!product.getSellerUser().getUserId().equals(currentUserId) && currentUserRole != UserRole.ADMIN) {
			throw new AccessDeniedException("您無權刪除此產品");
		}
		
        // 業務邏輯檢查：如果商品已存在任何交易中，則不允許刪除
        if (transactionRepository.existsByProductId_ProductId(productId)) {
            throw new ProductOperationException("刪除失敗：此商品已有訂單紀錄，無法刪除。您可以考慮將其下架。");
        }

		try {
			productRepository.delete(product);
		} catch (Exception e) {
			throw new ProductOperationException("刪除產品時發生未知錯誤，請稍後再試"+e.getMessage());
		}
	}

	@Override
	@Transactional
	public ProductDto updateProductStatus(Integer productId, ProductStatus newStatus, Integer currentUserId,
			UserRole currentUserRole) {
		Product product = productRepository.findById(productId)
						.orElseThrow(()->new ProductNotFoundException("找不到要更新狀態的產品 ID:" + productId));
		
		boolean isAdmin = UserRole.ADMIN.equals(currentUserRole);
		boolean isOwner = product.getSellerUser().getUserId().equals(currentUserId);
		
		if(!isAdmin && !isOwner) {
			throw new AccessDeniedException("您無權修改此產品狀態");
		}
		
		if(isOwner && !isAdmin) { // 這裡修正了邏輯，應該是 isOwner 且非 Admin
			if(newStatus != ProductStatus.Removed && newStatus != ProductStatus.For_Sale) {
				throw new AccessDeniedException("賣家只能將產品狀態設置為 'Removed' (已下架) 或 'For_Sale' (待售)");	
			}
			if(newStatus == ProductStatus.Sold) {
				throw new AccessDeniedException("產品狀態不能由賣家直接設置為 'Sold' (已售出)，此狀態應由交易流程管理");
			}
		}

		product.setStatus(newStatus);
		Product updateProduct = productRepository.save(product);
		
		return productMapper.toDto(updateProduct);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getAllProductsBySeller(Integer sellerUserId) throws UserNotFoundException {
		if (!userRepository.existsById(sellerUserId)) {
            throw new UserNotFoundException("找不到賣家，ID: " + sellerUserId);
		}
		return productRepository.findBySellerUser_UserId(sellerUserId)
					.stream()
					.map(productMapper::toSummaryDto)
					.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getProductsBySellerAndStatus(Integer sellerUserId, ProductStatus status)
			throws UserNotFoundException {
		if (!userRepository.existsById(sellerUserId)) {
            throw new UserNotFoundException("找不到賣家，ID: " + sellerUserId);
        }
		return productRepository.findBySellerUser_UserIdAndStatus(sellerUserId, status)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}
}