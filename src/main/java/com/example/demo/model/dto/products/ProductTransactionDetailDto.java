package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransactionDetailDto {
	// 產品交易明細 DTO
	private Integer detailId; // 這個 ProductTransactionDetail 實例的 ID
	private Integer methodId; // 交易方式 ID
	private String methodName; // 來自 TransactionMethod 實體
	private String methodDescription; // 來自 TransactionMethod 實體
	private LocalDateTime meetupTime;
  private String generalNotes;
  private BigDecimal meetupLatitude;
  private BigDecimal meetupLongitude;

}
