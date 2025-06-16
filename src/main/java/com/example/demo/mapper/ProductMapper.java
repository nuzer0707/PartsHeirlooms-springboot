package com.example.demo.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.repository.TransactionMethodRepository;

@Component
public class ProductMapper {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
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
		if (product.getProductContent() != null) {
			dto.setTitle(product.getProductContent().getTitle());
			dto.setShortDescription(product.getProductContent().getShortDescription());
			dto.setFullDescription(product.getProductContent().getFullDescription());
		}
		if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
			dto.setImageBases64(
					product.getProductImages().stream().map(ProductImage::getImageBase64).collect(Collectors.toList()));
		} else {
			dto.setImageBases64(Collections.emptyList());
		}
		if (product.getTransactionDetails() != null) {
			dto.setTransactionDetails(product.getTransactionDetails().stream().map(this::toProductTransactionDetailDto)
					.collect(Collectors.toList()));
		} else {
			dto.setTransactionDetails(Collections.emptyList());
		}
		return dto;
	}

	// 將 Product 實體轉換為 ProductSummaryDto (用於列表頁，只包含預覽圖)
	public ProductSummaryDto toSummaryDto(Product product) {
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
			dto.setCategoryName(product.getCategory().getCategoryName());
		}
		if (product.getProductContent() != null) {
			dto.setTitle(product.getProductContent().getTitle());
			dto.setShortDescription(product.getProductContent().getShortDescription());
		}
		if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
			dto.setFirstBase64Image(product.getProductImages().get(0).getImageBase64());
		} else {
			dto.setFirstBase64Image(null);
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
		product.setStatus(ProductStatus.For_Sale);
		
		ProductContent content = modelMapper.map(dto.getContent(), ProductContent.class);
		content.setProduct(product);
		product.setProductContent(content);

		if (dto.getImages() != null) {
			dto.getImages().forEach(imageDto -> {
				ProductImage img = new ProductImage();
				img.setImageBase64(imageDto.getImageBase64());
				img.setProduct(product);
				product.getProductImages().add(img);
			});
		}

		if (dto.getTransactionDetails() != null) {
			 dto.getTransactionDetails().forEach(detailDto -> {
                 TransactionMethod method = transactionMethodRepository.findById(detailDto.getMethodId())
                         .orElseThrow(() -> new IllegalArgumentException("無效的交易方式 ID: " + detailDto.getMethodId()));
                 ProductTransactionDetail detail = new ProductTransactionDetail();
                 detail.setMeetupTime(detailDto.getMeetupTime());
                 detail.setMeetupLatitude(detailDto.getMeetupLatitude());
                 detail.setMeetupLongitude(detailDto.getMeetupLongitude());
                 detail.setGeneralNotes(detailDto.getGeneralNotes()); // 假設 DTO 中欄位名已修正
                 detail.setTransactionMethod(method);
                 detail.setProduct(product);
                 product.getTransactionDetails().add(detail);
			});
		}
		return product;
	}

	// 使用 ProductUpdateDto 更新現有的 Product 實體
	public void updateEntityFromDto(Product product, ProductUpdateDto dto, Category category) {
		// --- 1. 更新可以安全修改的欄位 ---
		if (dto.getPrice() != null) product.setPrice(dto.getPrice());
		if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());
		if (dto.getStatus() != null) product.setStatus(dto.getStatus());
		if (category != null) product.setCategory(category);

		// --- 2. 更新產品內容 ---
		if (dto.getContent() != null) {
			ProductContent content = product.getProductContent();
			if (content == null) { // 防禦性程式設計
				content = new ProductContent();
				content.setProduct(product);
				product.setProductContent(content);
			}
			if (dto.getContent().getTitle() != null) content.setTitle(dto.getContent().getTitle());
			if (dto.getContent().getShortDescription() != null) content.setShortDescription(dto.getContent().getShortDescription());
			if (dto.getContent().getFullDescription() != null) content.setFullDescription(dto.getContent().getFullDescription());
		}

		// --- 3. 更新圖片 (完全替換) ---
		if (dto.getImages() != null) {
			product.getProductImages().clear();
			dto.getImages().forEach(imageDto -> {
				ProductImage img = new ProductImage();
				img.setImageBase64(imageDto.getImageBase64());
				img.setProduct(product);
				product.getProductImages().add(img);
			});
		}
			 
		// --- 4. 更新交易明細 (完全替換) ---
        // Mapper 只負責轉換，不進行業務判斷。業務判斷已在 Service 層完成。
		if (dto.getTransactionDetails() != null) {
			product.getTransactionDetails().clear();
			dto.getTransactionDetails().forEach(detailDto -> {
				TransactionMethod method = transactionMethodRepository.findById(detailDto.getMethodId())
						.orElseThrow(() -> new IllegalArgumentException("無效的交易方式 ID: " + detailDto.getMethodId()));
				
				ProductTransactionDetail newDetail = new ProductTransactionDetail();
				
				newDetail.setGeneralNotes(detailDto.getGeneralNotes());
				newDetail.setMeetupTime(detailDto.getMeetupTime());
                newDetail.setMeetupLatitude(detailDto.getMeetupLatitude());
                newDetail.setMeetupLongitude(detailDto.getMeetupLongitude());
				
				newDetail.setTransactionMethod(method);
				newDetail.setProduct(product);          
				
				product.getTransactionDetails().add(newDetail); 
			});
		}
	}
}