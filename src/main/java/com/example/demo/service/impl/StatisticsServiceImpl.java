package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.dto.PlatformOverviewDto;
import com.example.demo.model.dto.SalesReportDto;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.enums.ProductStatus;
import com.example.demo.model.entity.enums.TransactionStatus;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.StatisticsService;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	@Autowired
  private UserRepository userRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private TransactionRepository transactionRepository;

	
	
	@Override
	@Transactional(readOnly = true)
	public PlatformOverviewDto getPlatformOverview() {
		PlatformOverviewDto overviewDto = new PlatformOverviewDto();
		
		overviewDto.setTotalUser(userRepository.count());
		overviewDto.setActiveUser(userRepository.countByActive(true));
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime firstDayOfMonth=
				now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
				LocalDateTime lastDayOfMonth 
				= now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
		
		// 新增/修改: 使用 UserRepository 的 countByCreatedAtBetween (如果已加入)

				try {
					Long newUserthisMonthConut = 
							userRepository.countByCreatedAtBetween(firstDayOfMonth,lastDayOfMonth);
					overviewDto.setNewUsersThisMonth(newUserthisMonthConut);
				} catch (Exception e) {
					List<User> allUsers= userRepository.findAll();
					long newUserthisMonthStream 
						= allUsers
						.stream()
						.filter(user->user.getCreatedAt()!=null &&
						!user.getCreatedAt().isBefore(firstDayOfMonth)&&
						!user.getCreatedAt().isAfter(lastDayOfMonth))
						.count();
					overviewDto.setNewUsersThisMonth(newUserthisMonthStream);
				}
		
				overviewDto.setTotalProducts(productRepository.count());
				overviewDto.setProductForSale(productRepository.countByStatus(ProductStatus.For_Sale));
		
				List<Transaction> completedTransactions;
				
				try {
					completedTransactions = transactionRepository.findByStatus(TransactionStatus.Cancelled);				
				} catch (Exception e) {
					completedTransactions = transactionRepository
							.findAll()
							.stream()
							.filter(t->t.getStatus()==TransactionStatus.Cancelled)
							.collect(Collectors.toList());
				}
				
				overviewDto.setTotalTransactions((long) completedTransactions.size());
				
				BigDecimal totalTransactionValue = completedTransactions
						.stream()
						.map(Transaction::getFinalPrice)
						.reduce(BigDecimal.ZERO,BigDecimal::add);
				overviewDto.setTotalTransactionValue(totalTransactionValue);
				
		return overviewDto;
	}

	@Override
	@Transactional(readOnly = true)
	public SalesReportDto getSalesReportOverall() {
		SalesReportDto report = new SalesReportDto();
		
		List<Transaction>completedTransactions;
		try {
			completedTransactions = 
					transactionRepository.findByStatus(TransactionStatus.Completed);
		} catch (Exception e) {
			completedTransactions =
					transactionRepository
					.findAll()
					.stream()
					.filter(t->t.getStatus()==TransactionStatus.Completed)
					.collect(Collectors.toList());
		}

		BigDecimal totalRevenue = completedTransactions
															.stream()
															.map(Transaction::getFinalPrice)
															.reduce(BigDecimal.ZERO, BigDecimal::add);
		report.setTotalRevenue(totalRevenue);
		report.setTotalOrders((long)completedTransactions.size());
		report.setTotalItemsSold((long)completedTransactions.size());
		return report;
	}

}
