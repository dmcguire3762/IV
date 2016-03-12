package com.iv.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
	FileWriter writer = null;
	String filename = "";
	private String contents = "";
	
	public CSVWriter(String filename){
		this.filename = filename;
	}
	
	public void open() throws IOException{
		writer = new FileWriter(filename);
	}
	
	public void addLine(List<String> params){
		for(int i = 0; i < params.size(); i++){
			contents += params.get(i);
			if(i != params.size() - 1){
				contents += ",";
			}
		}
		
		contents += "\n";		
	}
	
	public void addLine(String line){
		contents += line;
	}
	
	public void write(){
		if(writer == null){
			throw new RuntimeException("open file writer");
		}
		
		try {
			writer.write(contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		close();
	}

	private void close() {
		if(writer != null){
			try {
				writer.flush();
				writer.close();
				writer = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
