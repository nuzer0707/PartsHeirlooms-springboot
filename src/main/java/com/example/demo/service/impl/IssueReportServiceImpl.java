package com.example.demo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.dto.IssueReportDto;
import com.example.demo.model.dto.IssueReportSubmitDto;
import com.example.demo.model.entity.enums.IssueStatus;
import com.example.demo.model.entity.enums.UserRole;
import com.example.demo.service.IssueReportService;

@Service
public class IssueReportServiceImpl implements IssueReportService{

	@Override
	public IssueReportDto submitReport(IssueReportSubmitDto submitDto, Integer reporterUserId)
			throws UserNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueReportDto getReportById(Integer reportId, Integer requestingUserId, UserRole userRole) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IssueReportDto> getAllReportsForAdmin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IssueReportDto> getReportsByReporter(Integer reporterUserId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueReportDto updateReportStatus(Integer reportId, IssueStatus newStatus, String adminRemarks,
			Integer adminUserId) throws UserNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
