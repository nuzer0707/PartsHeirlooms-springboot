package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
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
	
	
	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getAllPublicProduct() {
		// 回傳類型改為 ProductSummaryDto
		return productRepository.findByStatus(ProductStatus.For_Sale)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getAllProductsFotAdmin() {
		// 回傳類型改為 ProductSummaryDto		
		// 管理員可以查看所有狀態的產品
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
		// 公開查詢時，只顯示 For_Sale 狀態的產品
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
		
		boolean isAdmin =UserRole.ADMIN.equals(requestingUserRole);
		// 檢查產品的賣家 ID 是否與請求用戶 ID 相同
		boolean isOwner = product.getSellerUser().getUserId().equals(requestingUserId);
		
		if(!isAdmin && !isOwner) {
			// 如果不是管理員也不是擁有者，則遵循公開可見性規則 (例如，必須是 For_Sale)
			 throw new AccessDeniedException("您無權查看此產品的詳細資訊");
		}
		// 管理員或擁有者可以查看任何狀態的產品，或者產品是公開可見的
		return productMapper.toDto(product);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getProductsBySeller(Integer sellerUserId) throws UserNotFoundException{
		// UserNotFoundException 繼承自 CertException，這裡是兼容的
		if(!userRepository.existsById(sellerUserId)) {
			 throw new UserNotFoundException("找不到賣家，ID: " + sellerUserId);
		}
		// 預設只顯示 For_Sale 狀態
		return productRepository.findBySellerUser_UserIdAndStatus(sellerUserId, ProductStatus.For_Sale)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> getProductsByCategory(Integer categoryId) {
		// 注意：您的 CategoryNotFoundException 繼承自 CategoryException，這裡是兼容的
		if(!categoryRepository.existsById(categoryId)) {
			throw new CategoryNotFoundException("找不到分類 ID: " + categoryId);
		}
		// 預設只顯示 For_Sale 狀態。
		return productRepository.findByCategory_CategoryIdAndStatus(categoryId, ProductStatus.For_Sale)
				.stream()
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductSummaryDto> findProductsByTitle(String keywork) {
		// 回傳類型改為 ProductSummaryDto
		return productRepository.findByTitleContainingIgnoreCase(keywork)
				.stream()
				// 公開搜索時僅顯示 'For_Sale' 狀態的產品
				.filter(p->p.getStatus()==ProductStatus.For_Sale)
				.map(productMapper::toSummaryDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public ProductDto addProduct(ProductAddDto addDto, Integer sellerUserId) throws UserNotFoundException {
		User seller = userRepository.findById(sellerUserId)
				.orElseThrow(()-> new UserNotFoundException("找不到賣家 ID: " + sellerUserId));
		// 額外檢查：只有 SELLER 或 ADMIN 可以創建產品
		if(seller.getPrimaryRole()!=UserRole.SELLER&&seller.getPrimaryRole()!=UserRole.ADMIN) {
		throw new AccessDeniedException("只有賣家或管理員可以刊登產品");	
		}
		
		 // TODO: 如果有賣家驗證流程 (SellerVerification)，這裡應檢查賣家是否已通過驗證
		Category category = categoryRepository.findById(addDto.getCategoryId())
				.orElseThrow(()-> new CategoryNotFoundException("找不到分類 ID: " + addDto.getCategoryId()));
		try {
			Product product = productMapper.addDtoToEntity(addDto, seller, category);
			Product saveProduct = productRepository.save(product);
			return productMapper.toDto(saveProduct);
		} catch (IllegalArgumentException e) {
			throw new ProductNotFoundException("創建產品失敗: "+ e.getMessage());
		}catch (Exception e) {
			// 記錄更詳細的錯誤日誌
            // logger.error("創建產品時發生未知錯誤", e); 

			throw new ProductNotFoundException("創建產品時發生未知錯誤，請稍後再試"+e.getMessage());
		}
		
		
	}

	@Override
	@Transactional
	public ProductDto updateProduct(Integer productId, ProductUpdateDto updateDto, Integer currentUserId,
			UserRole currentUserRole) {
		Product product =productRepository.findById(productId)
				.orElseThrow(()->new ProductNotFoundException("找不到要更新的產品 ID: " + productId));
		// 授權：只有賣家或 ADMIN 可以更新
		if(!product.getSellerUser().getUserId().equals(currentUserId)&&currentUserRole != UserRole.ADMIN) {
			throw new AccessDeniedException("您無權修改此產品");
		}
		//如果當前用戶是賣家但不是管理員，他們不能將狀態更改為 'Removed' 或 'For_Sale' 以外的狀態
		if(currentUserRole != UserRole.ADMIN && product.getSellerUser().getUserId().equals(currentUserId)) {
			if(updateDto.getStatus()!=null &&
			   updateDto.getStatus() !=ProductStatus.For_Sale &&
			   updateDto.getStatus() !=ProductStatus.Removed
			) {
				throw new AccessDeniedException("賣家只能將產品狀態設置為 'For_Sale' (待售) 或 'Removed' (已下架)");
			}
			
			// 防止賣家直接將狀態設置為 'Sold'，這應該是交易流程的一部分
			if(updateDto.getStatus()==ProductStatus.Sold) {
				throw new AccessDeniedException("產品狀態不能由賣家直接設置為 'Sold' (已售出)");
			}
			
		}
		Category category =null;
		if(updateDto.getCategoryId()!=null) {
			category =categoryRepository.findById(updateDto.getCategoryId())
					.orElseThrow(()->new CategoryNotFoundException("找不到分類 ID: "+ updateDto.getCategoryId()));
		}
		
		try {
			productMapper.updateEntityFromDto(product, updateDto, category);
			Product updatedProduct = productRepository.save(product);
			return productMapper.toDto(updatedProduct);
		} catch (IllegalArgumentException e) {
			throw new ProductOperationException("更新產品失敗: " + e.getMessage());
		}catch (Exception e) {
			throw new ProductOperationException("更新產品時發生未知錯誤，請稍後再試"+ e.getMessage());
		}
		
	}

	@Override
	@Transactional
	public void deleteProduct(Integer productId, Integer currentUserId, UserRole currentUserRole) {
		Product product =productRepository.findById(productId)
						.orElseThrow(()->new ProductNotFoundException("找不到要刪除的產品 ID:"+productId));
		 // 授權：只有賣家或 ADMIN 可以刪除
		if(!product.getSellerUser().getUserId().equals(currentUserId)&&currentUserRole!=UserRole.ADMIN) {
			throw new AccessDeniedException("您無權刪除此產品");
		}
		// 考慮是否要軟刪除 (例如，將狀態更改為 "DELETED") 而不是直接刪除。
        // 目前為了簡單起見，允許擁有者或管理員刪除
		try {
			productRepository.delete(product);
		} catch (Exception e) {
			// logger.error("刪除產品時發生未知錯誤", e);
			throw new ProductOperationException("刪除產品時發生未知錯誤，請稍後再試"+e.getMessage());
		}
		
	}

	@Override
	@Transactional
	public ProductDto updateProductStatus(Integer productId, ProductStatus newStatus, Integer currentUserId,
			UserRole currentUserRole) {
		Product product = productRepository.findById(productId)
						.orElseThrow(()->new ProductNotFoundException("找不到要更新狀態的產品 ID:"+productId));
		boolean isAmin = UserRole.ADMIN.equals(currentUserRole);
		boolean isOwner = product.getSellerUser().getUserId().equals(currentUserId);
		
		if(!isAmin && !isOwner) {
			throw new AccessDeniedException("您無權修改此產品狀態");
		}
		
		// 賣家特定的狀態更改規則
		if(isOwner && isAmin) {
			// 賣家只能將狀態改為 Removed 或 For_Sale (假設賣家可以重新上架未售出的已下架商品)
			if(newStatus != ProductStatus.Removed && newStatus != ProductStatus.For_Sale) {
				throw new AccessDeniedException("賣家只能將產品狀態設置為 'Removed' (已下架) 或 'For_Sale' (待售)");	
			}
			// 防止賣家直接將狀態設置為 'Sold'
			if(newStatus != ProductStatus.Sold) {
				throw new AccessDeniedException("產品狀態不能由賣家直接設置為 'Sold' (已售出) 此狀態應由交易流程管理");
			}
		}
	    // 管理員可以設置任何狀態
        // 擁有者可以在限制範圍內設置為 For_Sale 或 Removed
		product.setStatus(newStatus);
		Product updateProduct = productRepository.save(product);
		
		return productMapper.toDto(updateProduct);
		
		
	}

}
