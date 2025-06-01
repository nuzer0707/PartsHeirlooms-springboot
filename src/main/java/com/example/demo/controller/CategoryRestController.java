package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.CategoryException;

import com.example.demo.model.dto.CategoryDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.CategoryService;

import jakarta.validation.Valid;

/**
請求方法 URL 路徑              功能說明      請求參數                                   回應
--------------------------------------------------------------------------------------------------------------------
GET    /category          取得所有分類列表 無                                       成功時返回所有分類的列表 payload 及成功訊息。
GET    /category /{category } 取得指定分類資料 category  (路徑參數，分類 ID)                  成功時返回指定分類資料及 payload 成功訊息。
POST   /category          新增房分類       請求體包含 categoryDto                         成功時返回成功訊息，並包含 payload。
PUT    /category /{category } 更新指定分類資料 category  (路徑參數，分類 ID)，請求體包含 category Dto 成功時返回成功訊息，並包含 payload。
DELETE /category /{category } 刪除指定分類    category  (路徑參數，分類 ID)                  成功時返回成功訊息，不包含 payload。
*/

@RestController
@RequestMapping(value = {"/category","/categorys"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8002"},allowCredentials = "true")
public class CategoryRestController {

	@Autowired
	private CategoryService categoryService;
	

	//取得所有分類
	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryDto>>> finsAllCategorys(){
		List<CategoryDto> categoryDto = categoryService.findAllCategorys();
		String message = categoryDto.isEmpty() ? "查無資料" : "查詢成功" ;
		return ResponseEntity.ok(ApiResponse.success(message, categoryDto));
	}
	
	// 取得單筆
	@GetMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<CategoryDto>> getCategory(@PathVariable Integer categoryId){
		CategoryDto categoryDto = categoryService.getCategoryId(categoryId);
		
		return ResponseEntity.ok(ApiResponse.success("Category 新增成功 ", categoryDto));
	}
	
	// 新增分類
	@PostMapping
	public ResponseEntity<ApiResponse<CategoryDto>> addCategory(@Valid @RequestBody CategoryDto categoryDto ,BindingResult bindingResult ){
		if(bindingResult.hasErrors()) {
			throw new CategoryException("新增失敗:" + bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
		categoryService.addCategory(categoryDto);
		return ResponseEntity.ok(ApiResponse.success("Category 新增成功 ", categoryDto));
		
	} 
	
	// 修改分類
	@PutMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Integer categoryId,@Valid @RequestBody CategoryDto categoryDto ,BindingResult bindingResult){
		if(bindingResult.hasErrors()) {
			throw new CategoryException("修改失敗:" + bindingResult.getAllErrors().get(0).getDefaultMessage());
		}
		categoryService.updateCategory(categoryId, categoryDto);
		
		return ResponseEntity.ok(ApiResponse.success("Category 修改成功 ", categoryDto));
	}
	
	// 刪除分類
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Integer>> deleteCategory(@PathVariable Integer categoryId){
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.ok(ApiResponse.success("Category 刪除成功 ", categoryId));
	}


	@ExceptionHandler({CategoryException.class})
	public ResponseEntity<ApiResponse<Void>> handleRoomExceptions(CategoryException e) {
		return ResponseEntity.ok(ApiResponse.error(500, e.getMessage()));
	}
	
}
