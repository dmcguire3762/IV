package com.iv.realtime;

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
import org.joda.time.LocalDate;
import com.iv.sentiment.ArticleSentiment;

public class RealtimeStockStatus extends Thread{
	private final String ticker;
	private final String companyName;
	
	private ArrayList<Score> nonAdjustedScores = new ArrayList<Score>();
	private double dateWeightedScore = 0;
	private HashMap<LocalDate, HashMap<String, HashSet<Double>>> keywordSentimentsByDate = 
			new HashMap<LocalDate, HashMap<String, HashSet<Double>>>();
	private Set<String> latestValidKeywords = new HashSet<String>();
	private HashMap<LocalDate, HashSet<String>> dateArticleUrlMap = new HashMap<LocalDate, HashSet<String>>();
	
	public RealtimeStockStatus(String ticker, String companyName){
		this.ticker = ticker;
		this.companyName = companyName;
	}
	
	public String getTicker(){ return ticker; }
	public String getCompanyName(){ return companyName; }
	public double getWeightedScore(){ return dateWeightedScore; }
	public ArrayList<Score> getNonAdjustedScores(){
		Collections.sort(nonAdjustedScores, new ScoreDateSorter(-1));
		return nonAdjustedScores; 
	}
	public Set<String> getLatestValidKeywords(){ return latestValidKeywords; }
	public Map<LocalDate, HashSet<String>> getDateArticleUrlMap(){ return dateArticleUrlMap; }
	
	public void addArticleSentiment(ArticleSentiment sentiment){
		LocalDate sentimentDate = sentiment.getDate().toLocalDate();

		if(!dateArticleUrlMap.containsKey(sentimentDate)){
			dateArticleUrlMap.put(sentimentDate, new HashSet<String>());
		}
		
		if(dateArticleUrlMap.get(sentimentDate).contains(sentiment.getArticleTitle())){
			return;
		} else {
			dateArticleUrlMap.get(sentimentDate).add(sentiment.getArticleTitle());
		}
		
		if(!keywordSentimentsByDate.containsKey(sentimentDate)){
			keywordSentimentsByDate.put(sentimentDate, new HashMap<String, HashSet<Double>>());
		}
		
		HashMap<String, HashSet<Double>> keywordSentimentsMap = keywordSentimentsByDate.get(sentimentDate);
		for(Map.Entry<String, Double> keywordSentiment : sentiment.getKeywordSentimentMap().entrySet()){
			if(!keywordSentimentsMap.containsKey(keywordSentiment.getKey())){
				keywordSentimentsMap.put(keywordSentiment.getKey(), new HashSet<Double>());
			}
			
			keywordSentimentsMap.get(keywordSentiment.getKey()).add(keywordSentiment.getValue());
		}
	}
	
	
	@Override
	public void run(){
		for(Map.Entry<LocalDate, HashMap<String, HashSet<Double>>> keywordSentimentForDate : keywordSentimentsByDate.entrySet()){
			nonAdjustedScores.add(new Score(keywordSentimentForDate.getKey(), calcScoreForKeywordSentimentsMap(keywordSentimentForDate.getValue())));
		}
		
		List<Score> scoresByDate = new ArrayList<Score>(nonAdjustedScores);
		Collections.sort(scoresByDate, new ScoreDateSorter());
		
		double numerator = 0;
		double denominator = 0;
		for(Score nonAdjustedScore : nonAdjustedScores){
			if(nonAdjustedScore.getScore() == 0.0){
				continue;
			}
			int daysOld = Days.daysBetween(nonAdjustedScore.getDate(), DateTime.now().toLocalDate()).getDays();
			double weight = getWeight(daysOld, nonAdjustedScores.size());
			
			numerator += nonAdjustedScore.getScore() * weight;
			denominator += weight;
		}
		
		if(denominator == 0){
			dateWeightedScore = 0;
		} else {
			dateWeightedScore = numerator / denominator;
		}
	}
	
	private double getWeight(int daysToEndDate, int numDates) {
		if(daysToEndDate < 1){
			daysToEndDate = 0;
		}
		
		double weight = ((1.0 / ((double)daysToEndDate + 1.0)) / (double)numDates);
		return weight;
	}
	
	
	/**
	 * Average the keyword sentiments, skipping any keywords that have a single entry.
	 */
	private double calcScoreForKeywordSentimentsMap(HashMap<String, HashSet<Double>> keywordSentimentsForDate) {
		double unaveragedScore = 0;
		int numSentiments = 0;
		Set<String> validKeywords = determineKeywords();
		for(Map.Entry<String, HashSet<Double>> keywordSentiments : keywordSentimentsForDate.entrySet()){
			if(validKeywords.contains(keywordSentiments.getKey())){
				for(Double sentiments : keywordSentiments.getValue()){
					unaveragedScore += sentiments;
					numSentiments++;
				}
			}
		}
		
		if(numSentiments == 0){
			return 0;
		} else {
			return unaveragedScore / (double)numSentiments;
		}
	}
	
	/**
	 * Iterate through all dates, and find the keywords that have more than one entry.
	 * @return all keywords that have more than one entry.
	 */
	private Set<String> determineKeywords() {
		HashMap<String, Integer> keywordCount = new HashMap<String, Integer>();
		for(Map.Entry<LocalDate, HashMap<String, HashSet<Double>>> keywordSentimentForDate : keywordSentimentsByDate.entrySet()){
			for(Map.Entry<String, HashSet<Double>> keywordSentiment : keywordSentimentForDate.getValue().entrySet()){
				if(!keywordCount.containsKey(keywordSentiment.getKey())){
					keywordCount.put(keywordSentiment.getKey(), 0);
				}
				
				keywordCount.put(keywordSentiment.getKey(), keywordCount.get(keywordSentiment.getKey()) + 1);
			}
		}
		
		Set<String> validKeywords = new HashSet<String>();
		for(Map.Entry<String, Integer> keyword : keywordCount.entrySet()){
			if(keyword.getValue() > 0){
				validKeywords.add(keyword.getKey());
			}
		}
		
		validKeywords.add(this.getCompanyName());
		validKeywords.add(this.getTicker());
		
		latestValidKeywords = validKeywords;
		return validKeywords;
	}

	private class ScoreDateSorter implements Comparator<Score>{
		private final int direction;
		public ScoreDateSorter(int direction){
			if(direction > 0){
				this.direction = 1;
			} else if(direction < 0){
				this.direction = -1;
			} else {
				this.direction = 0;
			}
		}
		
		public ScoreDateSorter(){
			direction = 1;
		}
		
		@Override
		public int compare(Score arg0, Score arg1) {
			int value;
			if(arg0.getDate().isBefore(arg1.getDate())){
				value = -1;
			} else if(arg0.getDate().equals(arg1.getDate())){
				value = 0;
			} else {
				value = 1;
			}
			
			return value * direction;
		}
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result + ((ticker == null) ? 0 : ticker.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeStockStatus other = (RealtimeStockStatus) obj;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (ticker == null) {
			if (other.ticker != null)
				return false;
		} else if (!ticker.equals(other.ticker))
			return false;
		return true;
	}
}
