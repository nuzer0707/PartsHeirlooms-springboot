package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.CertException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.users.UserAddDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.model.dto.users.UserDto;
import com.example.demo.model.dto.users.UserUpdateDto;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8002" }, allowCredentials = "true")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	// ==========================================================
	//                         用戶管理
	// ==========================================================

	// 取得 BUYER, SELLER, BLACK 的所有帳號資料
	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<UserDto>>> getManagrdUser() {
		List<UserRole> rolesToFetch = Arrays.asList(UserRole.BUYER, UserRole.SELLER, UserRole.BLACK);
		List<UserDto> users = userService.findUsersByRoles(rolesToFetch);
		if (users.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無符合條件的使用者資料", users));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", users));
	}

	// ADMIN 新增使用者
	@PostMapping("/users")
	public ResponseEntity<ApiResponse<UserDto>> addUserByAdmin(@Valid @RequestBody UserAddDto userAddDto) {
		try {
			UserDto addUser = userService.addUserByAdmin(userAddDto);
			// 理想情況下，URI 應該指向新資源 /admin/users/{userId}
			// URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
			// .buildAndExpand(createdUser.getUserId()).toUri();
			// return ResponseEntity.created(location).body(ApiResponse.success("使用者新增成功",
			// createdUser));
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("使用者新增成功", addUser));
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), "新增失敗：" + e.getMessage()));
		} catch (CertException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "新增失敗：" + e.getMessage()));
		}

	}

	// ADMIN 修改使用者
	@PutMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> updateUserByAdmin(@PathVariable Integer userId,
			@Valid @RequestBody UserUpdateDto updateDto) {
		try {
			UserDto updateUser = userService.updateUserByAdmin(userId, updateDto);
			return ResponseEntity.ok(ApiResponse.success("使用者更新成功", updateUser));

		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage()));
		} catch (CertException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "更新失敗" + e.getMessage()));
		}
	}

	// ADMIN 刪除使用者
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<Integer>> deleteUserByAdmin(@PathVariable Integer userId) {
		try {
			userService.deleteUserByAdmin(userId);
			return ResponseEntity.ok(ApiResponse.success("使用者" + userId + "成功刪除", userId));

		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "刪除失敗" + e.getMessage()));
		} catch (CertException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "刪除失敗" + e.getMessage()));
		}

	}

	// ==========================================================
	//                         產品管理
	// ==========================================================
	
	@GetMapping("/product")
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getAllProductsForAdmin() {

		List<ProductSummaryDto> products = productService.getAllProductsFotAdmin();

		if (products.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無任何商品資料", products));
		}

		return ResponseEntity.ok(ApiResponse.success("查詢所有商品成功", products));

	}

	@PutMapping("/product/{productId}/status")
	public ResponseEntity<ApiResponse<ProductDto>> updateAnyProductStatus(@PathVariable Integer productId,
			@RequestParam ProductStatus status, HttpSession session) {

		UserCert adminCert = (UserCert) session.getAttribute("userCert");
		try {
			ProductDto updatedProduct = productService.updateProductStatus(productId, status, adminCert.getUserId(),
					adminCert.getPrimaryRole());

			return ResponseEntity.ok(ApiResponse.success("產品狀態已由管理員更新成功", updatedProduct));

		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}
	}

	@DeleteMapping("/products/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteAnyProduct(@PathVariable Integer productId, HttpSession session) {
		UserCert adminCert = (UserCert) session.getAttribute("userCert");
		try {

			productService.deleteProduct(productId, adminCert.getUserId(), adminCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("產品 " + productId + " 已由管理員成功刪除", null));

		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}
	}
	
	// ==========================================================
	//               		  賣家資格審核管理
	// ==========================================================
	
	@GetMapping("/reports")
	public ResponseEntity<ApiResponse<Object>> getAllReports(){
		
		return ResponseEntity.ok(ApiResponse.success("所有報告查詢成功", null));
	}
	
	@PutMapping()
	public ResponseEntity<ApiResponse<Void>> updateReportStatus(
		@PathVariable Integer reportId,@RequestParam String newStatus ){
		
		return ResponseEntity.ok(ApiResponse.success("報告狀態已更新", null));
		
	}
	
	// ==========================================================
	//                       評分管理
	// ==========================================================
	
	
	
	
}
