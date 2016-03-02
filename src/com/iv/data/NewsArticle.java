package com.iv.data;

import org.joda.time.DateTime;

public class NewsArticle {
	private final String url;
	private final String title;
	private final DateTime date;
	
	public NewsArticle(String url, String title, DateTime date){
		this.date = date;
		this.title = title;
		this.url = url;
	}
	
	public String getURL(){ return url; }
	public String getTitle(){ return title; }
	public DateTime getDate(){ return date; }
	
	@Override
	public String toString(){
		return title + "\n" + url + "\n" + date.toString() + "\n";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewsArticle other = (NewsArticle) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
