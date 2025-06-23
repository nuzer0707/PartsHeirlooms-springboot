package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.CategoryAddDto;
import com.example.demo.model.dto.CategoryDto;
import com.example.demo.model.dto.CategoryUpdateDto;

public interface CategoryService {

	public List<CategoryDto> findAllCategorys();// 查詢所有分類
	
	public CategoryDto getCategoryId(Integer categoryId);// 查詢單筆分類
	
	CategoryDto addCategory(CategoryAddDto categoryAddDto); 
	
	CategoryDto updateCategory(Integer categoryId, CategoryUpdateDto categoryUpdateDto); 
	
	public void addCategory(CategoryDto categoryDto);// 新增分類
	
	public void addCategory(Integer categoryId,String categoryName);// 新增分類
	
	public void updateCategory(Integer categoryId,CategoryDto categoryDto);//修改分類
	
	public void updateCategory(Integer categoryId,String categoryName);//修改分類
	
	public void deleteCategory(Integer categoryId);//刪除分類 

}
