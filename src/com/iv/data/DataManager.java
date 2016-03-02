package com.iv.data;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iv.csv.ValueJumpCSVWriter;
import com.iv.json.JsonUtils;
import com.iv.parse.ParseManager;

public class DataManager {
	private static double requiredPercentIncrease = 20.0;
	private static double requiredAbsoluteIncrease = 2.0;
	
	private static DataManager instance = null;
	private DataManager(){}
	public static DataManager getInstance(){
		if(instance == null){
			instance = new DataManager();
		}
		
		return instance;
	}
	
	public void evaluateAllStocks(){
		Set<String> tickers = ParseManager.getInstance().getAllTickers();
		for(String ticker : tickers){
			StockData newStockData = new StockData(ticker);
			newStockData.importCSVData();
			newStockData.calculateValueJumps();

			String filename = "C:\\Sandbox\\IV\\ValueJumpCSVs\\" + ticker + ".csv";
			ValueJumpCSVWriter valueJumpWriter = new ValueJumpCSVWriter(filename);
			valueJumpWriter.writeValueJumpList(newStockData.getValueJumps());
		}
	}
	
	public List<StockEvaluation> getStocksWithPercentRiseInLast60Days(double percentRise){
		List<StockEvaluation> validStocks = new ArrayList<StockEvaluation>();
		Set<String> tickers = ParseManager.getInstance().getAllTickers();
		
		DateTime endDate = DateTime.now();
		DateTime startDate = endDate.minusDays(60);
		
		for(String ticker : tickers){
			StockData newStockData = new StockData(ticker);
			newStockData.importCSVData();
			StockEvaluation eval = new StockEvaluation(startDate, endDate, newStockData);
			if(eval.getPercentDiff() > requiredPercentIncrease && eval.getAbsoluteDiff() > requiredAbsoluteIncrease){
				validStocks.add(eval);
			}
		}
		
		return validStocks;
	}
	
	public Set<NewsArticle> getNewsArticlesForDate(DateTime date){
		Set<NewsArticle> newsArticles = new HashSet<NewsArticle>();
		
		// get gson file for date
		JsonObject root = JsonUtils.getJsonObjectFromFile("Resources/xignite/"+date.toLocalDate()+".json");
		if(root != null){
			JsonArray headLines = root.getAsJsonArray("Headlines");
			for(JsonElement headLine : headLines){
				String url = headLine.getAsJsonObject().get("Url").toString().replaceAll("\"", "");
				String title = headLine.getAsJsonObject().get("Title").toString().replaceAll("\"", "");
				DateTime articleDate = getDateTimeForMMDDYYYY(headLine.getAsJsonObject().get("Date").toString().replaceAll("\"", ""));
				newsArticles.add(new NewsArticle(url, title, articleDate));
			}
		}
		
		return newsArticles;
	}
	
	private DateTime getDateTimeForMMDDYYYY(String date){
		String[] fields = date.replaceAll("\"",  "").split("/");
		if(fields.length != 3){
			return null;
		}
		
		String formattedDate = fields[2]+"-"+fields[0]+"-"+fields[1];
		return new DateTime(formattedDate);
	}
}
