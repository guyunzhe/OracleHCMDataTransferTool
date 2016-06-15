package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.service.ExcelDataReader;
import com.oracle_hcm.dataUploadTool.service.SourceDataConvertor;

public class CDPSourceDataConvertorImpl implements SourceDataConvertor {
	
	private final Logger logger = Logger.getLogger(CDPSourceDataConvertorImpl.class);
	
	public Map<String, SourceTable> convert(Set<File> sourceFiles) {
		Map<String, SourceTable> sourceTableMap = new HashMap<String, SourceTable>();

		Iterator<File> sourceFilesIterator = sourceFiles.iterator();
		while(sourceFilesIterator.hasNext()) {
			File sourceFile = sourceFilesIterator.next();
			String ext = FilenameUtils.getExtension(sourceFile.getAbsolutePath());
			if(StringUtils.isEmpty(ext)) {
				logger.error(String.format("The file(%s) dose not have a suffix", sourceFile.getPath()));
				//TODO throw new RuntimeException
			}
			
			Set<SourceTable> sourceTables = null;
			ExcelDataReader excelDataReader = new ExcelDataReaderImpl();
			if(ext.equals("xls")) {
				sourceTables = excelDataReader.readXLS(sourceFile);
			}else if(ext.equals("xlsx")) {
				sourceTables = excelDataReader.readXLSX(sourceFile);
			}
			
			Iterator<SourceTable> sourceTableIterator = sourceTables.iterator();
			while(sourceTableIterator.hasNext()) {
				SourceTable sourceTable = sourceTableIterator.next();
				sourceTableMap.put(sourceTable.getName(), sourceTable);
			}
		}
		return sourceTableMap;
	}

}
