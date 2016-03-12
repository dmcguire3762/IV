package com.iv.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iv.csv.ArticleSentimentCSVReader;
import com.iv.csv.CSVReader;
import com.iv.csv.CSVWriter;
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
	
	@Deprecated
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
		
		// get gson file for date
		JsonObject root = JsonUtils.getJsonObjectFromFile("Resources/xignite/"+date.toLocalDate()+".json");
		return getNewsArticlesFromJsonObject(root);
	}
	
	public Set<NewsArticle> getNewsArticlesForFile(String filename){
		JsonObject root = JsonUtils.getJsonObjectFromFile(filename);
		return getNewsArticlesFromJsonObject(root);
	}
	
	private Set<NewsArticle> getNewsArticlesFromJsonObject(JsonObject root){
		Set<NewsArticle> newsArticles = new HashSet<NewsArticle>();
		
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
	
	public List<NewsArticle> getAllEvaluatedNewsArticles(){
		File articleFolder = new File("C:\\Sandbox\\IV\\Resources\\evaluatedArticles");
		File[] allArticles = articleFolder.listFiles();
		List<NewsArticle> newsArticles = new ArrayList<NewsArticle>();
		
		for(File evaluatedArticle : allArticles){
			ArticleSentimentCSVReader reader = new ArticleSentimentCSVReader(evaluatedArticle);
			try {
				reader.open();
				newsArticles.add(reader.getNewsArticle());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally{
				reader.close();
			}
		}
		
		return newsArticles;
	}
	
	public static void addDateToEvaluatedArticles(){
		File articleFolder = new File("C:\\Sandbox\\IV\\Resources\\evaluatedArticles");
		File[] allArticles = articleFolder.listFiles();
		
		for(File article : allArticles){
			DateTime date = new DateTime(article.lastModified());
			
			CSVWriter writer = new CSVWriter(article.getAbsolutePath());
			writer.addLine(date.toString() + "\r\n");
			
			CSVReader reader = new CSVReader(article.getAbsolutePath());
			try {
				reader.open();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			
			String nextLine = reader.getNextLine();
			while(nextLine != null){
				writer.addLine(nextLine);
				nextLine = reader.getNextLine();
			}
			
			reader.close();
			try{
				writer.open();
				writer.write();
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public static void removeAllCarraigeReturnsFromEvaluatedArticles(){
		File articleFolder = new File("C:\\Sandbox\\IV\\Resources\\evaluatedArticles");
		File[] allArticles = articleFolder.listFiles();
		
		for(File article : allArticles){
			String strFile = null;
			try {
				 strFile = JsonUtils.readFile(article.getAbsolutePath(), Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			try {
				JsonUtils.writeFile(article.getAbsolutePath(), strFile.replace("\r", "").getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public static void addNewLineToEvaluatedArticles(){
		//File articleFolder = new File("C:\\Sandbox\\IV\\Resources\\evaluatedArticles");
		File articleFolder = new File("C:\\Sandbox\\IV\\Resources\\test");
		File[] allArticles = articleFolder.listFiles();
		
		for(File article : allArticles){
			CSVReader reader = new CSVReader(article.getAbsolutePath());
			CSVWriter writer = new CSVWriter(article.getAbsolutePath());
			
			try {
				reader.open();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				reader.close();
				continue;
			}
			
			String nextLine = reader.getNextLine(); // skip date
			nextLine = reader.getNextLine();
			if(nextLine != null){
				String[] fields = nextLine.split(",");
				for(int i = 0; i < fields.length; i+=2){
					writer.addLine(fields[i] + "," + fields[i+1] + "\n");
				}
			}

			reader.close();
			writer.write();
		}
	}
}
