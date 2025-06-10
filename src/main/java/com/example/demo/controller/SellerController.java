package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.CategoryNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.products.ProductAddDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.ProductService;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = { "/seller" })
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class SellerController {

	@Autowired
	private ProductService productService;

	@Autowired
	private TransactionService transactionService;

	// ==========================================================
	// 刊登/管理商品
	// ==========================================================

	@GetMapping("/products")
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getMyProductsAsSeller(
			@RequestParam(required = false) ProductStatus status, HttpSession session) {
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		if (userCert == null) {
			return unauthorizedGenericResponse("未授權或未登入，請重新登入");
		}
		try {
			List<ProductSummaryDto> products;
			if (status != null) {
				products = productService.getProductsBySellerAndStatus(userCert.getUserId(), status);
			} else {
				products = productService.getAllProductsBySeller(userCert.getUserId());// 獲取該賣家的所有商品

			}
			if (products.isEmpty()) {
				return ResponseEntity.ok(ApiResponse.success("您尚未刊登任何商品或無符合條件的商品", products));
			}
			return ResponseEntity.ok(ApiResponse.success("查詢您刊登的商品成功", products));
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
		}
	}

	@PostMapping("/products")
	public ResponseEntity<ApiResponse<ProductDto>> addproductBySeller(@Valid @RequestBody ProductAddDto addDto,
			HttpSession session) {

		UserCert userCert = (UserCert) session.getAttribute("userCert");

		if (userCert == null) {
			return unauthorizedGenericResponse("未授權或未登入，請重新登入");
		}
		try {
			ProductDto newProduct = productService.addProduct(addDto, userCert.getUserId());
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("商品刊登成功", newProduct));

		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
		} catch (CategoryNotFoundException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "刊登失敗：" + e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "刊登失敗：" + e.getMessage()));
		}

	}
	@GetMapping("/products/{productId}")
  public ResponseEntity<ApiResponse<ProductDto>> getMyProductDetailsAsSeller(
  	@PathVariable Integer productId,HttpSession session	){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		if(userCert ==null) {
			return unauthorizedGenericResponse("未授權或未登入，請重新登入");
		}
		try {
			ProductDto product = productService.getProductByIdForOwnerOrAdmin(productId, userCert.getUserId(), userCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("查詢商品詳情成功", product));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private ResponseEntity<ApiResponse<Void>> unauthorizedResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}

	private <T> ResponseEntity<ApiResponse<T>> unauthorizedGenericResponse(String message) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}

}
