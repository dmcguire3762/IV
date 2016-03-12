package com.iv.sentiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.iv.data.DataManager;
import com.iv.data.NewsArticle;
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
	
	public Set<StockSentiment> getAllEmptyStockSentiments(DateTime date) {
		Map<String, String> tickersAndNames = ParseManager.getInstance().getTickerCompanyNameMap();
		Set<StockSentiment> stocks = new HashSet<StockSentiment>();
		
		for(Map.Entry<String, String> tickerNameCombo : tickersAndNames.entrySet()){
			stocks.add(new StockSentiment(tickerNameCombo.getKey(), tickerNameCombo.getValue(), date));
		}
		
		return stocks;
	}
	
	public static class StockScoreSorter implements Comparator<StockSentiment>{
		@Override
		public int compare(StockSentiment arg0, StockSentiment arg1) {
			return arg0.getScore() < arg1.getScore() ? -1 : ((arg0.getScore() == arg1.getScore()) ? -1 : 1 );
		}
	}
	
	private class StockDateSorter implements Comparator<StockSentiment>{
		@Override
		public int compare(StockSentiment arg0, StockSentiment arg1) {
			return arg0.getDate().isBefore(arg1.getDate()) ? -1 : (arg0.getDate().equals(arg1.getDate()) ? 0 : 1 );
		}
	}
	
	public double getScoreForStockWeightedByDate(List<StockSentiment> stockSentiments, DateTime endDate){ 
		Collections.sort(stockSentiments, new StockDateSorter());
		HashMap<Double, Double> scoreWeightMap = new HashMap<Double, Double>();
		
		for(StockSentiment stockForDate : stockSentiments){
			scoreWeightMap.put(stockForDate.getScore(), getWeight(Days.daysBetween(endDate.toLocalDate(), stockForDate.getDate().toLocalDate()).getDays(), stockSentiments.size()));
		}
		
		double numerator = 0;
		double denominator = 0;
		
		for(Map.Entry<Double, Double> scoreWeight : scoreWeightMap.entrySet()){
			numerator += (scoreWeight.getKey() * scoreWeight.getValue());
			denominator += scoreWeight.getValue();
		}
		
		return numerator / denominator;
	}
	
	private double getWeight(int daysToEndDate, int numStocks) {
		return ((1.0 / ((double)daysToEndDate + 1.0)) / (double)numStocks);
	}
	
	public Map<String, Double> getWeightedStockScoresForDateRange(DateTime startDate, DateTime endDate){
		HashMap<String, ArrayList<StockSentiment>> allSentimentsForEachStock = new HashMap<String, ArrayList<StockSentiment>>();
		for(DateTime currentDate = startDate; currentDate.isBefore(endDate.plusDays(1)); currentDate = currentDate.plusDays(1)){
			System.out.println("Parsing articles for " + currentDate.toString());
			

			Set<StockSentiment> stocks = SentimentManager.getInstance().getAllEmptyStockSentiments(currentDate);
			Set<NewsArticle> articles = DataManager.getInstance().getNewsArticlesForDate(currentDate);
			Set<ArticleSentiment> articleSentiments = new HashSet<ArticleSentiment>();
			for(NewsArticle article : articles){
				articleSentiments.add(new ArticleSentiment(article));
			}
			
			SentimentManager.getInstance().populateStockSentimentsWithArticleSentiments(stocks, articleSentiments);

			ArrayList<StockSentiment> stockList = new ArrayList<StockSentiment>();
			for(StockSentiment stock : stocks){
				if(stock.getScore() != 0){
					stockList.add(stock);
				}
			}
			
			for(StockSentiment stock : stockList){
				if(!allSentimentsForEachStock.containsKey(stock.getTicker())){
					allSentimentsForEachStock.put(stock.getTicker(), new ArrayList<StockSentiment>());
				}
				
				allSentimentsForEachStock.get(stock.getTicker()).add(stock);
			}
		}

		HashMap<String, Double> tickerWeightedScoreMap = new HashMap<String, Double>();
		for(Map.Entry<String, ArrayList<StockSentiment>> allSentimentsForOneStock : allSentimentsForEachStock.entrySet()){
			Collections.sort(allSentimentsForOneStock.getValue(), new StockDateSorter());
			System.out.print(allSentimentsForOneStock.getKey() + "\t");
			for(StockSentiment singleSentiment : allSentimentsForOneStock.getValue()){
				System.out.print(singleSentiment.getScore() + "\t");
			}
			
			double weightedScore = getScoreForStockWeightedByDate(allSentimentsForOneStock.getValue(), endDate);
			System.out.print(weightedScore + "\n");
			
			tickerWeightedScoreMap.put(allSentimentsForOneStock.getKey(), weightedScore);
		}
		
		return tickerWeightedScoreMap;
	}
	
}
