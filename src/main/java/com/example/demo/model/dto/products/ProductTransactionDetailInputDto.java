package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransactionDetailInputDto {
	//產品交易明細輸入 DTO

	@NotNull(message = "交易方式 ID 不能為空")
	private Integer methodId;  // 參考 TransactionMethod 的 ID

	private LocalDateTime meetupTime;// 面交時間
	
	@Size(max = 255,message = "一般備註長度不能超過 255 個字元")
	private String generaNotes;
	
	//緯度驗證範圍：-90 到 90
	
	@Column(name = "meetup_latitude", precision = 10, scale = 8)
	private BigDecimal meetupLatitude;
	
	// 經度驗證範圍：-180 到 180
	
	@Column(name = "meetup_longitude", precision = 11, scale = 8)
	private BigDecimal meetupLongitude;
}
