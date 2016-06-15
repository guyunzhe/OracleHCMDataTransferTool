package com.oracle_hcm.dataUploadTool.bo;

import java.util.ArrayList;
import java.util.List;


public class SourceTable {
	private String name;
	private List<SourceRow> rows = new ArrayList<SourceRow>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void addRow(SourceRow sourceRow) {
		rows.add(sourceRow);
	}
	
	public List<SourceRow> getSourceRows() {
		return this.rows;
	}
}
