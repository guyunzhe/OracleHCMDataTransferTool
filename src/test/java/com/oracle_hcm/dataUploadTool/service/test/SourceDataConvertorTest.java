package com.oracle_hcm.dataUploadTool.service.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oracle_hcm.dataUploadTool.bo.SourceElement;
import com.oracle_hcm.dataUploadTool.bo.SourceRow;
import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.service.SourceDataConvertor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service.xml"})
public class SourceDataConvertorTest {

	@Autowired
	private SourceDataConvertor sourceDataConvertor;

	@Test
	public void testConvertData() {
		Map<String, SourceTable> sourceTables = this.sourceDataConvertor.convertData();
		for(Map.Entry<String, SourceTable> entry : sourceTables.entrySet()) {
			String tableName = entry.getKey();
			System.out.println("Table Name:" + tableName);
			SourceTable sourceTable = entry.getValue();
			List<SourceRow> sourceRows = sourceTable.getSourceRows();
			Iterator<SourceRow> sourceRowsIterator = sourceRows.iterator();
			while(sourceRowsIterator.hasNext()) {
				SourceRow sourceRow = sourceRowsIterator.next();
				int rowIndex = sourceRow.getIndex();
				System.out.println("Row Index:" + Integer.toString(rowIndex));
				Map<String, SourceElement> sourceElements = sourceRow.getElements();
				for(String elementName : sourceElements.keySet()) {
					SourceElement sourceElement = sourceElements.get(elementName);
					String columnName = sourceElement.getColumnName();
					int columnIndex = sourceElement.getRowIndex();
					String elementValue = sourceElement.getValue();
					System.out.println("Column Name:" + columnName + " -- " + "Column Index:" + columnIndex + " --> " + elementValue);
				}
			}
		}
	}
}
