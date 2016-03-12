package com.iv.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.iv.sentiment.ArticleSentiment;

public class ArticleSentimentCSVWriter extends CSVWriter{

	public ArticleSentimentCSVWriter(String filename) {
		super(filename);
	}
	
	public void writeArticleSentiment(ArticleSentiment article){
		this.addLine(article.getDate().toString() + "\n");
		for(Map.Entry<String, Double> keywordSentimentPair : article.getKeywordSentimentMap().entrySet()){
			List<String> params = new ArrayList<String>();
			params.add(keywordSentimentPair.getKey());
			params.add(keywordSentimentPair.getValue().toString());
			this.addLine(params);
		}

		try {
			this.open();
			this.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
