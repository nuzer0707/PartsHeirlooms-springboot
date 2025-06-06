package com.example.demo.model.dto.products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransactionDetailInputDto {

	@NotBlank(message = "交易方式 ID 不能為空")
	private Integer methodId;  // 參考 TransactionMethod 的 ID

	private LocalDateTime meetupTime;// 面交時間
	
	@Size(max = 255,message = "一般備註長度不能超過 255 個字元")
	private String generaNotes;
	
	//緯度驗證範圍：-90 到 90
	
	@DecimalMin(value = "-90.0", message = "面交緯度必須大於等於 -90")
	@DecimalMax(value = "90.0", message = "面交緯度必須小於等於 90")
	private BigDecimal meetupLatitude;
	
	// 經度驗證範圍：-180 到 180
	
  @DecimalMin(value = "-180.0", message = "面交經度必須大於等於 -180")
  @DecimalMax(value = "180.0", message = "面交經度必須小於等於 180")
	private BigDecimal meetupLongitude;
}
