package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.IssueReportMapper;
import com.example.demo.model.dto.IssueReportDto;
import com.example.demo.model.dto.IssueReportSubmitDto;
import com.example.demo.model.entity.IssueReport;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.IssueStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.repository.IssueReportRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IssueReportService;

@Service
public class IssueReportServiceImpl implements IssueReportService{
	
	@Autowired
	private IssueReportRepository issueReportRepository;
	@Autowired
	private UserRepository userRepository;
	
	private IssueReportMapper issueReportMapper;
	
	
	
	@Override
	@Transactional
	public IssueReportDto submitReport(IssueReportSubmitDto submitDto, Integer reporterUserId)
			throws UserNotFoundException {
		User reporter = userRepository.findById(reporterUserId)
				.orElseThrow(()->new UserNotFoundException("檢舉者用戶不存在 ID: "+reporterUserId));
		
		IssueReport report =issueReportMapper.toEntity(submitDto, reporter);
		report.setStatus(IssueStatus.Open);
		IssueReport saveReport = issueReportRepository.save(report);
		return issueReportMapper.toDto(saveReport);
	}

	@Override
	@Transactional(readOnly = true)
	public IssueReportDto getReportById(Integer reportId, Integer requestingUserId, UserRole userRole) {
		IssueReport report = issueReportRepository.findById(reportId)
				.orElseThrow(()-> new ProductNotFoundException("找不到檢舉報告 ID: " + reportId));
		
		boolean isAdmin = UserRole.ADMIN.equals(userRole);
		boolean isReporter = report.getReporterUser().getUserId().equals(requestingUserId);
		
		if(!isAdmin && !isReporter) {
			throw new AccessDeniedException("您無權查看此檢舉報告");
		}
		return issueReportMapper.toDto(report);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IssueReportDto> getAllReportsForAdmin() {
		return issueReportRepository
				.findAll()
				.stream()
				.map(issueReportMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<IssueReportDto> getReportsByReporter(Integer reporterUserId) {
		return issueReportRepository
				.findByReporterUser_UserIdOrderByCreatedAtDesc(reporterUserId)
				.stream()
				.map(issueReportMapper::toDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public IssueReportDto updateReportStatus(Integer reportId, IssueStatus newStatus, String adminRemarks,
			Integer adminUserId) throws UserNotFoundException {
		User admin = userRepository.findById(adminUserId)
				.orElseThrow(()->new UserNotFoundException("管理員用戶不存在 ID: "+adminUserId));
		if(!UserRole.ADMIN.equals(admin.getPrimaryRole())) {
			throw new AccessDeniedException("只有管理員可以更新報告狀態");
		}
		
		IssueReport report =  issueReportRepository.findById(reportId)
				.orElseThrow(()-> new ProductNotFoundException("找不到檢舉報告 ID:"+reportId));
		report.setStatus(newStatus);
		// report.setAdminRemarks(adminRemarks); // 假設 IssueReport 有 adminRemarks 欄位
        // report.setReviewedByAdmin(admin);    // 假設 IssueReport 有 reviewedByAdmin 欄位
		// report.setReviewedAt(LocalDateTime.now()); // 假設 IssueReport 有 reviewedAt 欄位
		IssueReport updateReport = issueReportRepository.save(report);
		return issueReportMapper.toDto(updateReport);
	}
}
