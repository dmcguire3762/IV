package com.iv.realtime;

import com.iv.data.NewsArticle;
import com.iv.sentiment.ArticleSentiment;

public class ArticleParser extends Thread{
	NewsArticle article = null;
	ArticleSentiment sentiment = null;
	
	public ArticleParser(NewsArticle newsArticle){
		article = newsArticle;
	}
	
	@Override
	public void run(){
		System.out.println("Parsing article - " + article.getTitle());
		sentiment = new ArticleSentiment(article);
		sentiment.evaluate();
	}
	
	public ArticleSentiment getArticleSentiment() { return sentiment; }
}
