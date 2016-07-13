package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.oracle_hcm.dataUploadTool.bo.SourceElement;
import com.oracle_hcm.dataUploadTool.bo.SourceRow;
import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.exceptions.WorkbookOperationException;
import com.oracle_hcm.dataUploadTool.service.ExcelDataReader;

@Component
public class ExcelDataReaderImpl implements ExcelDataReader {

	private final Logger logger = Logger.getLogger(ExcelDataReaderImpl.class);

	public Map<String, SourceTable> readXLS(File sourceFile) {
		Map<String, SourceTable> sourceTables = new HashMap<String, SourceTable>();
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

	public Map<String, SourceTable> readXLSX(File sourceFile) {
		Map<String, SourceTable> sourceTables = new HashMap<String, SourceTable>();
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

	private void transferData(Workbook workbook, String sourceFileName, 
			Map<String, SourceTable> sourceTables) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for(int sheetIdx = 0;sheetIdx < numberOfSheets;sheetIdx++) {
			SourceTable sourceTable = new SourceTable();
			Sheet spreadsheet = workbook.getSheetAt(sheetIdx);
			if(sheetIdx == 0) {
				sourceTable.setName(sourceFileName);
			}else{
				String sheetName = spreadsheet.getSheetName();
				Pattern sheetNamePattern = Pattern.compile("^Sheet\\d+$");
				Matcher sheetNameMatcher = sheetNamePattern.matcher(sheetName);
				if(sheetNameMatcher.matches()) {
					//Skip sheets whose name like Sheet1, Sheet2, ...
					continue;
				}else{
					sourceTable.setName(spreadsheet.getSheetName());
				}
			}

			int rowStart = Math.min(spreadsheet.getFirstRowNum(), 10);
			int rowEnd = Math.max(spreadsheet.getLastRowNum(), 1);
			Row columnDefinitionRow = spreadsheet.getRow(rowStart);
			if(columnDefinitionRow == null) {
				throw new WorkbookOperationException("W00000", 
						"Cannot find the column definition row in "
						+ "the spread sheet " + spreadsheet.getSheetName());
			}
			if(rowStart == rowEnd) {
				throw new WorkbookOperationException("W11111", 
						"There is no data in the spread sheet " 
								+ spreadsheet.getSheetName());
			}

			readDataFromTable(sourceTable, spreadsheet, 
					columnDefinitionRow, rowStart, rowEnd);
			sourceTables.put(sourceTable.getName(), sourceTable);
		}

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readDataFromTable(SourceTable sourceTable, 
			Sheet spreadsheet, Row columnDefinitionRow, 
			int rowStart, int rowEnd) {
		for(int rowNum = rowStart + 1;rowNum <= rowEnd;rowNum++) {
			Row row = spreadsheet.getRow(rowNum);
			if(row == null) {
				//Move on to the next data row
				continue;
			}
			SourceRow sourceRow = new SourceRow();
			sourceRow.setIndex(rowNum);

			readDataFromRow(columnDefinitionRow, row, sourceRow);
			sourceTable.addRow(sourceRow);
		}
	}

	private void readDataFromRow(Row columnDefinitionRow, 
			Row row, SourceRow sourceRow) {
		for(int columnIndex = columnDefinitionRow.getFirstCellNum();
				columnIndex < columnDefinitionRow.getLastCellNum();columnIndex++) {
			Cell columnDefinition = columnDefinitionRow.getCell(columnIndex, 
					Row.RETURN_BLANK_AS_NULL);
			if(columnDefinition == null) {
				throw new WorkbookOperationException("W00001", 
						"Empty cell in the column definition row");
			}
			Cell cell = row.getCell(columnIndex, Row.CREATE_NULL_AS_BLANK);
			SourceElement sourceElement = new SourceElement(row.getRowNum(),
					columnDefinition.getStringCellValue());

			readDataFromCell(cell, sourceElement);
			sourceRow.addElement(sourceElement);
		}
	}

	private void readDataFromCell(Cell cell, SourceElement sourceElement) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			if(HSSFDateUtil.isCellDateFormatted(cell)) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String formattedDate = simpleDateFormat.format(cell.getDateCellValue());
				logger.info(String.format("Cell Index:%d Cell value:%s", 
						cell.getColumnIndex(), formattedDate));
				sourceElement.setValue(formattedDate);
			}else{
				DataFormatter dataFormatter = new DataFormatter();
				String cellValue = dataFormatter.formatCellValue(cell);
				logger.info(String.format("Cell Index:%d Cell value:%s", 
						cell.getColumnIndex(), cellValue));
				sourceElement.setValue(cellValue);
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			logger.info(String.format("Cell Index:%d Cell value:%b", 
					cell.getColumnIndex(), cell.getBooleanCellValue()));
			sourceElement.setValue(String.valueOf(cell.getBooleanCellValue()));
			break;
		case Cell.CELL_TYPE_STRING:
			logger.info(String.format("Cell Index:%d Cell value:%s", 
					cell.getColumnIndex(), cell.getStringCellValue()));
			sourceElement.setValue(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			logger.info(String.format("Cell Index:%d Cell value:%s", 
					cell.getColumnIndex(), cell.getCellFormula()));
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
	}
}