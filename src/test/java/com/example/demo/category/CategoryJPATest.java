package com.example.demo.category;

import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.CategoryDto;
import com.example.demo.model.entity.Category;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.CategoryService;

@SpringBootTest
public class CategoryJPATest {
	
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CategoryService categoryService;
	
	
	@Test
	public void test() {
		List<CategoryDto> categoryDto = categoryService.findAllCategorys();
		System.out.println(categoryDto);
	}
	
}
