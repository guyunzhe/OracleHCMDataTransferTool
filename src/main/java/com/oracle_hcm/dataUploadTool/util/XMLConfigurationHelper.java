package com.oracle_hcm.dataUploadTool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import com.oracle_hcm.dataUploadTool.bo.SourceElement;
import com.oracle_hcm.dataUploadTool.bo.SourceRow;
import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.configure.Directory;
import com.oracle_hcm.dataUploadTool.service.SourceDataConvertor;
import com.oracle_hcm.dataUploadTool.service.SourceDataReader;
import com.oracle_hcm.dataUploadTool.util.identifier.IdentifierGenerator;
import com.oracle_hcm.dataUploadTool.util.identifier.impl.IncrementIdentifierGenerator;


public class XMLConfigurationHelper {
	
	private SourceDataReader sourceDataReader;
	private SourceDataConvertor sourceDataConvertor;
	
	private Map<String, SourceTable> sourceTables;
	public Map<String, SourceTable> getSourceTables() {
		return sourceTables;
	}
	//TODO decide whether or not the set method is needed
	public void setSourceTables(Map<String, SourceTable> sourceTables) {
		this.sourceTables = sourceTables;
	}
	
	/**
	 * Loads a document from a file.
	 *
	 * @param configureFile the data source
	 * @throw a org.dom4j.DocumentExcepiton occurs on parsing failure.
	 */
	public Document parseWithSAX(File configureFile) throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		return xmlReader.read(configureFile);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Element> iteratorChildElements(Element parent) {
		List<Element> elements = new ArrayList<Element>();
		
		Iterator childElementIterator = parent.elementIterator();
		while(childElementIterator.hasNext()){
			Element elmeent = (Element)childElementIterator.next();
			elements.add(elmeent);
		}
		
		return elements;
	}
	
