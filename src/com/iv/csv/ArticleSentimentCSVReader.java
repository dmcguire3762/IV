package com.iv.csv;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class ArticleSentimentCSVReader extends CSVReader{

	public ArticleSentimentCSVReader(String filename) {
		super(filename);
	}
	
	public HashMap<String, Double> getArticleSentimentKeywordMap(){
		HashMap<String, Double> outMap = new HashMap<String, Double>();
		
		try {
			this.open();
		} catch (FileNotFoundException e) {
			// no worries here, we just haven't created the file yet.
			this.close();
			return null;
		}
		
		String nextLine = this.getNextLine();
		while(nextLine != null){
			String[] fields = nextLine.split(",");
			if(fields.length == 2 && fields[0] != null && fields[1] != null){
				outMap.put(fields[0], Double.parseDouble(fields[1]));
			}
			
			nextLine = this.getNextLine();
		}
		
		this.close();
		return outMap;
	}

}
