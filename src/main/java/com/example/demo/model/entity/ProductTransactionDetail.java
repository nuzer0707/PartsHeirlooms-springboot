package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;


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
@Table(name = "product_transaction_details")
public class ProductTransactionDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "detail_id")
	private Integer detailId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id",nullable = false)
	private Product product;
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "method_id",nullable = false)
	private TransactionMethod transactionMethod;
	
	@Column(name = "meetup_time")
	private LocalDateTime meetupTime;
	
	@Column(name = "general_notes",length = 255)
	private String generalNotes;
	
	@Column(name = "meetup_latitude",precision = 10,scale = 8)
	private BigDecimal meetupLatitude;
	
	@Column(name = "meetup_longitude",precision = 10,scale = 8)
	private BigDecimal meetupLongitude;
	
	
}
