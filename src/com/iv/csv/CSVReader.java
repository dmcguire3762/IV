package com.iv.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CSVReader {
	private final File csvFile;
	Scanner csvScanner = null;
	
	public CSVReader(String filename){
		csvFile = new File(filename);
	}
	
	public CSVReader(File file){
		csvFile = file;
	}
	
	public String getFilename(){
		return csvFile.getName();
	}
	
	public void open() throws FileNotFoundException{
		csvScanner = new Scanner(csvFile);
		csvScanner.useDelimiter("\n");
	}
	
	public void close(){
		if(csvScanner != null){
			csvScanner.close();
		}
	}
	
	public String getNextLine(){
		if(csvScanner != null && csvScanner.hasNext()){
			return csvScanner.next();
		} else {
			return null;
		}
	}
}
