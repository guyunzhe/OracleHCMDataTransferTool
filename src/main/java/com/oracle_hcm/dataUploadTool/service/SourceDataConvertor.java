package com.oracle_hcm.dataUploadTool.service;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.oracle_hcm.dataUploadTool.bo.SourceTable;

public interface SourceDataConvertor {
	
	Map<String, SourceTable> convert(Set<File> sourceFiles);
}
