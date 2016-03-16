package com.iv.parse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.iv.csv.CompanyListReader;


public class ParseManager {
	private Map<String, String> tickerMap = null;
	
	private static ParseManager instance = null;
	private ParseManager(){}
	public static ParseManager getInstance(){
		if(instance == null){
			instance = new ParseManager();
		}
		
		return instance;
	}
	
	public void writeCSVForAllTickers(){
		populateTickers();
		for(String ticker : tickerMap.keySet()){
			StockParser stockData = new StockParser(ticker);
			InputStream input = null;
			try{
				System.out.println("Parsing: " + ticker + " | "+ stockData.getFormattedURL());
				input = new URL(stockData.getFormattedURL()).openStream();
				File newCSV = new File("C:\\Sandbox\\IV\\StockHistoryCSVs\\"+ticker+".csv");
				Files.copy(input, newCSV.toPath());
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try{
					if(input != null){
						input.close();
					}
				} catch (Exception ex){}
			}
			
			try {
				// try sleeping briefly... don't want to ddos or be flagged or anything.
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getURLAsString(String url){
		InputStream input = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla");///5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
			input = connection.getInputStream();
			return IOUtils.toString(input, Charset.defaultCharset());
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				input.close();
			} catch (IOException e) { }
		}
	}
	
	private void populateTickers(){
		if(tickerMap == null){
			CompanyListReader companyInfoReader = new CompanyListReader("C:\\Sandbox\\IV\\Resources\\companylist.csv");
			companyInfoReader.ReadFile();
			tickerMap = companyInfoReader.getCompanyTickerMap();
		}
	}
	
	public Set<String> getAllTickers(){
		populateTickers();
		return tickerMap.keySet();
	}
	
	public Map<String, String> getTickerCompanyNameMap(){
		populateTickers();
		return tickerMap;
	}
	
	
}
