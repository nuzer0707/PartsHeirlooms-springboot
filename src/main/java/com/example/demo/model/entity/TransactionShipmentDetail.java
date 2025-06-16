package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "transaction_shipment_details")
public class TransactionShipmentDetail {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_detail_id")	
	private Integer shipmentDetailId;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

	
//--- 以下是快照資訊 ---
	
	@Column(name = "method_name", nullable = false, length = 50)
    private String methodName;

	@Column(name = "address", length = 255)
    private String address;

	@Column(name = "meetup_time")
    private LocalDateTime meetupTime;

	@Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

  @Column(name = "meetup_latitude", precision = 10, scale = 8)
  private BigDecimal meetupLatitude;

  @Column(name = "meetup_longitude", precision = 11, scale = 8)
  private BigDecimal meetupLongitude;
	
	
}
