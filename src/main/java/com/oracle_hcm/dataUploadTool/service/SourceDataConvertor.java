package com.oracle_hcm.dataUploadTool.service;

import java.util.Map;

import com.oracle_hcm.dataUploadTool.bo.SourceTable;

public interface SourceDataConvertor {
	
	Map<String, SourceTable> convertData();
}
