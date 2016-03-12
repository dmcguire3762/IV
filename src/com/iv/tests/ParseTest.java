package com.iv.tests;

import org.junit.Test;
import com.iv.parse.ParseManager;

public class ParseTest {

	@Test
	public void test() {
		System.out.println("testing.. ");		
		ParseManager.getInstance().writeCSVForAllTickers();
		System.out.println("done");
	}

}
