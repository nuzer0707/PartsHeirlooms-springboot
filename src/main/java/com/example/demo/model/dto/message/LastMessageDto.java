package com.example.demo.model.dto.message;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LastMessageDto {

	private String content;
    private LocalDateTime createdAt;
    private boolean isRead;
}
