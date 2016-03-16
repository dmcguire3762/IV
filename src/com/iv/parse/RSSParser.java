package com.iv.parse;

import org.w3c.dom.Document;

import com.iv.json.XMLUtils;

public class RSSParser {
	private final String url;
	
	public RSSParser(String url){
		this.url = url;
	}
	
	public String parse(){
		System.out.println("Getting RSS feed XML from " + url);
		String filteredXML = filterXMLString(ParseManager.getInstance().getURLAsString(url));
		
		System.out.println(filteredXML);
		
		Document RSSXML = XMLUtils.xmlStringToDocument(filteredXML);
		
		return RSSXML.toString();
	}
	
	private String filterXMLString(String rssXML){
		return rssXML.replaceAll("<content:encoded>(.+?)</content:encoded>", "<content:encoded></content:encoded>")
				.replaceAll("<description>(.+?)<description>", "<description></description>");
	}
	
	//public List<String> getAllLinks
}
