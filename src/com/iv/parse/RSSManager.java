package com.iv.parse;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iv.json.XMLUtils;

public class RSSManager {
	public static String feedlyURL = "http://feedly.com/v3/opml?feedlyToken=AwqmlE1t1onvdB5pFle6uAoXEPcybEjM3vIWesZA_nN5y9FynXa8a9oIbhc_wpDnT7g8k5vjnf0MBPawB_46eXX05W48bIJXOJRmBo6_Mpah9Rah4XzX2XF8ZTNMF_0dKgZ6mQrtyGX-SqrgOZUqIcNgcdyKAT61mZQpq-rVTYkyJMS5KnN3T51XNbo5QQESc8_BkLqLD41q1qSGYx4%3Afeedly";
	
	
	public static Set<String> getXMLUrlsFromOpml(String url){
		Set<String> xmlUrls = new HashSet<String>();
		String feedlyXMLStr = ParseManager.getInstance().getURLAsString(url);
		Document feedlyXMLDoc = XMLUtils.xmlStringToDocument(feedlyXMLStr);
		
		Element feedlyXMLRoot = feedlyXMLDoc.getDocumentElement();
		NodeList allOutlines = feedlyXMLRoot.getElementsByTagNameNS("*", "outline");
		for(int i = 0; i < allOutlines.getLength(); i++){
			Node outline = allOutlines.item(i);
			Node urlNode = outline.getAttributes().getNamedItem("xmlUrl");
			if(urlNode != null){
				xmlUrls.add(urlNode.getNodeValue());
			}
		}
		
		return xmlUrls;
	}
}
