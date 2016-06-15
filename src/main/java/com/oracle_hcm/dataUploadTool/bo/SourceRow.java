package com.oracle_hcm.dataUploadTool.bo;

import java.util.HashMap;
import java.util.Map;

public class SourceRow {
	
	private int index;
	private Map<String, SourceElement> elements = new HashMap<String, SourceElement>();
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void addElement(SourceElement sourceElement) {
		elements.put(sourceElement.getColumnName(), sourceElement);
	}
}
