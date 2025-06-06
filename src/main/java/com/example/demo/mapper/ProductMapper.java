package com.example.demo.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.dto.products.ProductAddDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductImageDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.products.ProductTransactionDetailDto;
import com.example.demo.model.dto.products.ProductTransactionDetailInputDto;
import com.example.demo.model.dto.products.ProductUpdateDto;
import com.example.demo.model.entity.Category;
import com.example.demo.model.entity.Product;
import com.example.demo.model.entity.ProductContent;
import com.example.demo.model.entity.ProductImage;
import com.example.demo.model.entity.ProductTransactionDetail;
import com.example.demo.model.entity.TransactionMethod;
import com.example.demo.model.entity.User;

public class ProductMapper {

	@Autowired
	private ModelMapper modelMapper;

	private TransactionMethodRepository transactionMethodRepository;

	// 將 Product 實體轉換為 ProductDto (用於讀取產品詳情)
	public ProductDto toDto(Product product) {
		if (product == null) {
			return null;
		}

		ProductDto dto = modelMapper.map(product, ProductDto.class);

		if (product.getSellerUser() != null) {
			dto.setSellerUserId(product.getSellerUser().getUserId());
			dto.setSellerUsername(product.getSellerUser().getUsername());
		}

		if (product.getCategory() != null) {
			dto.setCategoryId(product.getCategory().getCategoryId());
			dto.setCategoryName(product.getCategory().getCategoryName());
		}
		// 從 ProductContent 實體獲取內容
		if (product.getProductContent() != null) {
			dto.setTitle(product.getProductContent().getTitle());
			dto.setShortDescription(product.getProductContent().getShortDescription());
			dto.setFullDescription(product.getProductContent().getFullDescription());
		}
		// 從 ProductImage 實體獲取 Base64 字串
		if (product.getProductImages() != null) {
			dto.setImageBases64(
					product.getProductImages().stream().map(ProductImage::getImageBase64).collect(Collectors.toList()));
		} else {
			dto.setImageBases64(Collections.emptyList()); // 避免 null
		}
		// 從 ProductTransactionDetail 實體獲取交易明細
		if (product.getTransactionDetails() != null) {
			dto.setTransactionDetails(product.getTransactionDetails().stream().map(this::toProductTransactionDetailDto)
					.collect(Collectors.toList()));
		} else {
			dto.setTransactionDetails(Collections.emptyList()); // 避免 null
		}
		return dto;
	}

	// 將 Product 實體轉換為 ProductSummaryDto (用於列表頁，只包含預覽圖)

	public ProductSummaryDto toProductSummaryDto(Product product) {

		if (product == null) {
			return null;
		}
		ProductSummaryDto dto = modelMapper.map(product, ProductSummaryDto.class);

		if (product.getSellerUser() != null) {
			dto.setSellerUserId(product.getSellerUser().getUserId());
			dto.setSellerUsername(product.getSellerUser().getUsername());
		}

		if (product.getCategory() != null) {
			dto.setCategoryId(product.getCategory().getCategoryId());
			dto.setCatrgoryName(product.getCategory().getCategoryName());
		}
		if (product.getProductContent() != null) {
			dto.setTitle(product.getProductContent().getTitle());
			dto.setShortDescription(product.getProductContent().getShortDescription());
		}
		// 只設定第一張圖片的 Base64 字串作為預覽
		if (product.getProductImages() != null) {
			dto.setFirstBase64Image(product.getProductImages().get(0).getImageBase64());
		} else {
			dto.setFirstBase64Image(null);// 或者一個預設的 Base64 佔位圖
		}

		return dto;
	}

	// 將 ProductTransactionDetail 實體轉換為 ProductTransactionDetailDto
	public ProductTransactionDetailDto toProductTransactionDetailDto(ProductTransactionDetail detail) {
		if (detail == null)
			return null;
		ProductTransactionDetailDto dto = modelMapper.map(detail, ProductTransactionDetailDto.class);
		if (detail.getTransactionMethod() != null) {
			dto.setMethodId(detail.getTransactionMethod().getMethodId());
			dto.setMethodName(detail.getTransactionMethod().getName());
			dto.setMethodDescription(detail.getTransactionMethod().getDescription());
		}
		return dto;
	}

