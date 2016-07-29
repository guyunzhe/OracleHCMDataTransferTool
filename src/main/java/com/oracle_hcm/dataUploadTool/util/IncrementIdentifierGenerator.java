package com.oracle_hcm.dataUploadTool.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class IncrementIdentifierGenerator {

	private Map<String, List<String>> generatedIdentifiers = new HashMap<String, List<String>>();

	//The start index
	private int start;

	//The discrepancy between keys
	private int step;

	//index = previous index + step
	private int index;

	//component name || other string
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
		initializeIdentifierMap(prefix);
	}

	public IncrementIdentifierGenerator(int start, int step) {
		this.start = start;
		this.step = step;
		initializeIndex();
	}

	public IncrementIdentifierGenerator() { }

	public IncrementIdentifierGenerator(int start, int step, String prefix) {
		this.start = start;
		this.step = step;
		this.prefix = prefix;
		initializeIndex();
		initializeIdentifierMap(prefix);
	}

	/**
	 * Generate the incremented key every time the method is invoked
	 * */
	public String generateIdentifier() {
		if(StringUtils.isEmpty(this.prefix)) {
			throw new RuntimeException("No prefix for generating the source key");
		}

		this.index += this.step;
		String identifier = this.prefix + Integer.toString(this.index);

		return identifier;
	}

	/**
	 * Initialize <code>index</code> to <code>start</code> value
	 * */
	public void initializeIndex() {
		this.index = this.start;
	}

	private void initializeIdentifierMap(String prefix) {
		this.generatedIdentifiers.put(prefix, new ArrayList<String>());
	}
}
