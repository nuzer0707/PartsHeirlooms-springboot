package com.example.demo.model.dto;


import java.time.LocalDateTime;

import com.example.demo.model.entity.enums.IssueStatus;
import com.example.demo.model.entity.enums.ReportTargetType;

import lombok.Data;

@Data
public class IssueReportDto {

	private Integer reportId;
	private Integer reporterUserId;
	private String reporterUsername;
	private ReportTargetType targetType;
	private Integer targetTypeId;
	private String reasonCategory;
	private IssueStatus status;
	private LocalDateTime createdAt;
	
}
