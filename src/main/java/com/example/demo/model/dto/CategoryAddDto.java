package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryAddDto {

    // 【核心】這個 DTO 中完全不包含 categoryId 欄位

    @NotBlank(message = "分類名稱不能為空")
    @Size(min = 1, max = 50, message = "分類名稱長度必須在 1 到 50 個字元之間")
    private String categoryName;
}
