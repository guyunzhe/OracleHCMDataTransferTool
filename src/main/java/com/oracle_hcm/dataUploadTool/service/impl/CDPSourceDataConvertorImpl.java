package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.exceptions.FileOperationException;
import com.oracle_hcm.dataUploadTool.service.ExcelDataReader;
import com.oracle_hcm.dataUploadTool.service.SourceDataConvertor;
import com.oracle_hcm.dataUploadTool.service.SourceDataReader;

@Component
public class CDPSourceDataConvertorImpl implements SourceDataConvertor {

	private SourceDataReader sourceDataReader;
	private ExcelDataReader excelDataReader;

	@Autowired
	public CDPSourceDataConvertorImpl(SourceDataReader sourceDataReader, ExcelDataReader excelDataReader) {
		this.sourceDataReader = sourceDataReader;
		this.excelDataReader = excelDataReader;
	}

	public Map<String, SourceTable> convertData() {
		Map<String, SourceTable> sourceTableMap = new HashMap<String, SourceTable>();

		Map<String, File> sourceFiles = this.sourceDataReader.readData();
		for(String fileName : sourceFiles.keySet()) {
			File sourceFile = sourceFiles.get(fileName);
			String ext = FilenameUtils.getExtension(sourceFile.getAbsolutePath());
			if(StringUtils.isEmpty(ext)) {
				throw new FileOperationException("C00000", "Missing extention of the source file");
			}

			Map<String, SourceTable> sourceTables = null;
			if(ext.equals("xls")) {
				sourceTables = this.excelDataReader.readXLS(sourceFile);
			}else if(ext.equals("xlsx")) {
				sourceTables = this.excelDataReader.readXLSX(sourceFile);
			}else{
				throw new FileOperationException("C00001", "Unsupported file extention");
			}
			sourceTableMap.putAll(sourceTables);
		}
		return sourceTableMap;
	}
}
