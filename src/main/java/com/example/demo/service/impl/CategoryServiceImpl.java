package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.CategoryAlreadyException;
import com.example.demo.exception.CategoryNotFoundException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.model.dto.CategoryAddDto;
import com.example.demo.model.dto.CategoryDto;
import com.example.demo.model.dto.CategoryUpdateDto;
import com.example.demo.model.entity.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;



@Service
public class CategoryServiceImpl implements CategoryService {

	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	
	@Override
	@Transactional(readOnly = true)  // 讀操作，設置 readOnly = true 可以優化
	public List<CategoryDto> findAllCategorys() {
		return categoryRepository.findAll()//  category 集合
								 .stream()//  category 集合
								 .map(categoryMapper::toDto)//  categoryDto 串流  .map(category -> categoryMapper.toDto(category))
								 .toList();//  categoryDto  集合
	}

	@Override
	@Transactional(readOnly = true)  // 讀操作，設置 readOnly = true 可以優化
	public CategoryDto getCategoryId(Integer categoryId) {
		Category category = categoryRepository
							.findById(categoryId)
							.orElseThrow(()->new CategoryNotFoundException("找不到分類: categoryId=" + categoryId));
		return categoryMapper.toDto(category);
	}

	@Override
	@Transactional// 寫操作建議加上事務管理
	public void addCategory(CategoryDto categoryDto) {
		Optional<Category> optCategory = categoryRepository
									 .findByCategoryName(categoryDto.getCategoryName());
		if(optCategory.isPresent()) { // 分類已存在
			throw new CategoryAlreadyException("新增失敗: 分類號 " + categoryDto.getCategoryName()+"已存在");
		}
		// 進入新增程序
		// DTO 轉 Entity
		Category category = categoryMapper.toEntity(categoryDto);
		// 將 Entity room 存入
		categoryRepository.save(category); //更新(可以配合交易模式, 若交易失敗則會回滾), 只是先暫存起來
		categoryRepository.flush(); // 提早手動寫入資料庫
		// ... 其他 code
		// 方法結束會自動 flush()
	}

	@Override
	@Transactional// 寫操作建議加上事務管理
	public void addCategory(Integer categoryId, String categoryName) {
		
		CategoryDto ctegoryDto = new CategoryDto(categoryId,categoryName);
		addCategory(ctegoryDto);
	}

	@Override
	@Transactional// 寫操作建議加上事務管理
	public void updateCategory(Integer categoryId, CategoryDto categoryDto) {
		
		Optional<Category> optCategory = categoryRepository.findById(categoryId);
		if(optCategory.isEmpty()) {
			throw new CategoryAlreadyException("修改失敗: 分類號 " + categoryId +"不存在");
		}
		categoryDto.setCategoryId(categoryId);
		Category category = categoryMapper.toEntity(categoryDto);
		categoryRepository.saveAndFlush(category); // 更新(馬上強制寫入更新)
		// 更新(可以配合交易模式, 若交易失敗則會回滾)
		
	}

	@Override
	@Transactional// 寫操作建議加上事務管理
	public void updateCategory(Integer categoryId, String categoryName) {
		CategoryDto ctegoryDto = new CategoryDto(categoryId,categoryName);
		updateCategory(categoryId, ctegoryDto);
	}
	
	@Override
	@Transactional// 寫操作建議加上事務管理
	public void deleteCategory(Integer categoryId) {
		// 判斷該分類是否已經存在 ?
		Optional<Category> optCategory = categoryRepository.findById(categoryId);
		
		if(optCategory.isEmpty()) {// 分類不存在
			throw new CategoryAlreadyException("刪除失敗: 分類號 " + categoryId +"不存在");
		}
		categoryRepository.deleteById(categoryId);
	}

	@Override
	@Transactional
	public CategoryDto addCategory(CategoryAddDto categoryAddDto) {
		 categoryRepository.findByCategoryName(categoryAddDto.getCategoryName()).ifPresent(c -> {
	            throw new CategoryAlreadyException("新增失敗: 分類名稱 " + categoryAddDto.getCategoryName() + " 已存在");
	        });

	        // 創建新的 Category 實體，【注意】這裡我們不再設置 ID
	        Category category = new Category();
	        category.setCategoryName(categoryAddDto.getCategoryName());

	        // 保存實體，數據庫會自動生成 ID
	        Category savedCategory = categoryRepository.save(category);

	        // 將保存後的實體（現在有 ID 了）轉換為 DTO 並返回
	        return categoryMapper.toDto(savedCategory);
	}

	@Override
	@Transactional
	public CategoryDto updateCategory(Integer categoryId, CategoryUpdateDto categoryUpdateDto) {
	      Category category = categoryRepository.findById(categoryId)
	              .orElseThrow(() -> new CategoryNotFoundException("修改失敗: 找不到分類 ID " + categoryId));

	          // 檢查新名稱是否與其他現有分類衝突
	          categoryRepository.findByCategoryName(categoryUpdateDto.getCategoryName())
	              .ifPresent(existingCategory -> {
	                  if (!existingCategory.getCategoryId().equals(categoryId)) {
	                      throw new CategoryAlreadyException("修改失敗: 分類名稱 " + categoryUpdateDto.getCategoryName() + " 已被其他分類使用");
	                  }
	              });

	          // 更新名稱並保存
	          category.setCategoryName(categoryUpdateDto.getCategoryName());
	          Category updatedCategory = categoryRepository.save(category);

	          return categoryMapper.toDto(updatedCategory);
	}


}
