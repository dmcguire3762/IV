package com.iv.sentiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.iv.data.NewsArticle;

public class StockSentiment {
	private Set<String> urls = new HashSet<String>();
	private Map<String, HashSet<Double>> keywordSentimentMap = new HashMap<String, HashSet<Double>>();
	private final String ticker;
	private final String name;
	private double score = 0;
	private boolean reScore = true;
	
	public String getName(){ return name; }
	public String getTicker(){ return ticker; }
	
	public StockSentiment(String ticker, String name){
		this.ticker = ticker;
		this.name = name;
	}
	
	public void addArticleSentiment(ArticleSentiment article){
		if(urls.contains(article.getArticleUrl())){
			return;
		}
		
		reScore = true;
		urls.add(article.getArticleUrl());
		for(Map.Entry<String, Double> entry : article.getKeywordSentimentMap().entrySet()){
			if(!keywordSentimentMap.containsKey(entry.getKey())){
				keywordSentimentMap.put(entry.getKey(), new HashSet<Double>());
			}
			
			keywordSentimentMap.get(entry.getKey()).add(entry.getValue());
		}
	}
	
	@Override
	public String toString(){
		String outStr = "";
		outStr += ticker + "\n";
		outStr += name + "\n";
		
		for(Map.Entry<String, HashSet<Double>> keyword : keywordSentimentMap.entrySet()){
			outStr += keyword.getKey();
			for(Double sentiment : keyword.getValue()){
				outStr += ", " + sentiment.toString();
			}
			
			outStr += "\n\n";
		}
		
		return outStr;
	}
	
	public double getScore(){
		if(reScore == true){
			reScore = false;
			calculateScore();
		}
		
		return score;
	}
	
	/**
	 * Average the keyword sentiments, skipping any keywords that have a single entry.
	 */
	private void calculateScore() {
		double unaveragedScore = 0;
		double numSentiments = 0;
		for(Map.Entry<String, HashSet<Double>> keywordSentiments : keywordSentimentMap.entrySet()){
			if(keywordSentiments.getValue().size() > 1){
				for(Double sentiments : keywordSentiments.getValue()){
					unaveragedScore += sentiments;
					numSentiments++;
				}
			}
		}
		
		if(numSentiments == 0){
			score = 0;
		} else {
			score = unaveragedScore / numSentiments;
		}
	}
}
