package com.oracle_hcm.dataUploadTool.bo;

public class SourceElement {
	
	private int rowIndex;
	private String columnName;
	private String value;
	
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public SourceElement(int rowIndex, String columnName, String value) {
		this.rowIndex = rowIndex;
		this.columnName = columnName;
		this.value = value;
	}
	
	public SourceElement(int rowIndex, String columnName) {
		this.rowIndex = rowIndex;
		this.columnName = columnName;
	}
	public SourceElement() {}
}
