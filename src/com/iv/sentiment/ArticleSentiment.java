package com.iv.sentiment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iv.csv.ArticleSentimentCSVReader;
import com.iv.csv.ArticleSentimentCSVWriter;
import com.iv.data.NewsArticle;
import com.iv.rest.AlchemyAPI;
import com.iv.rest.RESTManager;
import com.iv.rest.RESTManager.AlchemyService;

public class ArticleSentiment implements AlchemyAPI{
	private final NewsArticle article;
	private HashMap<String, Double> keywordSentimentMap;
	
	public ArticleSentiment(NewsArticle article) {
		this.article = article;
	}
	
	public Set<String> getKeywords(){
		evaluate();
		return keywordSentimentMap.keySet();
	}
	
	public Map<String, Double> getKeywordSentimentMap(){
		if(keywordSentimentMap == null){
			evaluate();
		}
		
		return keywordSentimentMap;
	}
	
	public void evaluate(){
		if(keywordSentimentMap != null){
			return;
		}
		
		readFromFile();
		if(keywordSentimentMap == null && article.getURL() != null){
			keywordSentimentMap = new HashMap<String, Double>();
			JsonObject root = RESTManager.getRequest(this);
			if(root != null){
				JsonArray keywordElements = root.getAsJsonArray("keywords");
				if(keywordElements == null){
					writeToFile();
					return;
				}
				for(JsonElement keywordElement : keywordElements){
					if(keywordElement == null){
						continue;
					}
					
					JsonElement text = keywordElement.getAsJsonObject().get("text");
					JsonElement sentiment = keywordElement.getAsJsonObject().get("sentiment");
					JsonElement score = null;
					if(sentiment != null){
						score = sentiment.getAsJsonObject().get("score");
					}
					if(text != null && score != null){
						keywordSentimentMap.put(text.toString().replaceAll("\"", ""), Double.parseDouble(score.toString().replaceAll("\"", "")));
					}
				}
			}
			
			writeToFile();
		}
	}

	private void readFromFile() {
		ArticleSentimentCSVReader reader = new ArticleSentimentCSVReader(getCSVFileName());
		keywordSentimentMap = reader.getArticleSentimentKeywordMap();
	}

	private void writeToFile() {
		ArticleSentimentCSVWriter writer = new ArticleSentimentCSVWriter(getCSVFileName());
		writer.writeArticleSentiment(this);
	}
	
	@Override
	public int getNumTransactions() {
		return 2;
	}

	@Override
	public String getArticleUrl() {
		return article.getURL();
	}
	
	public String getArticleTitle(){
		return article.getTitle();
	}

	@Override
	public AlchemyService getService() {
		return RESTManager.AlchemyService.KEYWORD;
	}
	
	@Override
	public String toString(){
		evaluate();
		String outStr = article.toString();
		for(Map.Entry<String, Double> keywordSentiment: keywordSentimentMap.entrySet()){
			outStr += keywordSentiment.getKey() + " | " + keywordSentiment.getValue() + "\n";
		}
		
		return outStr;
	}
	
	private String getCSVFileName(){
		return "Resources\\evaluatedArticles\\"+article.getTitle()
				.replace("\\", "")
				.replace(":", "")
				.replace("/", "")
				.replace("*", "")
				.replace("?", "")
				.replace("<", "")
				.replace(">", "")
				+".csv";
	}

	public DateTime getDate() {
		return article.getDate();
	}

}
