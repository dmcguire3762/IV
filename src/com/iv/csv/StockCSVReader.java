package com.iv.csv;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.joda.time.DateTime;

import com.iv.data.DayEarnings;

public class StockCSVReader extends CSVReader{
	private ArrayList<DayEarnings> dailyEarnings = null;
	
	public StockCSVReader(String filename) {
		super(filename.replaceAll(" ", ""));
	}
	
	public ArrayList<DayEarnings> getDailyEarnings(){
		if(dailyEarnings == null){
			dailyEarnings = new ArrayList<DayEarnings>();
						
			try {
				this.open();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				this.close();
			}
			
			this.getNextLine(); // throw out the first line
			
			String nextLine = this.getNextLine();
			while(nextLine != null){
				try{
					String[] fields = nextLine.split(",");
					
					DateTime date = new DateTime(fields[0]);
					if(date.isBefore(new DateTime("2010-01-01"))){
						continue;
					}
					
					double adjClose = Double.parseDouble(fields[6]);
					DayEarnings newDay = new DayEarnings(date,adjClose);				
					dailyEarnings.add(newDay);
				
				} finally {
					nextLine = this.getNextLine();
				}
			}
		}
		
		return dailyEarnings;
	}

}
