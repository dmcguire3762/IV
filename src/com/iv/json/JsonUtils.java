package com.iv.json;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {
	
	public static JsonObject getJsonObjectFromFile(String filePath){
		try {
			String strFile = readFile(filePath, Charset.defaultCharset());
			return new JsonParser().parse(strFile).getAsJsonObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
	public static JsonObject getJsonObjectFromString(String object){
		return new JsonParser().parse(object).getAsJsonObject();
	}
}
