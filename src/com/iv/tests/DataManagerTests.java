package com.iv.tests;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import com.iv.data.DataManager;
import com.iv.data.NewsArticle;
import com.iv.data.StockEvaluation;

public class DataManagerTests {

	/*@Test
	public void test() {
		//DataManager.getInstance().evaluateAllStocks();
		List<StockEvaluation> validStocks = DataManager.getInstance().getStocksWithPercentRiseInLast60Days(25);
		for(StockEvaluation stock : validStocks){
			System.out.println(stock.getTicker() + " | " + stock.getStartDate().toString() + " - " + stock.getEndDate().toString() + " | " + stock.getPercentDiff());
		}
	}*/
	
	@Test
	public void testReadingXigniteNews(){
		Set<NewsArticle> articles = (Set<NewsArticle>) DataManager.getInstance().getNewsArticlesForDate(new DateTime("2016-2-27"));
		for(NewsArticle article : articles){
			System.out.println(article.toString());
		}
		
		System.out.println("Total # of articles: " + articles.size());
	}

}
