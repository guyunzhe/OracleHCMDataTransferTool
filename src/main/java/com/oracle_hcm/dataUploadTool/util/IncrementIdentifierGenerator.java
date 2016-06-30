package com.oracle_hcm.dataUploadTool.util;

import org.springframework.stereotype.Component;

@Component
public class IncrementIdentifierGenerator {

	private int start;
	private int step;
	private int index;
	private String prefix;

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

	public String generateIdentifier() {
		this.index += this.step;
		String identifier = this.prefix + Integer.toString(this.index);

		return identifier;
	}

	public void initializeIndex() {
		this.index = this.start;
	}
}
