package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.oracle_hcm.dataUploadTool.bo.SourceElement;
import com.oracle_hcm.dataUploadTool.bo.SourceRow;
import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.service.ExcelDataReader;

public class ExcelDataReaderImpl implements ExcelDataReader {
	
	private final Logger logger = Logger.getLogger(ExcelDataReaderImpl.class);
	
	public Set<SourceTable> readXLS(File sourceFile) {
		Set<SourceTable> sourceTables = new HashSet<SourceTable>();
		
		String sourceFileName = FilenameUtils.removeExtension(sourceFile.getName());
		
		InputStream sourceInputStream = null;
		try {
			sourceInputStream = new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Workbook workbook = null;
		try {
			workbook = new HSSFWorkbook(sourceInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		transferData(workbook, sourceFileName, sourceTables);
		
		return sourceTables;
	}

	public Set<SourceTable> readXLSX(File sourceFile) {
		Set<SourceTable> sourceTables = new HashSet<SourceTable>();
		
		String sourceFileName = FilenameUtils.removeExtension(sourceFile.getName());
		
		InputStream sourceInputStream = null;
		try {
			sourceInputStream = new FileInputStream(sourceFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(sourceInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		transferData(workbook, sourceFileName, sourceTables);
		
		return sourceTables;
	}
	
	
	private void transferData(Workbook workbook, String sourceFileName, Set<SourceTable> sourceTables) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for(int sheetIdx = 0;sheetIdx < numberOfSheets;sheetIdx++) {
			SourceTable sourceTable = new SourceTable();
			Sheet spreadsheet = workbook.getSheetAt(sheetIdx);
			if(sheetIdx == 0) {
				sourceTable.setName(sourceFileName);
			}else{
				sourceTable.setName(spreadsheet.getSheetName());
			}
			
			int rowStart = Math.min(spreadsheet.getFirstRowNum(), 10);
			int rowEnd = spreadsheet.getLastRowNum();
			
			Row columnDefinitionRow = spreadsheet.getRow(rowStart);
			if(columnDefinitionRow == null) {
				//The column definition row is empty
				logger.error(String.format("Missing column definition row of the file(%s)", sourceFileName));
				//TODO throw new RuntimeException
			}
			int firstColumnIndex = columnDefinitionRow.getFirstCellNum();
			int lastColumnIndex = columnDefinitionRow.getLastCellNum();
			
			for(int rowNum = rowStart + 1;rowNum <= rowEnd;rowNum++) {
				Row row = spreadsheet.getRow(rowNum);
				if(row == null) {
					logger.warn(String.format("Empty row: %d", rowNum));
					//TODO throw new RuntimeException
				}
				
				SourceRow sourceRow = new SourceRow();
				sourceRow.setIndex(rowNum);
				
				for(int columnIndex = firstColumnIndex;columnIndex < lastColumnIndex;columnIndex++) {
					Cell columnDefinition = columnDefinitionRow.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);
					if(columnDefinition == null) {
						logger.error(String.format("Missing value found in the cell of the column definition row: %d", 
								columnIndex));
						//TODO throw new RuntimeException
					}
					Cell cell = row.getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
					
					SourceElement sourceElement = new SourceElement(rowNum, columnDefinition.getStringCellValue());
					
					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_NUMERIC:
						if(HSSFDateUtil.isCellDateFormatted(cell)) {
							logger.info(String.format("Cell Index:%d Cell value:%T", 
									columnIndex, cell.getDateCellValue()));
							sourceElement.setValue(cell.getDateCellValue().toString());
						}else{
							logger.info(String.format("Cell Index:%d Cell value:%f", 
									columnIndex, cell.getNumericCellValue()));
							sourceElement.setValue(Double.toString(cell.getNumericCellValue()));
						}
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						logger.info(String.format("Cell Index:%d Cell value:%b", 
								columnIndex, cell.getBooleanCellValue()));
						sourceElement.setValue(String.valueOf(cell.getBooleanCellValue()));
						break;
					case Cell.CELL_TYPE_STRING:
						logger.info(String.format("Cell Index:%d Cell value:%s", 
								columnIndex, cell.getStringCellValue()));
						sourceElement.setValue(cell.getStringCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						logger.info(String.format("Cell Index:%d Cell value:%s", 
								columnIndex, cell.getCellFormula()));
						sourceElement.setValue(cell.getCellFormula());
						break;
					case Cell.CELL_TYPE_BLANK:
						logger.info("Cell Index:%d Cell value:Null");
						sourceElement.setValue(null);
						break;
					default:
						logger.info("Cell Index:%d Cell value:Null");
						sourceElement.setValue(null);
					}
					
					sourceRow.addElement(sourceElement);
				}
				
				sourceTable.addRow(sourceRow);
			}
			
			sourceTables.add(sourceTable);
		}
		
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
