package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.model.dto.PlatformOverviewDto;
import com.example.demo.model.dto.SalesReportDto;

public interface StatisticsService {
	
	PlatformOverviewDto getPlatformOverview();
	//可選的按日期範圍
	//SalesReportDto getSalesReport(LocalDateTime startDate,LocalDate endDate);
	
	SalesReportDto getSalesReportOverall();

}