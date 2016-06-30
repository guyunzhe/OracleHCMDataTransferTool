package com.oracle_hcm.dataUploadTool.service;

import java.io.File;
import java.util.Map;

import com.oracle_hcm.dataUploadTool.bo.SourceTable;

public interface ExcelDataReader {

	/**
	 * Read data from excel files with the extension - xls
	 * 
	 * @param sourceFile  the source file from which the data are read
	 * @return the source data model
	 * */
	Map<String, SourceTable> readXLS(File sourceFile);

	/**
	 * Read data from excel files with the extension - xlsx
	 * 
	 * @param sourceFile  the source file from which the data are read
	 * @return the source data model
	 * */
	Map<String, SourceTable> readXLSX(File sourceFile);
}