	// 將 ProductAddDto 轉換為 Product 實體
	public Product addDtoToEntity(ProductAddDto dto, User seller, Category category) {
		Product product = new Product();
		product.setSellerUser(seller);
		product.setCategory(category);
		product.setPrice(dto.getPrice());
		product.setQuantity(dto.getQuantity());
		// 狀態在 Product 實體的 @PrePersist 中預設為 'For_Sale'

		// 產品內容
		ProductContent content = modelMapper.map(dto.getContent(), ProductContent.class);
		content.setProduct(product); // 建立雙向關聯
		product.setProductContent(content);// 設定 Product 的 productContent

		if (dto.getImages() != null) {
			for (ProductImageDto imageDto : dto.getImages()) {
				ProductImage img = new ProductImage();
				img.setImageBase64(imageDto.getImageBase64()); // 設定 Base64 字串
				img.setProduct(product);// 建立雙向關聯
				product.getProductImages().add(img);
			}
		}

		// 產品交易明細
		if (dto.getTransactionDetails() != null) {
			for (ProductTransactionDetailInputDto detailDto : dto.getTransactionDetails()) {
				TransactionMethod method = transactionMethodRepository.findById(detailDto.getMethodId())
						.orElseThrow(() -> new IllegalArgumentException("無效的交易方式 ID: " + detailDto.getMethodId()));
				ProductTransactionDetail detail = new ProductTransactionDetail();
				modelMapper.map(detailDto, detail); // 將 DTO 的值映射到實體
				detail.setTransactionMethod(method);
				detail.setProduct(product);// 建立雙向關聯
				product.getTransactionDetails().add(detail);
			}
		}
		return product;
	}

	// 使用 ProductUpdateDto 更新現有的 Product 實體
	public void updateEntityFromDto(Product product, ProductUpdateDto dto, Category category) {
		// 更新基本欄位 (如果 DTO 中有提供)
		if (dto.getPrice() != null)
			product.setPrice(dto.getPrice());
		if (dto.getQuantity() != null)
			product.setQuantity(dto.getQuantity());
		if (dto.getStatus() != null)
			product.setStatus(dto.getStatus());
		if (category != null)
			product.setCategory(category);
		// 如果 DTO 中有 categoryId 並且已獲取 category 實體

		// 更新產品內容
		if (dto.getContent() != null) {
			ProductContent content = product.getProductContent();
			// ProductContent 應該總是存在於一個格式正確的 Product 中 (因為 Product 的 optional=false)
			if (content == null) {
				content = new ProductContent();
				content.setProduct(product);
				product.setProductContent(content);
			}
			if (dto.getContent().getTitle() != null)
				content.setTitle(dto.getContent().getTitle());

			if (dto.getContent().getShortDescription() != null)
				content.setShortDescription(dto.getContent().getShortDescription());

			// 如果 DTO 中提供，完整描述可以設置為 null 或空字串
			// 注意：這裡如果傳入 null 會清空 fullDescription

			if (dto.getContent().getFullDescription() != null)
				content.setFullDescription(dto.getContent().getFullDescription());

		}
		// 更新產品圖片 (完全替換)
		// 只有當前端明確提供 images 列表時才更新 (即使是空列表也代表清空)

		if (dto.getImages() != null) {
			product.getProductImages().clear();
			for (ProductImageDto imageDto : dto.getImages()) {
				ProductImage img = new ProductImage();
				img.setImageBase64(imageDto.getImageBase64());
				img.setProduct(product);
				product.getProductImages().add(img);
			}

			// 更新產品交易明細 (完全替換)
			// 只有當前端明確提供 transactionDetails 列表時才更新 (即使是空列表也代表清空)

			if (dto.getTransactionDetails() != null) {
				product.getTransactionDetails().clear();
				for (ProductTransactionDetailInputDto detailDto : dto.getTransactionDetails()) {
					TransactionMethod method = transactionMethodRepository.findById(detailDto.getMethodId())
							.orElseThrow(() -> new IllegalArgumentException("無效的交易方式 ID" + detailDto.getMethodId()));
					ProductTransactionDetail detail = new ProductTransactionDetail();
					modelMapper.map(detailDto, detail); // 將 DTO 的值映射到實體
					detail.setTransactionMethod(method);
					detail.setProduct(product);
					product.getTransactionDetails().add(detail);

				}

			}

		}

	}

}
