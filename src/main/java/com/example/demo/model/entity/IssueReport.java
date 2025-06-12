package com.example.demo.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.model.entity.enums.IssueStatus;
import com.example.demo.model.entity.enums.ReportTargetType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "issue_reports")
public class IssueReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Integer reportId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_user_id",nullable = false)
	private User reporterUser;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "target_type",nullable = false,
	columnDefinition = "ENUM('PRODUCT','USER','GENERAL')")
	private ReportTargetType targetType;
	
	@Column(name = "target_id")
	private Integer targetId;
	
	@Column(name = "reason_category",nullable = false,length = 100)
	private String reasonCategory;
	
	@Lob
	@Column(name = "details" ,columnDefinition = "TEXT")
	private String details;
	
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition="ENUM('Open','In Progress','Resolved','Closed','Invalid') DEFAULT 'Open'")
  private IssueStatus status;
	
  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
