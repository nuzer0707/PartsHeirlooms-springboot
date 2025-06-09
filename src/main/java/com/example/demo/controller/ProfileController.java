package com.example.demo.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.CertException;
import com.example.demo.exception.PasswordInvalidException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.ProductOperationException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.users.UserPasswordChangeDto;
import com.example.demo.model.dto.users.UserProfileDto;
import com.example.demo.model.dto.IssueReportDto;
import com.example.demo.model.dto.IssueReportSubmitDto;
import com.example.demo.model.dto.RatingDto;
import com.example.demo.model.dto.RatingSubmitDto;
import com.example.demo.model.dto.SellerVerificationApplyDto;
import com.example.demo.model.dto.SellerVerificationDto;
import com.example.demo.model.dto.products.ProductSummaryDto;
import com.example.demo.model.dto.users.UserCert;

import com.example.demo.response.ApiResponse;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.IssueReportService;
import com.example.demo.service.RatingService;
import com.example.demo.service.SellerVerificationService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = {"/profile"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"},allowCredentials = "true")
public class ProfileController {
	//使用者自行操作
	@Autowired
	private UserService userService;
	
	@Autowired
  private FavoriteService favoriteService;
	
	 @Autowired
   private TransactionService transactionService;
	 
	 @Autowired
   private SellerVerificationService sellerVerificationService;

	 @Autowired
   private IssueReportService issueReportService;

	 @Autowired
   private RatingService ratingService; 

	// ==========================================================
  //                     帳戶管理 (EXISTENTE)
  // ==========================================================

	
	//使用者查詢自己的個人資料
	@GetMapping("/user")
	private ResponseEntity<ApiResponse<UserProfileDto>>getMyProfile (HttpSession session){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		if (userCert == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
													.body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(),"請先登入以查看個人資料"));
		}
		try {
				UserProfileDto userProfile = userService.getUserProfile(userCert.getUserId());
				return ResponseEntity.ok(ApiResponse.success("個人資料查詢成功", userProfile));
		} catch (UserNotFoundException  e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
															.body(ApiResponse.error
															(HttpStatus.NOT_FOUND.value(),e.getMessage()));
		}catch (CertException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          								.body(ApiResponse.error
          								(HttpStatus.INTERNAL_SERVER_ERROR.value(), "查詢個人資料失敗，請稍後再試"));
		}
		
	}
	
	//使用者修改密碼
	@PutMapping("/password")
	private ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody UserPasswordChangeDto passwordChangeDto,HttpSession session){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		
		if(userCert == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(401,"請先登入"));
		}
		try {
			userService.changePassword(userCert.getUserId(), passwordChangeDto);
			return ResponseEntity.ok(ApiResponse.success("密碼修改成功", null));
			
		} catch (CertException e) {
			 // 根據例外類型返回不同狀態碼
			if(e instanceof PasswordInvalidException) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(400,"密碼修改失敗"+e.getMessage()));
				}else if(e instanceof UserNotFoundException){
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(404,"密碼修改失敗"+e.getMessage()));
				}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(500,"密碼修改失敗"+e.getMessage()));
		}
	}
  // ==========================================================
  //                     興趣商品 (NUEVO)
  // ==========================================================
	
	@GetMapping("/favorites")
	public ResponseEntity<ApiResponse<List<ProductSummaryDto>>> getMyFavorites(HttpSession session){
		
		UserCert userCert = (UserCert) session.getAttribute("userCert");
		
		if(userCert==null) return unauthorizedGenericResponse("請先登入以查看收藏商品");
		
		List<ProductSummaryDto> favorites =
				favoriteService.getFavoritesByUserId(userCert.getUserId());
		return ResponseEntity.ok(ApiResponse.success("查詢收藏商品成功", favorites));
	}
	
	@PostMapping("/favorites/{productId}")
	
	public ResponseEntity<ApiResponse<Void>> addFavorite (
			@PathVariable Integer productId,
			HttpSession session
			){
			UserCert userCert = (UserCert) session.getAttribute("userCert");
			if(userCert==null) return unauthorizedResponse("請先登入以查看收藏商品");
				
				try {
				favoriteService.addFavorite(userCert.getUserId(), productId);
				return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("商品已成功加入收藏", null));
				} catch (UserNotFoundException  e) {
					 return ResponseEntity.status(HttpStatus.NOT_FOUND)
               .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
				}catch (ProductNotFoundException   e) {
					 return ResponseEntity.status(HttpStatus.NOT_FOUND)
               .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));	 
				}catch (ProductOperationException e) { // 例如 "商品已在收藏中"
          return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage()));
			}	
	}
  @DeleteMapping("/favorites/{productId}")
	public ResponseEntity<ApiResponse<Void>> removeFavorite(
			@PathVariable Integer ProductId,
			HttpSession session
			){
  		UserCert userCert = (UserCert) session.getAttribute("userCert");
  		
  		if (userCert == null) return unauthorizedResponse("請先登入以移除收藏");
  		favoriteService.removeFavorite(userCert.getUserId(), ProductId);
	
  		 return ResponseEntity.ok(ApiResponse.success("商品已從收藏移除", null));
  	
  }
	
  @GetMapping("/favorites/check/{productId}")
  public ResponseEntity<ApiResponse<Boolean>> checkFavorite(
  		@PathVariable Integer productId,
  		HttpSession session
  		){
  	 UserCert userCert = (UserCert) session.getAttribute("userCert");
  	 if(userCert == null) {
  		 return ResponseEntity.ok(ApiResponse.success("用戶未登入，無法檢查收藏狀態", false));
  	 }
  	boolean isFavorited = favoriteService.isFavorited(userCert.getUserId(), productId);
  	return ResponseEntity.ok(ApiResponse.success("收藏狀態查詢成功", isFavorited));
  }
	
  // ==========================================================
  //                         賣家申請 (NUEVO)
  // ==========================================================
  
  @GetMapping("/seller-application/status")
  public ResponseEntity<ApiResponse<SellerVerificationDto>> getMySellerApplicationStatus(HttpSession session){
  	UserCert userCert = (UserCert) session.getAttribute("userCert");
  	
  	if(userCert==null) return unauthorizedGenericResponse("請先登入以查看賣家申請狀態");

  	try {
			SellerVerificationDto status = sellerVerificationService.getUserVerificationStatus(userCert.getUserId());
			return  ResponseEntity.ok(ApiResponse.success("查詢賣家申請狀態成功", status));
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),  "系統錯誤：" + e.getMessage()));
		} catch (ProductNotFoundException e) { // 表示尚未申請
      return ResponseEntity.ok(ApiResponse.success("您尚未提交賣家申請", null));
	}

 }  
  
  
  @PostMapping("/seller-application")
  public ResponseEntity<ApiResponse<SellerVerificationDto>> applyForSeller(
  		@Valid @RequestBody SellerVerificationApplyDto applyDto,
  		HttpSession session
  		){
  	
  		UserCert userCert = (UserCert) session.getAttribute("userCert");
  	
  		if(userCert == null ) return unauthorizedGenericResponse("請先登入以申請成為賣家");
  	
  		try {
				SellerVerificationDto verificationDto = sellerVerificationService.applyForSeller(applyDto, userCert.getUserId());
				return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("賣家資格申請已提交", verificationDto));
  		} catch (UserNotFoundException e) {
				 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						 .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
			}	catch (ProductOperationException e) {
				 return ResponseEntity.status(HttpStatus.CONFLICT)
             .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage()));
			}	
  }
  
  // ==========================================================
  //                         客服聯繫 (NUEVO)
  // ==========================================================
  
  @GetMapping("/support/reports")
  public ResponseEntity<ApiResponse<List<IssueReportDto>>> getMyIssueReports(HttpSession session){
		
  	UserCert userCert = (UserCert) session.getAttribute("userCert");
  	if(userCert == null) return unauthorizedGenericResponse("請先登入以查看問題回報紀錄");

  	List<IssueReportDto> reportDtos =issueReportService.getReportsByReporter(userCert.getUserId());
  	
  	return ResponseEntity.ok(ApiResponse.success("查詢問題回報紀錄成功", reportDtos));
  }
  
  @PostMapping("/support/report")
  public ResponseEntity<ApiResponse<IssueReportDto>> submitIssueReport (
  		@Valid @RequestBody IssueReportSubmitDto submitDto,
  		HttpSession session
  		){
  	UserCert userCert = (UserCert) session.getAttribute("userCert");
  	if(userCert==null)return unauthorizedGenericResponse("請先登入以提交問題回報");
  	
  	try {
			IssueReportDto reportDto = issueReportService.submitReport(submitDto, userCert.getUserId());
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("問題回報已提交", reportDto));
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
		} 	
  }
  
  // ==========================================================
  //                         提交評價 (NUEVO)
  // ==========================================================
 
  @GetMapping("/ratings/check/{transactionId}")
  public ResponseEntity<ApiResponse<Boolean>> hasRatedTransaction(
  		@PathVariable Integer transactionId,
  		HttpSession session
  		){
  		UserCert userCert = (UserCert) session.getAttribute("userCert");
  	 if (userCert == null) {
       return ResponseEntity.ok(ApiResponse.success("用戶未登入，無法檢查評價狀態", false));
  	 }
  	 boolean hasRated =ratingService.hasUserRatedTransaction(transactionId, userCert.getUserId());
  
  	 return ResponseEntity.ok(ApiResponse.success("評價狀態檢查成功", hasRated));
  }
  
  @PostMapping("/ratings")
  public ResponseEntity<ApiResponse<RatingDto>> submitRating(
  		@Valid @RequestBody RatingSubmitDto ratingSubmitDto,
  		HttpSession session
  		){
  	 UserCert userCert = (UserCert) session.getAttribute("userCert");
  	 
  	 if(userCert==null)return unauthorizedGenericResponse("請先登入以提交評價");
  	 
  	 try {
			RatingDto newRating = ratingService.addRating(ratingSubmitDto, userCert.getUserId());
			 return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("評價已成功提交", newRating));
  	 } catch (UserNotFoundException e) {
  		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
           .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系統錯誤：" + e.getMessage()));
  	 }catch (ProductNotFoundException e) {
  		 return ResponseEntity.status(HttpStatus.NOT_FOUND)
           .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "系統錯誤：" + e.getMessage()));
  	 }catch (AccessDeniedException e) {
  		 return ResponseEntity.status(HttpStatus.FORBIDDEN)
           .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "系統錯誤：" + e.getMessage()));
  	 }catch (ProductOperationException e) {
  		 return ResponseEntity.status(HttpStatus.CONFLICT)
           .body(ApiResponse.error(HttpStatus.CONFLICT.value(), "系統錯誤：" + e.getMessage()));
		}
  	 
  	 
  }
	
	
  
	
  
  
  
  
  
  
  
  
	private ResponseEntity<ApiResponse<Void>> unauthorizedResponse(String message){
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
	}
	
	private <T> ResponseEntity<ApiResponse<T>> unauthorizedGenericResponse(String message) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), message));
}

	
	
}
