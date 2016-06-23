package com.oracle_hcm.dataUploadTool.service.impl;

import java.util.Map;

import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.directory.DirectoryService;
import com.oracle_hcm.dataUploadTool.service.DataReader;

public class ExcelDataReader implements DataReader {
	
	private DirectoryService directoryService;
	
	public ExcelDataReader(DirectoryService directoryService) {
		this.directoryService = directoryService;
	}

	public Map<String, SourceTable> readData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
