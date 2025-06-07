package com.example.demo.model.dto;

import com.example.demo.model.entity.enums.ReportTargetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IssueReportSubmitDto {
	
	@NotNull(message = "必須指定報告目標類型")
	private ReportTargetType targetType;
	
	private Integer targetTypeId;
	
	@NotBlank(message = "必須提供原因類別")
	private String reasonCategory;
	
	@NotBlank(message = "請詳細描述問題")
	private String details;
	
}
