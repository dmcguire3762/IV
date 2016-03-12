package com.iv.realtime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.iv.data.DataManager;
import com.iv.data.NewsArticle;
import com.iv.parse.ParseManager;

public class RealtimeEngine {
	private enum State{
		INIT,
		EVAL,
		UPDATE,
		EXIT
	}
	
	private State currentState = State.INIT;
	private ArrayList<RealtimeStockStatus> stockStatuses = new ArrayList<RealtimeStockStatus>();
	private HashMap<String, RealtimeStockStatus> tickerStockStatusMap = new HashMap<String, RealtimeStockStatus>();
	private HashMap<String, RealtimeStockStatus> companyNameStockStatusMap = new HashMap<String, RealtimeStockStatus>();
	private final String newsDirectory = "C:\\Sandbox\\IV\\Resources\\news\\";
	private final String evaluatedNewsDirectory = "C:\\Sandbox\\IV\\Resources\\evaluatedNews\\";
	
	public RealtimeEngine(){
		
	}
	
	public void start(){
		System.out.println("Starting realtime engine");
		while(true){
			switch(currentState){
				case INIT:
					System.out.println("Initializing realtime engine... ");
					init();
					break;
					
				case EVAL:
					System.out.println("Evaluating new articles");
					eval();
					break;
					
				case UPDATE:
					System.out.println("Updating stock sentiment scores");
					update();
					break;
					
				case EXIT:
					System.out.println("Exiting... ");
					return;
			}
		}
	}

	/**
	 * Create each real time status, then:
	 * 
	 * 1. Gather all of the already-parsed news articles, create article sentiments from those, 
	 * and then add them to the real time status.
	 */
	private void init() {
		stockStatuses.clear();
		tickerStockStatusMap.clear();
		companyNameStockStatusMap.clear();
		
		for(Map.Entry<String, String> companyNameTicker : ParseManager.getInstance().getTickerCompanyNameMap().entrySet()){
			RealtimeStockStatus stockStatus = new RealtimeStockStatus(companyNameTicker.getKey(), companyNameTicker.getValue());
			stockStatuses.add(stockStatus);
			tickerStockStatusMap.put(companyNameTicker.getValue(), stockStatus);
			companyNameStockStatusMap.put(companyNameTicker.getKey(), stockStatus);
		}
		
		parseAndApplyNewsArticles(DataManager.getInstance().getAllEvaluatedNewsArticles());
		currentState = State.EVAL;
	}


	private void eval() {
		// Get the new news articles from news jsons
		File[] jsonFiles = new File(newsDirectory).listFiles();
		
		HashSet<NewsArticle> newsArticles = new HashSet<NewsArticle>();
		for(File jsonFile : jsonFiles){
			if(!jsonFile.getAbsolutePath().endsWith(".json")){
				System.out.println("Invalid file in the news directory: " + jsonFile.getAbsolutePath());
				continue;
			}
			
			newsArticles.addAll(DataManager.getInstance().getNewsArticlesForFile(jsonFile.getAbsolutePath()));
			jsonFile.renameTo(new File(evaluatedNewsDirectory + jsonFile.getName()));
		}
		
		parseAndApplyNewsArticles(newsArticles);
		
		for(RealtimeStockStatus stockStatus : stockStatuses){
			stockStatus.start();
		}
		
		for(RealtimeStockStatus stockStatus : stockStatuses){
			try {
				stockStatus.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		currentState = State.UPDATE;
	}
	
	private void parseAndApplyNewsArticles(Collection<NewsArticle> newsArticles){
		List<ArticleParser> parsers = new ArrayList<ArticleParser>();
		for(NewsArticle article : newsArticles){
			ArticleParser parser = new ArticleParser(article);
			parsers.add(parser);
			parser.start();
		}
		
		for(ArticleParser parser : parsers){
			try {
				parser.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
		applyArticlesToStocks(parsers);
	}
	
	private void applyArticlesToStocks(List<ArticleParser> parsers) {
		for(ArticleParser parser : parsers){
			for(String keyword : parser.getArticleSentiment().getKeywordSentimentMap().keySet()){
				if(tickerStockStatusMap.containsKey(keyword)){
					tickerStockStatusMap.get(keyword).addArticleSentiment(parser.getArticleSentiment());
				}
				
				if(companyNameStockStatusMap.containsKey(keyword)){
					companyNameStockStatusMap.get(keyword).addArticleSentiment(parser.getArticleSentiment());
				}
			}
		}
	}

	private void update() {
		Collections.sort(stockStatuses, new RealtimeStockScoreSorter());
		for(RealtimeStockStatus stockStatus : stockStatuses){
			if(stockStatus.getWeightedScore() == 0.0){
				continue;
			}
			System.out.println(stockStatus.getTicker() + "\t| " + stockStatus.getWeightedScore());
			System.out.print("\t");
			for(Score dailyScore : stockStatus.getNonAdjustedScores()){
				System.out.print(dailyScore.getDate() + " : " + dailyScore.getScore() + " | ");
			}
			System.out.println("");
			System.out.println("------------------------------------------------------------------");
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		currentState = State.EXIT;
	}
	
	private class RealtimeStockScoreSorter implements Comparator<RealtimeStockStatus>{

		@Override
		public int compare(RealtimeStockStatus o1, RealtimeStockStatus o2) {
			if(o1.getWeightedScore() < o2.getWeightedScore()){
				return -1;
			} else if (o1.getWeightedScore() > o2.getWeightedScore()){
				return 1;
			} else if (o2.getWeightedScore() == o1.getWeightedScore()){
				return 0;
			} else {
				throw new RuntimeException("Comparator failed for " + o1.getTicker() + " : " + o1.getWeightedScore() + " and " + o2.getTicker() + " : " + o2.getWeightedScore());
			}
		}
		
	}

}
