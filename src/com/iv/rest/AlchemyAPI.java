package com.iv.rest;

public interface AlchemyAPI {
	public int getNumTransactions();
	public String getArticleUrl();
	public RESTManager.AlchemyService getService();
}
