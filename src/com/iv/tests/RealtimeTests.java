package com.iv.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.iv.realtime.RealtimeEngine;

public class RealtimeTests {

	@Test
	public void test() {
		RealtimeEngine engine = new RealtimeEngine();
		engine.start();
	}

}
