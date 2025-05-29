package com.example.demo.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.demo.model.entity.enums.VerificationStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seller_verification")
public class SellerVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "verification_id")
	private Integer verification;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status" ,nullable = false,
	columnDefinition = "ENUM('Pending','Approved','Rejected','Resubmit') DEFAULT 'Pending'")
	private VerificationStatus status;
	
	@Lob
	@Column(name = "admin_remarks",columnDefinition = "TEXT")
	private String adminRemarks;
	
	@CreationTimestamp
	@Column(name = "submitted_at",nullable = false,updatable = false)
	private LocalDateTime submittedAt;
	
	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reviewed_by_admin_id")
	private User reviewedByAdmin;
	
	@OneToMany(mappedBy = "sellerVerification",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private List<VerificationImage> verificationImages;
	
	
}
