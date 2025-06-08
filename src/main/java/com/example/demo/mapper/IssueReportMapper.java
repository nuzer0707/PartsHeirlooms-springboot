package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.IssueReportDto;
import com.example.demo.model.dto.IssueReportSubmitDto;
import com.example.demo.model.entity.IssueReport;
import com.example.demo.model.entity.User;

@Component
public class IssueReportMapper {
	
 	private ModelMapper modelMapper;
 	
 	public IssueReportDto toDto(IssueReport issueReport) {
 		if(issueReport == null ) {
 			return null;
 		}
 		IssueReportDto dto = modelMapper.map(issueReport, IssueReportDto.class);
 		if(issueReport.getReporterUser()!=null) {
 			dto.setReporterUserId(issueReport.getReporterUser().getUserId());
 			dto.setReporterUsername(issueReport.getReporterUser().getUsername());
 		}
 		
 		return dto;
 	}
 	
	public IssueReport toEntity(IssueReportSubmitDto submitDto,User reporterUser) {
		if(submitDto==null) {
			return null;
		}
		IssueReport entity = modelMapper.map(submitDto,IssueReport.class);
		entity.setReporterUser(reporterUser);
		entity.setTargerId(submitDto.getTargetTypeId());
		
		return entity;
		
	}
}
