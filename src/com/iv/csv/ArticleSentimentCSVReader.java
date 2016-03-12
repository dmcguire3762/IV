package com.iv.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.joda.time.DateTime;

import com.iv.data.NewsArticle;

public class ArticleSentimentCSVReader extends CSVReader{
	private HashMap<String, Double> articleSentimentKeywordMap = null;
	private DateTime date = null;
	
	
	public ArticleSentimentCSVReader(String filename) {
		super(filename);
	}
	
	public ArticleSentimentCSVReader(File file) {
		super(file);
	}
	
	public HashMap<String, Double> getArticleSentimentKeywordMap(){
		if(articleSentimentKeywordMap == null){
			read();
		}
		
		return articleSentimentKeywordMap;
	}
	
	private void read(){
		try {
			this.open();
		} catch (FileNotFoundException e) {
			// no worries here, we just haven't created the file yet.
			this.close();
			return;
		}
		
		
		String nextLine = this.getNextLine();
		if(nextLine != null){
			try{
				date = new DateTime(nextLine);
				nextLine = this.getNextLine();
			} catch(Exception ex){
				System.out.println("error parsing date from article sentiment - " + this.getFilename());
			}
		}

		articleSentimentKeywordMap = new HashMap<String, Double>();
		while(nextLine != null){
			String[] fields = nextLine.split(",");
			if(fields.length == 2 && fields[0] != null && fields[1] != null){
				articleSentimentKeywordMap.put(fields[0], Double.parseDouble(fields[1]));
			}
			
			nextLine = this.getNextLine();
		}
		
		this.close();
	}
	
	public NewsArticle getNewsArticle(){
		if(date == null){
			read();
		}
		return new NewsArticle(null, this.getFilename().replace(".csv", ""), this.date);
	}

}
