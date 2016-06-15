package com.oracle_hcm.dataUploadTool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
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
		
		buildParentComponentData(printStream, parentComponentElement);
	}
	
	private void buildParentComponentData(PrintStream printStream, Element parentComponentElement) {
		Element discriminatorElement = (Element) parentComponentElement.selectSingleNode("child::discriminator");
		
		//1. business object name
		String discriminator = discriminatorElement.getText();
		
		generateComment(printStream, discriminator);
		
		Element sourceTableReferenceElement = (Element) parentComponentElement.selectSingleNode("child::source-table-reference");
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
		
		Element sourceKeyElement = (Element) parentComponentElement.selectSingleNode("child::source-key");
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
		
		Element attributesElement = (Element) parentComponentElement.selectSingleNode("child::attributes");
		
		String metadataLine = "METADATA|" + discriminator + "|SourceSystemOwner|SourceSystemId|";
		
		List attributeElementsList = attributesElement.selectNodes("child::attribute");
		Iterator attributeElementsListIterator = attributeElementsList.iterator();
		while(attributeElementsListIterator.hasNext()) {
			Element attributeElement = (Element) attributeElementsListIterator.next();
			Element attributeNameElement = (Element) attributeElement.selectSingleNode("child::name");
			String attributeName = attributeNameElement.getText();
			
			Element attributeSourceColumnElement = (Element) attributeNameElement.selectSingleNode("child::source-column");
			String attributeSourceColumn = attributeSourceColumnElement.getText();
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
