package com.iv.data;

import org.joda.time.DateTime;

public class ValueJump {
	private final DateTime startDate;
	private DateTime endDate;
	private final double percentJump;
	
	public ValueJump(DateTime startDate, DateTime endDate, double percentJump){
		this.startDate = startDate;
		this.endDate = endDate;
		this.percentJump = percentJump;
	}
	
	public DateTime getStartDate(){
		return startDate;
	}
	
	public DateTime getEndDate(){
		return endDate;
	}
	public double getPercentJump(){
		return percentJump;
	}

	public void setEndDate(DateTime date) {
		this.endDate = date;
	}
}
