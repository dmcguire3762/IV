package com.iv.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.iv.parse.ParseManager;

public class ParseTest {

	@Test
	public void test() {
		System.out.println("testing.. ");
		//ParseManager parser = ParseManager.getInstance();// reader = new CSVReader("NotAFile");
		
		ParseManager.getInstance().writeCSVForAllTickers();
		System.out.println("done");
	}

}
