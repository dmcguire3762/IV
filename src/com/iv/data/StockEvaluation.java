package com.iv.data;

import java.util.List;

import org.joda.time.DateTime;

public class StockEvaluation {
	private final DateTime startDate;
	private final DateTime endDate;
	private final StockData stockData;
	private final List<DayEarnings> earningsInRange;
	
	public StockEvaluation(DateTime startDate, DateTime endDate, StockData stockData){
		this.startDate = startDate;
		this.endDate = endDate;
		this.stockData = stockData;
		this.earningsInRange = stockData.getEarningsForDateRange(startDate, endDate);
	}
	
	public DateTime getStartDate(){ return startDate; }
	public DateTime getEndDate(){ return endDate; }
	
	public double getPercentDiff(){
		DayEarnings firstDay = firstDayEarnings();
		DayEarnings lastDay = lastDayEarnings();
		
		if(firstDay != null && lastDay != null){
			return StockData.getPercentJump(firstDayEarnings(), lastDayEarnings());
		} else {
			return 0;
		}
	}
	
	public double getAbsoluteDiff(){
		return lastDayEarnings().getAdjClose() - firstDayEarnings().getAdjClose();
	}
	
	private DayEarnings lastDayEarnings(){
		if(earningsInRange.size() > 0 ){
			return earningsInRange.get(earningsInRange.size() - 1);
		} else {
			return null;
		}
	}
	
	private DayEarnings firstDayEarnings(){
		if(earningsInRange.size() > 0){
			return earningsInRange.get(0);
		} else {
			return null;
		}
	}

	public String getTicker(){
		return stockData.getTicker();
	}
}
