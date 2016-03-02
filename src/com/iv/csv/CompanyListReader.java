package com.iv.csv;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class CompanyListReader extends CSVReader{
	HashMap<String, String> companyTickerMap = new HashMap<String, String>();
	
	public CompanyListReader(String filename) {
		super(filename);
	}
	
	public void ReadFile(){
		try {
			this.open();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			this.close();
		}
				
		String nextLine = this.getNextLine();
		while(nextLine != null){
			String[] fields = nextLine.split(",");
			if(fields.length > 2){
				String ticker = fields[0].replace("\"", "").replace(" ", "");
				String companyName = fields[1].replace("\"", "").replace("  ", "");
				if(!ticker.contains("^")){
					companyTickerMap.put(ticker, companyName);
				}
			}
			
			nextLine = this.getNextLine();
		}
		
		this.close();
	}
	
	public HashMap<String, String> getCompanyTickerMap(){
		return companyTickerMap;
	}

}
