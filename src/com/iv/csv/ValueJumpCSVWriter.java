package com.iv.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.iv.data.ValueJump;

public class ValueJumpCSVWriter extends CSVWriter{
	public ValueJumpCSVWriter(String filename) {
		super(filename);
	}
	
	public void writeValueJumpList(Collection<ValueJump> valueJumps){
		if(valueJumps.size() <= 0){
			return;
		}

		System.out.println("Writing " + this.filename);
		System.out.println("# of value jumps: " + valueJumps.size());
		try {
			this.open();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		for(ValueJump valueJump: valueJumps){
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(valueJump.getStartDate().toString());
			fields.add(valueJump.getEndDate().toString());
			fields.add(""+valueJump.getPercentJump());
			this.addLine(fields);
		}
		
		this.write();
	}
	
}
