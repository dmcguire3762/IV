package com.iv.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;
import com.iv.json.JsonUtils;

public class RESTManager {
	private final static String alchemyBaseUrl = "http://gateway-a.watsonplatform.net/calls/url/";
	private final static String alchemyKeywordServiceUrl = "URLGetRankedKeywords";
	private final static int transactionLimit = 1000;
	
	private static Set<AlchemyAPIKey> alchemyAPIKeys = new HashSet<AlchemyAPIKey>();
	static{
		alchemyAPIKeys.add(new AlchemyAPIKey("72a5f8e6c0e85a47397086542b9e845024bf04bd"));
		alchemyAPIKeys.add(new AlchemyAPIKey("b808316798b5d32ce4d463dd572b916160ec72d8"));
		alchemyAPIKeys.add(new AlchemyAPIKey("0eb3f26b83a2786e9610d19e8135cc9bf5302ee4"));
		alchemyAPIKeys.add(new AlchemyAPIKey("2fc96c1ac120cd2a49959a1eb5f15efec8b06959"));
		alchemyAPIKeys.add(new AlchemyAPIKey("7e7eff43fc2178fd5cb2ab801903809bf45fb30f"));
		alchemyAPIKeys.add(new AlchemyAPIKey("932525c6b28f7da2632ec37e916f3da7f0681a5d"));
		alchemyAPIKeys.add(new AlchemyAPIKey("81ea8486b9ccc67744857e8c2d0a21f6386dfd03"));
		
		
	}
	
	public enum AlchemyService{
		KEYWORD,
		SENTIMENT
	}
	
	public static String getAlchemyUrl(AlchemyAPI api){
		String formattedUrl = alchemyBaseUrl;
		switch(api.getService()){
			case KEYWORD:
				formattedUrl+=alchemyKeywordServiceUrl + "?outputMode=json&";
				break;
			
			default:
				return null;
		}
		
		String apiKey = getApiKey(api);
		if(apiKey == null){
			return null;
		}
		
		formattedUrl += "apikey="+ apiKey + "&";
		formattedUrl += "url=" + api.getArticleUrl() + "&";
		formattedUrl += "sentiment=1";
		
		return formattedUrl;
	}

	private static String getApiKey(AlchemyAPI api) {
		for(AlchemyAPIKey key : alchemyAPIKeys){
			if(key.getTransactionCount() <= 0){
				key.setTransactionCount(requestTransactionCount(key.getKey()));
			}
			if(key.getTransactionCount() < transactionLimit){
				key.addTransactions(api.getNumTransactions());
				return key.getKey();
			}
		}
		
		return null;
	}
	
	
	private static int requestTransactionCount(String key) {
		String url = "http://access.alchemyapi.com/calls/info/GetAPIKeyInfo?apikey=" + key + "&outputMode=json";
		JsonObject response = getRequest(url);
		return response.get("consumedDailyTransactions").getAsInt();
	}
	
	public static synchronized JsonObject getRequest(String url){
		try{
			System.out.println("Evaluating keywords for " + url);
			URL restServiceURL = null;
			try{
				restServiceURL = new URL(url);
			} catch( MalformedURLException ex ){
				System.out.println(url);
				throw new RuntimeException(ex);
			}
	
			HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("Accept", "application/json");
	
			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException("HTTP GET Request Failed with Error code : "
						+ httpConnection.getResponseCode());
			}
	
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
				(httpConnection.getInputStream())));
	
			String line, doc = "";
			while ((line = responseBuffer.readLine()) != null) {
				doc += line;
			}
	
			httpConnection.disconnect();
			
			if(doc.contains("daily-transaction-limit-exceeded")){
				throw new RuntimeException("Daily transaction limit exceeded - not able to proceed");
			}
			
			return JsonUtils.getJsonObjectFromString(doc);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonObject getRequest(AlchemyAPI api){
		return getRequest(getAlchemyUrl(api));
	}
}
