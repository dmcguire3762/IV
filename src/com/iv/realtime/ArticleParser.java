package com.iv.realtime;

import com.iv.data.NewsArticle;
import com.iv.sentiment.ArticleSentiment;

public class ArticleParser extends Thread{
	NewsArticle article = null;
	ArticleSentiment sentiment = null;
	
	public ArticleParser(NewsArticle newsArticle){
		article = newsArticle;
		sentiment = new ArticleSentiment(article);
	}
	
	@Override
	public void run(){
		sentiment.evaluate();
	}
	
	public ArticleSentiment getArticleSentiment() { return sentiment; }
	public NewsArticle getNewsArticle(){ return article; }
}
