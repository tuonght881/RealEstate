package com.poly.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.poly.service.TransactionService;

@RestController
public class StatisticalRestController {
	
	@Autowired
	TransactionService transactionService;
	
	@GetMapping("/rest/statisticl")
	public List<Object[]> getALL(@Param("year") Integer year){        
		return  transactionService.getMonthlyTotalPrices(year);
	}
	
	@GetMapping("/rest/income-in-recent-years")
	public List<Object[]> getIncomeInRecentYears(){
		return  transactionService.getIncomeInRecentYears();
	}
	
	@GetMapping("/rest/list-years")
	public List<Object[]> getYears(){
		return  transactionService.getYears();
	}
}
