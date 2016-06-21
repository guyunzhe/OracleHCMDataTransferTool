package com.oracle_hcm.dataUploadTool.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.dom4j.Element;
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
		@SuppressWarnings("rawtypes")
		List childComponents = childComponentsElement.selectNodes("child::component");
		@SuppressWarnings("rawtypes")
		Iterator childComponentsIterator = childComponents.iterator();
		while(childComponentsIterator.hasNext()) {
			Element childComponent = (Element) childComponentsIterator.next();
			buildComponentData(printStream, childComponent);
		}
	}
	
	private void buildComponentData(PrintStream printStream, Element componentElement) {
		buildComponentData(printStream, componentElement, false);
	}
	
	private void buildComponentData(PrintStream printStream, Element componentElement, Boolean isTopLevel) {
		Element discriminatorElement = (Element) componentElement.selectSingleNode("child::discriminator");
		String discriminator = discriminatorElement.getText();
		
		Element sourceTableReferenceElement = null;
		Element parentDiscriminatorElement = (Element) componentElement.selectSingleNode("child::parent-discriminator");
		if(parentDiscriminatorElement == null) {
			/*
			 * Without parent discriminator, parent component
			 * the source table reference attribute is required
			 * */
			sourceTableReferenceElement = (Element) componentElement.selectSingleNode("child::source-table-reference");
			if(sourceTableReferenceElement == null) {
				//if the parent component don't have a source table reference
				//TODO throw an exception
			}
		}else{
			/*
			 * With parent discriminator, child component
			 * the source table reference attribute is optional
			 * */
			sourceTableReferenceElement = (Element) componentElement.selectSingleNode("child::source-table-reference");
			
			String parentDiscriminator = parentDiscriminatorElement.getText();
			
			/*
			 * Without source table reference
			 * */
			if(sourceTableReferenceElement == null) {
				//use its parents' source table reference
				@SuppressWarnings("rawtypes")
				List parentComponentsElement = componentElement.getParent().getParent().selectNodes("child::*");
				@SuppressWarnings("rawtypes")
				Iterator parentComponentsElementIterator = parentComponentsElement.iterator();
				
				Element parentComponentElement = null;
				while(parentComponentsElementIterator.hasNext()) {
					parentComponentElement = (Element) parentComponentsElementIterator.next();
					Element parentComponentDiscriminatorElement = (Element) parentComponentElement.selectSingleNode("child::discriminator");
					String parentComponentDiscriminator = parentComponentDiscriminatorElement.getText();
					if(StringUtils.equals(parentDiscriminator, parentComponentDiscriminator)) {
						sourceTableReferenceElement = (Element) parentComponentElement.selectSingleNode("child::source-table-reference");
					}
				}
			}
		}
		
		printStream.println("COMMENT ##############################################################################");
		printStream.println("COMMENT Business Entity :" + discriminator);
		printStream.println("COMMENT ##############################################################################");

		String sourceTableName = sourceTableReferenceElement.getText();
		//default source table for the component
		SourceTable sourceTable = iterateSourceTable(sourceTableName);
		if(sourceTable == null) {
			//TODO 
		}
		List<SourceRow> sourceRows = sourceTable.getSourceRows();
		Iterator<SourceRow> sourceRowsIterator = sourceRows.iterator();
		
		Element sourceKeyElement = (Element) componentElement.selectSingleNode("child::source-key");
		
		//Metadata line
		String metadataLine = "METADATA|" + discriminator + "|SourceSystemOwner|SourceSystemId|";
		//Merge lines
		Map<Integer, String> mergeLines = new HashMap<Integer, String>();
		
		generateSourceKey(discriminator, sourceKeyElement, mergeLines, sourceRows);
		
		if(isTopLevel == false) {
//			<parent-reference>
//				<attribute-name></attribute-name>
//				<source-column></source-column>
//			</parent-reference>
			Element parentReferenceElement = (Element) componentElement.selectSingleNode("child::parent-reference");
			Element attributeNameElement = (Element) parentReferenceElement.selectSingleNode("child::attribute-name");
			//parent reference attribute name
			String attributeName = attributeNameElement.getText();
			Element sourceColumnElement = (Element) parentReferenceElement.selectSingleNode("child::source-column");
			//referenced source column
			String sourceColumn = sourceColumnElement.getText();
			
			metadataLine += attributeName;
			
			while(sourceRowsIterator.hasNext()) {
				SourceRow sourceRow = sourceRowsIterator.next();
				int sourceRowIndex = sourceRow.getIndex();
				Map<String, SourceElement> sourceElements = sourceRow.getElements();
				
				SourceElement referencedSourceElement = sourceElements.get(sourceColumn);
				
				for(int index : mergeLines.keySet()) {
					if(index == sourceRowIndex) {
//						String mergeLine = mergeLines.get(index);
//						mergeLine += referencedSourceElement.getValue();
//						mergeLine += "|";
						
						mergeLines.put(index, mergeLines.get(index) + referencedSourceElement.getValue() + "|");
					}
				}
			}
		}
		
		//attributes collection
		Element attributesElement = (Element) componentElement.selectSingleNode("child::attributes");
		@SuppressWarnings("rawtypes")
		List attributeElementsList = attributesElement.selectNodes("child::attribute");
		@SuppressWarnings("rawtypes")
		Iterator attributeElementsListIterator = attributeElementsList.iterator();
		/*
		 * Iterate every attribute of the component
		 * */
		while(attributeElementsListIterator.hasNext()) {
//			<attribute>
//				<name></name>
//				<source-column></source-column>
//				<source-table-reference></source-table-reference> <!-- optional -->
//			</attribute>
			Element attributeElement = (Element) attributeElementsListIterator.next();
			Element attributeNameElement = (Element) attributeElement.selectSingleNode("child::name");
			//name
			String attributeName = attributeNameElement.getText();
			
			Element attributeSourceColumnElement = (Element) attributeElement.selectSingleNode("child::source-column");
			//source column
			String attributeSourceColumn = attributeSourceColumnElement.getText();
			
			/*
			 * Get all the referenced values for the given attribute by iterating 
			 * all the source table rows within the specific <code>attributeSourceColumn</code>
			 * */
			while(sourceRowsIterator.hasNext()) {
				SourceRow sourceRow = sourceRowsIterator.next();
				int sourceRowIndex = sourceRow.getIndex();
				Map<String, SourceElement> sourceElements = sourceRow.getElements();
				
				//source element got by the <code>attributeSourceColumn</code> for each row
				SourceElement referencedSourceElement = sourceElements.get(attributeSourceColumn);
				
				for(int index : mergeLines.keySet()) {
					if(index == sourceRowIndex) {
//						String mergeLine = mergeLines.get(index);
//						mergeLine += referencedSourceElement.getValue();
//						mergeLine += "|";
						
						mergeLines.put(index, mergeLines.get(index) + referencedSourceElement.getValue() + "|");
					}
				}
			}
			
			metadataLine += attributeName;
			metadataLine += "|";
		}
		
		for(Map.Entry<Integer, String> mergeLineEntry : mergeLines.entrySet()) {
			int index = mergeLineEntry.getKey();
			String mergeLine = mergeLineEntry.getValue();
			
			mergeLines.put(index, mergeLine.substring(0, mergeLine.lastIndexOf("|") - 1));
		}
		metadataLine.substring(0, metadataLine.lastIndexOf("|") - 1);
		
		printStream.println(metadataLine);
		for(Map.Entry<Integer, String> mergeLineEntry : mergeLines.entrySet()) {
			String mergeLine = mergeLineEntry.getValue();
			printStream.println(mergeLine);
		}
	}
	
	private void generateSourceKey(String discriminator, Element sourceKeyElement, 
			Map<Integer, String> mergeLines, List<SourceRow> sourceRows) {
		Element sourceSystemOwnerElement = (Element) sourceKeyElement.selectSingleNode("child::SourceSystemOwner");
		String sourceSystemOwner = sourceSystemOwnerElement.getText();
		
		Element sourceSystemIdElement = (Element) sourceKeyElement.selectSingleNode("child::SourceSystemId");
		Attribute generationStrategyAttribute = sourceSystemIdElement.attribute("generation-strategy");
		if(generationStrategyAttribute == null || generationStrategyAttribute.getValue().equals("reference")) {
//			<source-key>
//				<SourceSystemOwner>STUDENT1</SourceSystemOwner>
//				<SourceSystemId generation-strategy="increment">
//					<prefix>STUDENT1_PER</prefix>
//					<index-generator>
//						<start>100</start>
//						<step>1</step>
//					</index-generator>
//				</SourceSystemId>
//			</source-key>
			Element sourceColumnElement = (Element) sourceSystemIdElement.selectSingleNode("source-column");
			String sourceColumn = sourceColumnElement.getText();
			
			Iterator<SourceRow> sourceRowsIterator = sourceRows.iterator();
			while(sourceRowsIterator.hasNext()) {
				SourceRow sourceRow = sourceRowsIterator.next();
				int sourceRowIndex = sourceRow.getIndex();
				Map<String, SourceElement> sourceElements = sourceRow.getElements();
				
				SourceElement referencedSourceElement = sourceElements.get(sourceColumn);
				String mergeLine = "MERGE|" + discriminator + "|" + sourceSystemOwner + "|" + referencedSourceElement.getValue() + "|";
				mergeLines.put(sourceRowIndex, mergeLine);
			}
		}else if(generationStrategyAttribute.getValue().equals("increment")) {
//			<source-key>
//				<SourceSystemOwner>STUDENT1</SourceSystemOwner>
//				<SourceSystemId generation-strategy="reference">
//					<source-column></source-column>
//				</SourceSystemId>
//			</source-key>
			Element prefixElement = (Element) sourceSystemIdElement.selectSingleNode("child::prefix");
			String prefix = prefixElement.getText();
			
			Element indexGeneratorElement = (Element) sourceSystemIdElement.selectSingleNode("child::index-generator");
			Element startElement = (Element) indexGeneratorElement.selectSingleNode("child::start");
			Element stepElement = (Element) indexGeneratorElement.selectSingleNode("child::step");
			
			String start = startElement.getText();
			String step = stepElement.getText();
			
			IdentifierGenerator identifierGenerator = new IncrementIdentifierGenerator(Integer.parseInt(start), Integer.parseInt(step), prefix);
			
			Iterator<SourceRow> sourceRowsIterator = sourceRows.iterator();
			while(sourceRowsIterator.hasNext()) {
				SourceRow sourceRow = sourceRowsIterator.next();
				int sourceRowIndex = sourceRow.getIndex();
				String sourceSystemId = identifierGenerator.generateIdentifier(sourceRowIndex);
				
				String mergeLine = "MERGE|" + discriminator + "|" + sourceSystemOwner + "|" + sourceSystemId + "|";
				mergeLines.put(sourceRowIndex, mergeLine);
			}
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
}
