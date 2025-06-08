package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.IssueReport;
import com.example.demo.model.entity.enums.IssueStatus;

@Repository
public interface IssueReportRepository extends JpaRepository<IssueReport, Integer>{
	
	List<IssueReport> findByReporterUser_UserIdOrderByCreatedAtDesc( Integer userId);
	List<IssueReport> findByStatus(IssueStatus status);
}
