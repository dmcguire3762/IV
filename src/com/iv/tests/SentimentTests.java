package com.iv.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.DateTime;
import org.junit.Test;
import com.iv.data.DataManager;
import com.iv.data.DayEarnings;
import com.iv.data.NewsArticle;
import com.iv.data.StockData;
import com.iv.data.StockEvaluation;
import com.iv.sentiment.ArticleSentiment;
import com.iv.sentiment.SentimentManager;
import static com.iv.sentiment.SentimentManager.StockScoreSorter;
import com.iv.sentiment.StockSentiment;

public class SentimentTests {

	@Test
	public void test() {
		Set<NewsArticle> articles = DataManager.getInstance().getNewsArticlesForDate(new DateTime("2016-3-02"));
		Set<ArticleSentiment> articleSentiments = new HashSet<ArticleSentiment>();
		
		for(NewsArticle article : articles){
			articleSentiments.add(new ArticleSentiment(article));
		}
		
		Set<StockSentiment> stocks = SentimentManager.getInstance().getAllEmptyStockSentiments();
		SentimentManager.getInstance().populateStockSentimentsWithArticleSentiments(stocks, articleSentiments);

		ArrayList<StockSentiment> stockList = new ArrayList<StockSentiment>();
		for(StockSentiment stock : stocks){
			if(stock.getScore() != 0){
				stockList.add(stock);
			}
		}
		
		Collections.sort(stockList, new StockScoreSorter());
		for(StockSentiment stock : stockList){
			StockData dataForStock = new StockData(stock.getTicker());
			dataForStock.importCSVData();
			StockEvaluation eval = new StockEvaluation(new DateTime("2016-3-02"), DateTime.now(), dataForStock);
			/*DayEarnings earningsOnDay = dataForStock.getEarningsForDay();
			DayEarnings earningsForToday = dataForStock.getEarningsForDay(DateTime.now());*/
			
			
			System.out.println(stock.getTicker() + " | " + stock.getName() + " | " + stock.getScore() + " | " + eval.getPercentDiff());
		}
	}

}
