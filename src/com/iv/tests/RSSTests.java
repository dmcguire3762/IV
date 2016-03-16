package com.iv.tests;

import org.junit.Test;

import com.iv.parse.RSSManager;
import com.iv.parse.RSSParser;

public class RSSTests {

	/*@Test
	public void testGetRSSXML() {
		RSSParser parser = new RSSParser();
		System.out.println(parser.getXMLForRSSFeed("http://www.fool.com/About/headlines/rss_headlines.asp"));
	}
	*/
	@Test
	public void testGetXMLUrlsFromOMPL(){
		System.out.println("AllUrls");
		for(String url : RSSManager.getXMLUrlsFromOpml(RSSManager.feedlyURL)){
			RSSParser parser = new RSSParser(url);
			System.out.println(parser.parse());
		}
	}

}
