package com.iv.sentiment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.iv.data.DayEarnings;
import com.iv.parse.ParseManager;

public class SentimentManager {
	
	private static SentimentManager instance = null;
	private SentimentManager(){}
	public static SentimentManager getInstance(){
		if(instance == null){
			instance = new SentimentManager();
		}
		
		return instance;
	}
	
	public void populateStockSentimentsWithArticleSentiments(Set<StockSentiment> stockSentiments, Set<ArticleSentiment> articleSentiments){
		HashMap<String, ArrayList<ArticleSentiment>> keywordArticleMap = new HashMap<String, ArrayList<ArticleSentiment>>();
		for(ArticleSentiment article : articleSentiments){
			for(String keyword : article.getKeywords()){
				if(!keywordArticleMap.containsKey(keyword)){
					keywordArticleMap.put(keyword, new ArrayList<ArticleSentiment>());
				}
				
				keywordArticleMap.get(keyword).add(article);
			}
		}
		
		for(StockSentiment stock : stockSentiments){
			ArrayList<ArticleSentiment> articles = keywordArticleMap.get(stock.getName());
			if(articles != null){
				for(ArticleSentiment article : articles){
					stock.addArticleSentiment(article);
				}
			}
			
			articles = keywordArticleMap.get(stock.getTicker());
			if(articles != null){
				for(ArticleSentiment article : articles){
					stock.addArticleSentiment(article);
				}
			}
		}
	}
	
	private boolean isStockNameInArticle(String name, Set<String> keywords) {
		for(String keyword : keywords){
			if(keyword.contains(name) || name.contains(keyword)){
				return true;
			}
		}
		
		return false;
	}
	
	public Set<StockSentiment> getAllEmptyStockSentiments() {
		Map<String, String> tickersAndNames = ParseManager.getInstance().getTickerCompanyNameMap();
		Set<StockSentiment> stocks = new HashSet<StockSentiment>();
		
		for(Map.Entry<String, String> tickerNameCombo : tickersAndNames.entrySet()){
			stocks.add(new StockSentiment(tickerNameCombo.getKey(), tickerNameCombo.getValue()));
		}
		
		return stocks;
	}
	
	public static class StockScoreSorter implements Comparator<StockSentiment>{
		
		@Override
		public int compare(StockSentiment arg0, StockSentiment arg1) {
			return arg0.getScore() < arg1.getScore() ? -1 : ((arg0.getScore() == arg1.getScore()) ? -1 : 1 );
		}
	}
}
