package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.CategoryException;
import com.example.demo.exception.CertException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.CategoryDto;
import com.example.demo.model.dto.IssueReportDto;
import com.example.demo.model.dto.PlatformOverviewDto;
import com.example.demo.model.dto.RatingDto;
import com.example.demo.model.dto.SalesReportDto;
import com.example.demo.model.dto.SellerVerificationDto;
import com.example.demo.model.dto.SellerVerificationReviewDto;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.products.ProductDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.users.UserAddDto;
import com.example.demo.model.dto.users.UserCert;
import com.example.demo.model.dto.users.UserDto;
import com.example.demo.model.dto.users.UserUpdateDto;
import com.example.demo.model.entity.enums.IssueStatus;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CategoryService;
import com.example.demo.service.IssueReportService;
import com.example.demo.service.ProductService;
import com.example.demo.service.RatingService;
import com.example.demo.service.SellerVerificationService;
import com.example.demo.service.StatisticsService;
import com.example.demo.service.TransactionService;
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

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private SellerVerificationService sellerVerificationService;

	@Autowired
	private IssueReportService issueReportService;

	@Autowired
	private RatingService ratingService;

	@Autowired
	private StatisticsService statisticsService;

	// ==========================================================
	// 用戶管理
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
	// 產品管理
	// ==========================================================

	@GetMapping("/products")
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getAllProductsForAdmin() {

		List<ProductSummaryDto> products = productService.getAllProductsFotAdmin();

		if (products.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無任何商品資料", products));
		}

		return ResponseEntity.ok(ApiResponse.success("查詢所有商品成功", products));

	}

	@PutMapping("/products/{productId}/status")
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
	// 交易管理 (NUEVO)
	// ==========================================================
	@GetMapping("/transactions")
	public ResponseEntity<ApiResponse<List<TransactionDto>>> getAllTransaction() {
		List<TransactionDto> transactionDto = transactionService.getAllTransactionsForAdmin();
		if (transactionDto.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無任何交易資料", transactionDto));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢所有交易成功", transactionDto));
	}

	@GetMapping("/transactions/{transactionId}")
	public ResponseEntity<ApiResponse<TransactionDto>> getTransactionByIdForAdmin(@PathVariable Integer transcationId,
			HttpSession session) {
		UserCert adminCert = (UserCert) session.getAttribute("userCert");
		try {
			TransactionDto transactionDto = transactionService.getTransactionById(transcationId, adminCert.getUserId(),
					adminCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("查詢交易成功", transactionDto));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}

	}

	@PutMapping("/transactions/{transactionId}/status")
	public ResponseEntity<ApiResponse<TransactionDto>> updateTransactionStatusByAdmin(@PathVariable Integer transactionId,
			@RequestParam TransactionStatus status, HttpSession session) {

		UserCert adminCert = (UserCert) session.getAttribute("userCert");
		try {
			TransactionDto updateTransaction = transactionService.updateTransactionsStatus(transactionId, status,
					adminCert.getUserId(), adminCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("交易狀態已由管理員更新成功", updateTransaction));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}

	}

	// ==========================================================
	// 賣家資格審核管理
	// ==========================================================
	@GetMapping("/verifications/pending")
	public ResponseEntity<ApiResponse<List<SellerVerificationDto>>> getPendingVerifications() {
		List<SellerVerificationDto> verificationDtos = sellerVerificationService.getAllPendingVerificationsForAdmin();
		if (verificationDtos.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無待審核的賣家申請", verificationDtos));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢待審核賣家申請成功", verificationDtos));
	}

	@GetMapping("/verifications")
	public ResponseEntity<ApiResponse<List<SellerVerificationDto>>> getAllVerifications() {
		List<SellerVerificationDto> verificationDtos = sellerVerificationService.getAllPendingVerificationsForAdmin();
		if (verificationDtos.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無任何賣家申請紀錄", verificationDtos));
		}

		return ResponseEntity.ok(ApiResponse.success("查詢所有賣家申請紀錄成功", verificationDtos));
	}

	@GetMapping("/verifications/{verificationId}")
	public ResponseEntity<ApiResponse<SellerVerificationDto>> getVerificationById(@PathVariable Integer verificationId) {
		try {
			SellerVerificationDto verificationDto = sellerVerificationService.getVerificationByIdForAdmin(verificationId);
			return ResponseEntity.ok(ApiResponse.success("查詢賣家申請成功", verificationDto));
		} catch (ProductNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		}
	}

	@PutMapping("/verifications/{verificationId}/review")
	public ResponseEntity<ApiResponse<SellerVerificationDto>> reviewSellerVerification(
			@PathVariable Integer verificationId, @Valid @RequestBody SellerVerificationReviewDto reviewDto,
			HttpSession session) {
		UserCert adminCert = (UserCert) session.getAttribute("userCert");
		try {
			SellerVerificationDto updateVerification = sellerVerificationService.reviewVerification(verificationId, reviewDto,
					adminCert.getUserId());
			return ResponseEntity.ok(ApiResponse.success("賣家資格審核完成", updateVerification));
		} catch (ProductNotFoundException e) { // Or VerificationNotFoundException
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (UserNotFoundException | AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		} catch (ProductOperationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		}

	}

	// ==========================================================
	// 檢舉管理 (MODIFICADO/NUEVO)
	// ==========================================================
	@GetMapping("/reports")
	public ResponseEntity<ApiResponse<List<IssueReportDto>>> getAllReportsForAdmin() {
		List<IssueReportDto> reportDtos = issueReportService.getAllReportsForAdmin();
		if (reportDtos.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無任何檢舉報告", reportDtos));
		}
		return ResponseEntity.ok(ApiResponse.success("所有檢舉報告查詢成功", reportDtos));
	}

	@GetMapping("/reports/{reportId}")
	public ResponseEntity<ApiResponse<IssueReportDto>> getReportByIdForAdmin(@PathVariable Integer reportId,
			HttpSession session) {
		UserCert adminCert = (UserCert) session.getAttribute("userCert");

		try {
			IssueReportDto reportDto = issueReportService.getReportById(reportId, adminCert.getUserId(),
					adminCert.getPrimaryRole());
			return ResponseEntity.ok(ApiResponse.success("檢舉報告查詢成功", reportDto));
		} catch (ProductNotFoundException e) { // Or ReportNotFoundException
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}

	}

	@PutMapping("/reports/{reportId}/status")
	public ResponseEntity<ApiResponse<IssueReportDto>> updateReportStatusByAdmin(@PathVariable Integer reportId,
			@RequestParam IssueStatus newStatus, @RequestParam(required = false) String adminRemarks, HttpSession session) {
		UserCert adminCert = (UserCert) session.getAttribute("userCert");

		try {
			IssueReportDto updateReportDto = issueReportService.updateReportStatus(reportId, newStatus, adminRemarks,
					adminCert.getUserId());
			return ResponseEntity.ok(ApiResponse.success("檢舉報告狀態已更新", updateReportDto));
		} catch (ProductNotFoundException e) { // Or ReportNotFoundException
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (UserNotFoundException | AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}

	}

	// ==========================================================
	// 													評分管理
	// ==========================================================
	
	@GetMapping("/ratings")
	public ResponseEntity<ApiResponse<List<RatingDto>>> getAllRatings() {
		List<RatingDto> ratingDtos = ratingService.getAllRatingsForAdmin();
		if (ratingDtos.isEmpty()) {
			return ResponseEntity.ok(ApiResponse.success("查無任何評分資料", ratingDtos));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢所有評分成功", ratingDtos));
	}

	@DeleteMapping("/ratings/{ratingId}")
	public ResponseEntity<ApiResponse<Void>> deleteRating(@PathVariable Integer ratingId, HttpSession session) {
		UserCert adminCert = (UserCert) session.getAttribute("userCert");
		try {
			ratingService.deleteRatingByAdmin(ratingId, adminCert.getUserId());
			return ResponseEntity.ok(ApiResponse.success("評分 " + ratingId + " 已由管理員成功刪除", null));
		} catch (ProductNotFoundException e) { // Or RatingNotFoundException
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
		} catch (UserNotFoundException | AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
		}
	}
	
	// ==========================================================
  //                       統計報表 (NUEVO)
  // ==========================================================
	@GetMapping("/statistics/overview")
	public ResponseEntity<ApiResponse<PlatformOverviewDto>> getPlatformOverview(){
		PlatformOverviewDto overviewDto = statisticsService.getPlatformOverview();
	  return ResponseEntity.ok(ApiResponse.success("平台總覽數據查詢成功", overviewDto));
	}
	
	public ResponseEntity<ApiResponse<SalesReportDto>> getSalesReport(){
		SalesReportDto salesReportDto = statisticsService.getSalesReportOverall();
		return ResponseEntity.ok(ApiResponse.success("銷售報告查詢成功", salesReportDto));
	}
	
  // ==========================================================
  //                         分類管理 (NUEVO / MOVIDO)
  // ==========================================================
	
	//新增分類
	@PostMapping("/categories")
	public ResponseEntity<ApiResponse<CategoryDto>> addCategory(@Valid @RequestBody CategoryDto categoryDto ,BindingResult bindingResult ){
		if(bindingResult.hasErrors()) {
			throw new CategoryException("新增失敗:" + bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
		categoryService.addCategory(categoryDto);
		return ResponseEntity.ok(ApiResponse.success("Category 新增成功 ", categoryDto));
		
	} 
	
	// 修改分類
	@PutMapping("/categories/{categoryId}")
	public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Integer categoryId,@Valid @RequestBody CategoryDto categoryDto ,BindingResult bindingResult){
		if(bindingResult.hasErrors()) {
			throw new CategoryException("修改失敗:" + bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
		categoryService.updateCategory(categoryId, categoryDto);
		
		return ResponseEntity.ok(ApiResponse.success("Category 修改成功 ", categoryDto));
	}
	
	// 刪除分類
	@DeleteMapping("/categories/{categoryId}")
	public ResponseEntity<ApiResponse<Integer>> deleteCategory(@PathVariable Integer categoryId){
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok(ApiResponse.success("Category 刪除成功 ", categoryId));
	}

	
	
}
