package com.iv.parse;

public class StockParser{
	public enum DateFrequency{
		day,
		month,
		year
		
	}
	
	private static String yahooURLHeader = "http://ichart.finance.yahoo.com/table.csv?";
	private static String yahooURLFooter = "ignore=.csv";
	
	private String ticker = null;
	private int fromMonth = 0;
	private int fromDay = 0;
	private int fromYear = 0;
	private int toMonth = 0;
	private int toDay = 0;
	private int toYear = 0;
	private DateFrequency dateFrequency = DateFrequency.day;
	private String formattedURL = null;
		
	public StockParser(String ticker, int fMonth, int fDay, int fYr, int tMonth, int tDay, int tYr, DateFrequency dateFreq){
		this.fromMonth = fMonth;
		this.fromDay = fDay;
		this.fromYear = fYr;
		this.toMonth = tMonth;
		this.toDay = tDay;
		this.toYear = tYr;
		
		this.ticker = ticker;
		this.dateFrequency = dateFreq;
	}
	
	public StockParser(String ticker){
		this(ticker,0,0,1900,3,1,2016,DateFrequency.day);
	}
	
	public String getFormattedURL(){
		if(formattedURL == null){
			formattedURL = yahooURLHeader;
			formattedURL += addParam("s", ticker);
			formattedURL += addParam("a", "" + fromMonth);
			formattedURL += addParam("b", "" + fromDay);
			formattedURL += addParam("c", "" + fromYear);
			formattedURL += addParam("d", "" + toMonth);
			formattedURL += addParam("e", "" + toDay);
			formattedURL += addParam("f", "" + toYear);
			
			switch(dateFrequency){
				case day:
					formattedURL += addParam("g", "d");
					break;
				
				case month:
					formattedURL += addParam("g", "m");
					break;
					
				case year:
					formattedURL += addParam("g", "y");
					break;
			}
			
			formattedURL += yahooURLFooter;
		}
		
		return formattedURL;
	}

	private String addParam(String param, String ticker) {
		return param + "=" + ticker + "&";
	}
}
