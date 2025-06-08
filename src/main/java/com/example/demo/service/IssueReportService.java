package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.IssueReportDto;
import com.example.demo.model.dto.IssueReportSubmitDto;
import com.example.demo.model.entity.enums.IssueStatus;
import com.example.demo.model.entity.enums.UserRole;

public interface IssueReportService {

	IssueReportDto submitReport(IssueReportSubmitDto submitDto, Integer reporterUserId) throws UserNotFoundException;

	IssueReportDto getReportById(Integer reportId, Integer requestingUserId, UserRole userRole);

	List<IssueReportDto> getAllReportsForAdmin(Integer reporterUserId);

	List<IssueReportDto> getReportsByReporter(Integer reporterUserId);

	IssueReportDto updateReportStatus(Integer reportId, IssueStatus newStatus, String adminRemarks, Integer adminUserId)
			throws UserNotFoundException;

	
}
