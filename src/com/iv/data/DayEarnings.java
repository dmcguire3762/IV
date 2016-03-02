package com.iv.data;

import org.joda.time.DateTime;

public class DayEarnings {
	private final DateTime date;
	private final double open;
	private final double close;
	private final double high;
	private final double low;
	private final double adjClose;
	private final long vol;
	
	public DayEarnings(DateTime date, double adjustedClose){
		this.date = date;
		this.adjClose = adjustedClose;
		open = 0;
		close = 0;
		high = 0;
		low = 0;
		vol = 0;
	}
	
	public DateTime getDate(){
		return date;
	}
	
	public double getAdjClose(){
		return adjClose;
	}
}
