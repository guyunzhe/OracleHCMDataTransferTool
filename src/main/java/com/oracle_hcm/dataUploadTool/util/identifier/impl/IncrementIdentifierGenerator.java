package com.oracle_hcm.dataUploadTool.util.identifier.impl;

import java.util.HashMap;
import java.util.Map;

import com.oracle_hcm.dataUploadTool.util.identifier.IdentifierGenerator;

public class IncrementIdentifierGenerator implements IdentifierGenerator {
	
	private int start;
	private int step;
	
	private int index;
	
	private String prefix;
	
	private Map<Integer, String> identifiers = new HashMap<Integer, String>();
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public IncrementIdentifierGenerator(int start, int step) {
		this.start = start;
		this.step = step;
		initializeIndex();
	}

	public IncrementIdentifierGenerator() {
		initializeIndex();
	}
	
	public IncrementIdentifierGenerator(int start, int step, String prefix) {
		this.start = start;
		this.step = step;
		this.prefix = prefix;
		initializeIndex();
	}

	public String generateIdentifier(int sourceRowIndex) {
		this.index += this.step;
		String identifier = this.prefix + Integer.toString(this.index);
		
		identifiers.put(sourceRowIndex, identifier);
		return identifier;
	}

	private void initializeIndex() {
		this.index = this.start;
	}
}