	public void browseBusinessObjects(File configureFile) {
		SAXReader xmlReader = new SAXReader();
		Document document = null;
		try {
			document = xmlReader.read(configureFile);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		Element root = document.getRootElement();
		System.out.println(root.getName());
		
		//TODO generate a root directory for all the dat files
		//Directory.LOADED for now
		File dataDirectory = new File(Directory.LOADED);
		//---------------------------------------------------------
		if(dataDirectory.exists()) {
			//TODO
		}else{
			FileOperationUtils.createDirectory(dataDirectory.getPath());
		}
		
		//TODO the directory where all the source files are exported
		File sourceDirectory = new File(Directory.EXPORTED);
		this.sourceTables = sourceDataConvertor.convert(sourceDataReader.read(sourceDirectory.getPath()));
		
		@SuppressWarnings("rawtypes")
		List objectElements = root.selectNodes("child::object");
		
		for(@SuppressWarnings("rawtypes")
		Iterator objectElementsIterator = objectElements.iterator();objectElementsIterator.hasNext();) {
			Element objectElement = (Element) objectElementsIterator.next();
			
			browseBusinessObject(dataDirectory, objectElement);
		}
	}
	

	public void browseBusinessObject(File dataDirectory, Element businessObject) {
		Element businessObjectNameElement = (Element) businessObject.selectSingleNode("child::name");
		String businessObjectName = businessObjectNameElement.getText();
		
		//create the dat file for the business object
		File businessObjectFile = new File(dataDirectory.getPath() + File.separator + businessObjectName + ".dat");
		if(businessObjectFile.exists()) {
			//TODO if the dat file for the business object already exists, keep where it is
		}else{
			try {
				businessObjectFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//---------------------------------------------------------
		
		//TODO 
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(businessObjectFile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		PrintStream printStream = new PrintStream(outputStream);
		//---------------------------------------------------------
		
		Element parentComponentElement = (Element) businessObject.selectSingleNode("child::parent-component");
		
		buildComponentData(printStream, parentComponentElement, true);
		
		Element childComponentsElement = (Element) businessObject.selectSingleNode("child::child-components");
		List childComponents = childComponentsElement.selectNodes("child::component");
		Iterator childComponentsIterator = childComponents.iterator();
		while(childComponentsIterator.hasNext()) {
			Element childComponent = (Element) childComponentsIterator.next();
		}
	}
	
	private void buildComponentData(PrintStream printStream, Element componentElement, Boolean isTopLevel) {
		Element discriminatorElement = (Element) componentElement.selectSingleNode("child::discriminator");
		
		//1. business object name
		String discriminator = discriminatorElement.getText();
		
		generateComment(printStream, discriminator);
		
		Element sourceTableReferenceElement = (Element) componentElement.selectSingleNode("child::source-table-reference");
		if(sourceTableReferenceElement == null) {
			//TODO 
		}
		String sourceTableName = sourceTableReferenceElement.getText();
		
		//default source table for the component
		SourceTable sourceTable = iterateSourceTable(sourceTableName);
		if(sourceTable == null) {
			//TODO 
		}
		List<SourceRow> sourceRows = sourceTable.getSourceRows();
		
		Element sourceKeyElement = (Element) componentElement.selectSingleNode("child::source-key");
		Element sourceSystemOwnerElement = (Element) sourceKeyElement.selectSingleNode("child::SourceSystemOwner");
		
		//2. sourceSystemOwner
		String sourceSystemOwner = sourceSystemOwnerElement.getText();
		
		//3. source key generator
		IdentifierGenerator identifierGenerator = null;
		
		Element sourceSystemIdElement = (Element) sourceKeyElement.selectSingleNode("child::SourceSystemId");
		Attribute generationStrategyAttribute = sourceSystemIdElement.attribute("generation-strategy");
		if(generationStrategyAttribute == null || generationStrategyAttribute.getValue().equals("increment")) {
			Element prefixElement = (Element) sourceSystemIdElement.selectSingleNode("child::prefix");
			String prefix = prefixElement.getText();
			
			Element indexGeneratorElement = (Element) sourceSystemIdElement.selectSingleNode("child::index-generator");
			Element startElement = (Element) indexGeneratorElement.selectSingleNode("child::start");
			Element stepElement = (Element) indexGeneratorElement.selectSingleNode("child::step");
			
			String start = startElement.getText();
			String step = stepElement.getText();
			
			identifierGenerator = new IncrementIdentifierGenerator(Integer.parseInt(start), Integer.parseInt(step), prefix);
		}
		
		Element attributesElement = (Element) componentElement.selectSingleNode("child::attributes");
		
		String metadataLine = "METADATA|" + discriminator + "|SourceSystemOwner|SourceSystemId|";
		Map<Integer, String> mergeLines = new HashMap<Integer, String>();
		
		Iterator<SourceRow> sourceRowsIterator = sourceRows.iterator();
		while(sourceRowsIterator.hasNext()) {
			SourceRow sourceRow = sourceRowsIterator.next();
			int sourceRowIndex = sourceRow.getIndex();
			String sourceSystemId = identifierGenerator.generateIdentifier(sourceRowIndex);
			
			String mergeLine = "MERGE|" + discriminator + "|" + sourceSystemOwner + "|" + sourceSystemId + "|";
			mergeLines.put(sourceRowIndex, mergeLine);
		}
		
		if(isTopLevel == false) {
			Element parentReferenceElement = (Element) componentElement.selectSingleNode("child::parent-reference");
			Element attributeNameElement = (Element) parentReferenceElement.selectSingleNode("child::attribute-name");
			Element sourceColumnElement = (Element) parentReferenceElement.selectSingleNode("child::source-column");
			
			metadataLine += attributeNameElement;
			
			for(int index : mergeLines.keySet()) {
				
			}
		}
		
		List attributeElementsList = attributesElement.selectNodes("child::attribute");
		Iterator attributeElementsListIterator = attributeElementsList.iterator();
		while(attributeElementsListIterator.hasNext()) {
			Element attributeElement = (Element) attributeElementsListIterator.next();
			Element attributeNameElement = (Element) attributeElement.selectSingleNode("child::name");
			String attributeName = attributeNameElement.getText();
			
			Element attributeSourceColumnElement = (Element) attributeNameElement.selectSingleNode("child::source-column");
			String attributeSourceColumn = attributeSourceColumnElement.getText();
			
			while(sourceRowsIterator.hasNext()) {
				SourceRow sourceRow = sourceRowsIterator.next();
				int sourceRowIndex = sourceRow.getIndex();
				Map<String, SourceElement> sourceElements = sourceRow.getElements();
				
				SourceElement referencedSourceElement = sourceElements.get(attributeSourceColumn);
				
				
				
				for(int index : mergeLines.keySet()) {
					if(index == sourceRowIndex) {
						String mergeLine = mergeLines.get(index);
						mergeLine += referencedSourceElement.getValue();
						mergeLine += "|";
						
						mergeLines.put(index, mergeLine);
					}
				}
			}
			
			metadataLine += attributeName;
			metadataLine += "|";
		}
		
		for(Map.Entry<Integer, String> mergeLineEntry : mergeLines.entrySet()) {
			int index = mergeLineEntry.getKey();
			String mergeLine = mergeLineEntry.getValue();
			mergeLine.substring(0, mergeLine.lastIndexOf("|") - 1);
			
			mergeLines.put(index, mergeLine);
		}
		metadataLine.substring(0, metadataLine.lastIndexOf("|") - 1);
		
		printStream.println(metadataLine);
		for(Map.Entry<Integer, String> mergeLineEntry : mergeLines.entrySet()) {
			String mergeLine = mergeLineEntry.getValue();
			printStream.println(mergeLine);
		}
	}
	
	private SourceTable iterateSourceTable(String sourceTableName) {
		for(String key : this.sourceTables.keySet()) {
			if(StringUtils.equals(key, sourceTableName)) {
				return this.sourceTables.get(key);
			}
		}
		return null;
	}
	
	private void generateComment(PrintStream printStream, String discriminator) {
		printStream.println("COMMENT ##############################################################################");
		printStream.println("COMMENT Business Entity :" + discriminator);
		printStream.println("COMMENT ##############################################################################");
	}
}
