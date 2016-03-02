package com.iv.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.iv.csv.StockCSVReader;

public class StockData {
	private ArrayList<DayEarnings> dailyEarnings = new ArrayList<DayEarnings>();
	private ArrayList<ValueJump> valueJumps = new ArrayList<ValueJump>();
	private final String ticker;
	
	public StockData(String ticker){
		this.ticker = ticker;
	}
	
	public String getTicker(){ return ticker; }
	
	public void importCSVData(){
		String filename = "C:\\Sandbox\\IV\\StockHistoryCSVs\\" + ticker + ".csv";
		StockCSVReader csvReader = new StockCSVReader(filename);
		dailyEarnings = csvReader.getDailyEarnings();
		Collections.sort(dailyEarnings, new DayEarningsSorter());
	}

	/**
	 * Sort the daily earnings list by date asc, and then iterate over it.  Add each day that was a jump
	 * to the value jumps list.
	 */
	public void calculateValueJumps() {
		Collections.sort(dailyEarnings, new DayEarningsSorter());
		if(dailyEarnings.size() <= 0){
			return;
		}
		
		int index = 0;
		do{
			DayEarnings startDay = getEarningsForIndex(index);
			DayEarnings endDay = getEarningsForIndex(index + 90);
			
			double percentJump = getPercentJump(startDay, endDay);
			if(Math.abs(percentJump) > 300){
				this.valueJumps.add(new ValueJump(startDay.getDate(), endDay.getDate(), percentJump));
			}
			
			index ++;
		} while (index < dailyEarnings.size());
		
		//conslidateValueJumps();
	}
	
	private void conslidateValueJumps() {
		Iterator<ValueJump> valueJumpIter = valueJumps.iterator();
		if(valueJumpIter.hasNext()){
			ValueJump first = valueJumpIter.next();
			ValueJump second = null;
			
			while(valueJumpIter.hasNext()){
				second = valueJumpIter.next();
				if(second.getEndDate().getDayOfYear() - first.getEndDate().getDayOfYear() == 1 || 
					(second.getEndDate().getDayOfYear() == 1 && first.getEndDate().getDayOfYear() == 365)){
					first.setEndDate(second.getEndDate());
					valueJumpIter.remove();
				} else {
					first = second;
				}
			}
		}
	}

	private DayEarnings getEarningsForIndex(int index){
		if(index > dailyEarnings.size() - 1){
			return dailyEarnings.get(dailyEarnings.size() - 1);
		} else { 
			return dailyEarnings.get(index);
		}
	}
	
	public static double getPercentJump(DayEarnings startDay, DayEarnings endDay){
		return ((endDay.getAdjClose() - startDay.getAdjClose()) / startDay.getAdjClose()) * 100;
	}
	
	private class DayEarningsSorter implements Comparator<DayEarnings>{
		@Override
		public int compare(DayEarnings arg0, DayEarnings arg1) {
			return arg0.getDate().isBefore(arg1.getDate()) ? -1 : (arg0.getDate().equals(arg1.getDate()) ? 0 : 1 );
		}
		
	}

	public ArrayList<ValueJump> getValueJumps() {
		return valueJumps;
	}
	
	public List<DayEarnings> getEarningsForDateRange(DateTime start, DateTime end){
		ArrayList<DayEarnings> earningsInRange = new ArrayList<DayEarnings>();
		for(DayEarnings day : dailyEarnings){
			if(day.getDate().isBefore(end) && day.getDate().isAfter(start)){
				earningsInRange.add(day);
			}
		}
		
		return earningsInRange;
	}

	public DayEarnings getEarningsForDay(DateTime date) {
		if(dailyEarnings.size() > 0 && date.isAfter(dailyEarnings.get(dailyEarnings.size() - 1).getDate())){
			return dailyEarnings.get(dailyEarnings.size() - 1);
		}
		
		for(DayEarnings day : dailyEarnings){
			switch(date.getDayOfWeek()){
				case 6:
					// Saturday goes to Friday
					date = date.minusDays(1);
					break;
				case 7:
					// Sunday goes to Monday
					date = date.plusDays(1);
					break;
			}
			
			// not open on xmas
			if(date.getDayOfYear() == 359)
			{
				date = date.minusDays(2);
			}
			
			LocalDate date1 = day.getDate().toLocalDate();
			LocalDate date2 = date.toLocalDate();
			if(date1.compareTo(date2) == 0){
				return day;
			}
		}
		
		return null;
	}

	public DayEarnings getLastEarning() {
		if(dailyEarnings.size() > 0){
			return dailyEarnings.get(dailyEarnings.size() - 1);	
		} else {
			return null;
		}
	}
	
	
}
