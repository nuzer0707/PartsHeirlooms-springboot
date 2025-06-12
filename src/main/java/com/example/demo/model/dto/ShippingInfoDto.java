package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ShippingInfoDto {
	@NotBlank(message = "運送地址不能為空")
    @Size(max = 255)
    private String address;
}
