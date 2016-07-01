package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle_hcm.dataUploadTool.bo.SourceElement;
import com.oracle_hcm.dataUploadTool.bo.SourceRow;
import com.oracle_hcm.dataUploadTool.bo.SourceTable;
import com.oracle_hcm.dataUploadTool.exceptions.DataMappingOperationException;
import com.oracle_hcm.dataUploadTool.service.DataMapper;
import com.oracle_hcm.dataUploadTool.service.DirectoryService;
import com.oracle_hcm.dataUploadTool.service.SourceDataConvertor;
import com.oracle_hcm.dataUploadTool.util.FileOperationUtils;
import com.oracle_hcm.dataUploadTool.util.IncrementIdentifierGenerator;

@Component("XMLDataMapper")
public class XMLDataMapper implements DataMapper {

	private final static Logger logger = Logger.getLogger(XMLDataMapper.class);

	private SourceDataConvertor sourceDataConvertor;
	private DirectoryService directoryService;
	private IncrementIdentifierGenerator incrementIdentifierGenerator;

	@Autowired
	public XMLDataMapper(SourceDataConvertor sourceDataConvertor, 
			DirectoryService directoryService, 
			IncrementIdentifierGenerator incrementIdentifierGenerator) {
		this.sourceDataConvertor = sourceDataConvertor;
		this.directoryService = directoryService;
		this.incrementIdentifierGenerator = incrementIdentifierGenerator;
	}

	public void mapData() {
		Document document = null;
		try {
			document = parseWithSAX();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		logNodeInfo(root);

		File targetDirectory = new File(this.directoryService.getTargetDirectory());
		if(!targetDirectory.exists()) {
			FileOperationUtils.createDirectory(targetDirectory.getPath());
		}

		@SuppressWarnings("rawtypes")
		List businessObjectList = root.selectNodes("object");
		for(@SuppressWarnings("rawtypes") Iterator businessObjectListIterator = businessObjectList.iterator();
				businessObjectListIterator.hasNext();) {
			Element businessObject = (Element) businessObjectListIterator.next();
			logNodeInfo(businessObject);
			buildBusinessObject(businessObject);
		};
	}

	private Document parseWithSAX() throws DocumentException {
		SAXReader xmlReader = new SAXReader();
		String mappingConfigurationDirectory = this.directoryService.getMappingConfigurationDirectory();
		File configurationFile = FileOperationUtils.searchSingleFile(
				new File(mappingConfigurationDirectory), "mappingConfiguration", "xml");
		if(configurationFile == null) {
			throw new DataMappingOperationException("G00000", 
					"Cannot find the mapping configuration file in " + mappingConfigurationDirectory);
		}
		return xmlReader.read(configurationFile);
	}

	private void buildBusinessObject(Element businessObject) {
		Element nameElement = (Element) businessObject.selectSingleNode("name");
		validateTextElement(nameElement, "<name/>");
		File dataFile = prepareDataFile(nameElement.getTextTrim());
		PrintStream printStream = null;
		try {
			printStream = new PrintStream(new FileOutputStream(dataFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("rawtypes")
		List components = businessObject.selectNodes("component");
		@SuppressWarnings("rawtypes")
		Iterator childComponentsIterator = components.iterator();
		while(childComponentsIterator.hasNext()) {
			List<String> metadataValues = new ArrayList<String>();
			List<ArrayList<String>> mergeValueSet = new ArrayList<ArrayList<String>>();

			Element component = (Element) childComponentsIterator.next();
			Attribute isTopLevelAttribute = component.attribute("topLevel");
			if(isTopLevelAttribute != null && 
					Boolean.valueOf(isTopLevelAttribute.getValue())) {
				buildTopLevelComponent(component, metadataValues, mergeValueSet);
			}else{
				buildNonTopLevelComponent(component, metadataValues, mergeValueSet);
			}

			printStream.println("COMMENT ################################################");
			printStream.println("COMMENT Business Entity : " + ((Element) component.
					selectSingleNode("discriminator")).getTextTrim());
			printStream.println("COMMENT ################################################");
			printStream.println(getDataLine(metadataValues));
			for(int rowNum = 0;rowNum < mergeValueSet.size();rowNum++) {
				List<String> mergeValues = mergeValueSet.get(rowNum);
				printStream.println(getDataLine(mergeValues));
			}
			printStream.println();
		}
	}

	private void buildTopLevelComponent(Element component, 
			List<String> metadataValues, List<ArrayList<String>> mergeValueSet) {
		Element sourceTableReference = (Element) component.selectSingleNode("source-table-reference");
		if(sourceTableReference == null) {
			throw new DataMappingOperationException("M00002", "The component is a top level component , "
					+ "but the source table reference element is missing from the component.");
		}else if(StringUtils.isEmpty(sourceTableReference.getTextTrim())) {
			throw new DataMappingOperationException("D00000", "The component " + 
					sourceTableReference.getPath() + " is empty");
		}
		String sourceTableName = sourceTableReference.getTextTrim();
		SourceTable sourceTable = getSourceTable(sourceTableName);
		List<SourceRow> sourceRows = sourceTable.getSourceRows();
		if(ArrayUtils.isEmpty(sourceRows.toArray())) {
			throw new DataMappingOperationException("F00000", "The source table " 
					+ sourceTable.getName() + " has no rows");
		}
		for(int i = 0;i < sourceRows.size();i++) {
			mergeValueSet.add(new ArrayList<String>());
		}

		buildDiscriminator(component, metadataValues, mergeValueSet, sourceRows);
		buildSourceKey(component, metadataValues, mergeValueSet, sourceRows);
		buildAttribute(component, metadataValues, mergeValueSet, sourceRows);
		buildDescriptiveFlexfields(component, metadataValues, mergeValueSet, sourceRows);
		buildExtensibleFlexfield(component, metadataValues, mergeValueSet, sourceRows);
	}

	private void buildNonTopLevelComponent(Element component, 
			List<String> metadataValues, List<ArrayList<String>> mergeValueSet) {
		Element sourceTableReference = getSourceTableReference(component);
		String sourceTableName = sourceTableReference.getTextTrim();
		SourceTable sourceTable = getSourceTable(sourceTableName);
		List<SourceRow> sourceRows = sourceTable.getSourceRows();
		if(ArrayUtils.isEmpty(sourceRows.toArray())) {
			throw new DataMappingOperationException("F00000", "The source table " 
					+ sourceTable.getName() + " has no rows");
		}
		for(int i = 0;i < sourceRows.size();i++) {
			mergeValueSet.add(new ArrayList<String>());
		}

		buildDiscriminator(component, metadataValues, mergeValueSet, sourceRows);
		buildSourceKey(component, metadataValues, mergeValueSet, sourceRows);
		buildParentReference(component, metadataValues, mergeValueSet, sourceRows);
		buildAttribute(component, metadataValues, mergeValueSet, sourceRows);
		buildDescriptiveFlexfields(component, metadataValues, mergeValueSet, sourceRows);
		buildExtensibleFlexfield(component, metadataValues, mergeValueSet, sourceRows);
	}

	private void buildDiscriminator(Element component, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows) {
		Element discriminator = (Element) component.selectSingleNode("discriminator");
		validateTextElement(discriminator, "discriminator");
		metadataValues.add(0, "METADATA");
		metadataValues.add(1, discriminator.getTextTrim());
		for(int i = 0;i < sourceRows.size();i++) {
			List<String> mergeValues = mergeValueSet.get(i);
			mergeValues.add(0, "MERGE");
			mergeValues.add(1, discriminator.getTextTrim());
		}
	}

	private void buildSourceKey(Element component, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows) {
		Element sourceKey = (Element) component.selectSingleNode("source-key");
		validateElement(sourceKey, "source-key");
		Element sourceSystemOwner = (Element) sourceKey.selectSingleNode("SourceSystemOwner");
		validateTextElement(sourceSystemOwner, "SourceSystemOwner");
		Element sourceSystemId = (Element) sourceKey.selectSingleNode("SourceSystemId");
		validateElement(sourceSystemId, "SourceSystemId");
		metadataValues.add(2, "SourceSystemOwner");
		metadataValues.add(3, "SourceSystemId");

		Attribute generationStrategy = sourceSystemId.attribute("generation-strategy");
		if(generationStrategy == null || generationStrategy.getValue().equals("reference")) {
			Element sourceColumn = (Element) sourceSystemId.selectSingleNode("source-column");
			validateTextElement(sourceColumn, "source-column");
			for(int i = 0;i < sourceRows.size();i++) {
				SourceRow sourceRow = sourceRows.get(i);
				Map<String, SourceElement> sourceElements = sourceRow.getElements();
				SourceElement sourceElement = sourceElements.get(sourceColumn.getTextTrim());
				List<String> mergeValues = mergeValueSet.get(i);
				mergeValues.add(2, sourceSystemOwner.getTextTrim());
				mergeValues.add(3, sourceElement.getValue());
			}
		}else if(generationStrategy.getValue().equals("increment")) {
			Element prefix = (Element) sourceSystemId.selectSingleNode("prefix");
			validateTextElement(prefix, "SourceSystemId - prefix");
			Element indexGenerator = (Element) sourceSystemId.selectSingleNode("index-generator");
			validateElement(indexGenerator, "SourceSystemId - index-generator");
			Element start = (Element) indexGenerator.selectSingleNode("start");
			Element step = (Element) indexGenerator.selectSingleNode("step");
			validateTextElement(start, "SourceSystemId - index-generator - start");
			validateTextElement(step, "SourceSystemId - index-generator - step");

			incrementIdentifierGenerator.setStart(Integer.parseInt(start.getTextTrim()));
			incrementIdentifierGenerator.setStep(Integer.parseInt(step.getTextTrim()));
			incrementIdentifierGenerator.setPrefix(prefix.getTextTrim());
			incrementIdentifierGenerator.initializeIndex();
			for(int i = 0;i < sourceRows.size();i++) {
				List<String> mergeValues = mergeValueSet.get(i);
				mergeValues.add(2, sourceSystemOwner.getTextTrim());
				mergeValues.add(3, incrementIdentifierGenerator.generateIdentifier());
			}
		}
	}

	private void buildParentReference(Element component, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows) {
		Element parentReference = (Element) component.selectSingleNode("parent-reference");
		if(parentReference == null) {
			throw new DataMappingOperationException("N00000", 
					"The component is not a top level component, "
					+ "but the parent reference element is missing from the component.");
		}
		Element attributeName = (Element) parentReference.selectSingleNode("attribute-name");
		Element sourceColumn = (Element) parentReference.selectSingleNode("source-column");
		validateTextElement(attributeName, "attribute-name");
		validateTextElement(sourceColumn, "source-column");

		metadataValues.add(attributeName.getTextTrim());
		writeMappingData(sourceRows, sourceColumn, mergeValueSet);
	}

	private void buildDescriptiveFlexfields(Element component, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows) {
		Element descriptiveFlexfields = (Element) component.selectSingleNode("dffs");
		if(descriptiveFlexfields != null) {
			@SuppressWarnings("rawtypes")
			List fields = descriptiveFlexfields.selectNodes("field");
			if(fields == null) {
				throw new DataMappingOperationException("000000", 
						"Cannot find any field in " + 
								descriptiveFlexfields.getPath());
			}

			@SuppressWarnings("rawtypes")
			Iterator fieldsIterator = fields.iterator();
			while(fieldsIterator.hasNext()) {
				Element field = (Element) fieldsIterator.next();
				Attribute code = field.attribute("code");
				if(code == null) {
					throw new DataMappingOperationException("000001", 
							"Missing code attribute in " + field.getPath());
				}
				String codeValue = "FLEX:" + code.getValue();
				metadataValues.add(codeValue);

				setFieldPlaceHolder(sourceRows, metadataValues, mergeValueSet, codeValue);
				boolean isDisplay = checkLookup(field);

				@SuppressWarnings("rawtypes")
				List segments = field.selectNodes("segment");
				if(segments == null) {
					throw new DataMappingOperationException("000002", 
							"Cannot find any segment in " + field.getPath());
				}

				@SuppressWarnings("rawtypes")
				Iterator segmentsIterator = segments.iterator();
				while(segmentsIterator.hasNext()) {
					Element segment = (Element) segmentsIterator.next();
					validateElement(segment, "<segment/>");
					buildFlexfieldSegment(segment, metadataValues, mergeValueSet, 
							sourceRows, code, codeValue, isDisplay);
				}
			}
		}
	}

	private void buildExtensibleFlexfield(Element component, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows) {
		Element extensibleFlexfields = (Element) component.selectSingleNode("eff");
		if(extensibleFlexfields != null) {
			Attribute code = extensibleFlexfields.attribute("code");
			if(code == null) {
				throw new DataMappingOperationException("000001", 
						"Missing code attribute in " + extensibleFlexfields.getPath());
			}

			//TODO Define category code
			metadataValues.add("EFF_CATEGORY_CODE");
			for(int i = 0;i < sourceRows.size();i++) {
				List<String> mergeValues = mergeValueSet.get(i);
				mergeValues.add(code.getValue());
			}

			String codeValue = "FLEX:" + code.getValue();
			metadataValues.add(codeValue);

			setFieldPlaceHolder(sourceRows, metadataValues, mergeValueSet, codeValue);
			boolean isDisplay = checkLookup(extensibleFlexfields);

			@SuppressWarnings("rawtypes")
			List segments = extensibleFlexfields.selectNodes("segment");
			if(segments == null) {
				throw new DataMappingOperationException("000002", 
						"Cannot find any segment in " + extensibleFlexfields.getPath());
			}

			@SuppressWarnings("rawtypes")
			Iterator segmentsIterator = segments.iterator();
			while(segmentsIterator.hasNext()) {
				Element segment = (Element) segmentsIterator.next();
				validateElement(segment, "<segment/>");
				buildFlexfieldSegment(segment, metadataValues, mergeValueSet, 
						sourceRows, code, codeValue, isDisplay);
			}
		}
	}

	private Element getSourceTableReference(Element component) {
		Element parentDiscriminator = (Element) component.selectSingleNode("parent-discriminator");
		if(parentDiscriminator == null) {
			throw new DataMappingOperationException("M00001", 
					"The component is not a top level component , "
							+ "but the parent discriminator element is missong from the component.");
		}else if(StringUtils.isEmpty(parentDiscriminator.getTextTrim())) {
			throw new DataMappingOperationException("D00000", "The component " + 
					parentDiscriminator.getPath() + " is empty");
		}

		Element sourceTableReference = (Element) component.selectSingleNode("source-table-reference");
		if(sourceTableReference == null) {
			Element parentComponent = (Element) component.getParent()
					.selectSingleNode("child::component[discriminator='" + 
							parentDiscriminator.getTextTrim() + "']");
			sourceTableReference = (Element) parentComponent
					.selectSingleNode("source-table-reference");
			if(sourceTableReference == null) {
				Attribute isTopLevelAttribute = parentComponent.attribute("topLevel");
				if(isTopLevelAttribute != null && 
						Boolean.valueOf(isTopLevelAttribute.getValue())) {
					throw new DataMappingOperationException("M00002", "The component is a top level component , "
							+ "but the source table reference element is missing from the component.");
				}else{
					sourceTableReference = getSourceTableReference(parentComponent);
				}
			}
		}
		if(StringUtils.isEmpty(sourceTableReference.getTextTrim())) {
			throw new DataMappingOperationException("D00000", "The component " + 
					sourceTableReference.getPath() + " is empty");
		}
		return sourceTableReference;
	}

	private void buildAttribute(Element component, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows) {
		Element attributesElement = (Element) component.selectSingleNode("attributes");
		validateElement(attributesElement, "attributes");
		@SuppressWarnings("rawtypes")
		List attributes = attributesElement.selectNodes("attribute");
		if(attributes == null) {
			throw new DataMappingOperationException("I00000", 
					"There is no attribute defined in the element " + attributesElement.getPath());
		}
		@SuppressWarnings("rawtypes")
		Iterator attributesIterator = attributes.iterator();
		while(attributesIterator.hasNext()) {
			Element attribute = (Element) attributesIterator.next();
			Element name = (Element) attribute.selectSingleNode("name");
			validateTextElement(name, "<name/>");
			Element sourceColumn = (Element) attribute.selectSingleNode("source-column");
			validateTextElement(sourceColumn, "<source-column/>");
			prepareSourceTableReference(attribute, sourceColumn, 
					sourceRows, mergeValueSet);
			metadataValues.add(name.getTextTrim());
		}
	}

	private void prepareSourceTableReference(Element attribute, 
			Element sourceColumn, List<SourceRow> sourceRows,
			List<ArrayList<String>> mergeValueSet) {
		Element sourceTableReference = (Element) attribute.
				selectSingleNode("source-table-reference");
		if(sourceTableReference != null) {
			String sourceTableName = sourceTableReference.getTextTrim();
			if(StringUtils.isEmpty(sourceTableName)) {
				throw new DataMappingOperationException("Null Value", 
						"The element " + sourceTableReference.getName() + 
						" of " + attribute.getPath() + " is empty");
			}
			SourceTable sourceTable = getSourceTable(sourceTableName);
			List<SourceRow> sourceRowsOverride = sourceTable.getSourceRows();
			if(ArrayUtils.isEmpty(sourceRowsOverride.toArray())) {
				throw new DataMappingOperationException("Empty Source Row", 
						"The source table " + sourceTable.getName() + 
						" has no rows");
			}
			if(sourceRows.size() != sourceRowsOverride.size()) {
				throw new DataMappingOperationException("Incompatible Row Numbers", 
						"The source table that the attribute " + attribute.getPath() + 
						" refers to haves the wrong row numbers");
			}
			writeMappingData(sourceRowsOverride, sourceColumn, mergeValueSet);
		}else{
			writeMappingData(sourceRows, sourceColumn, mergeValueSet);
		}
	}

	private void buildFlexfieldSegment(Element segment, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, List<SourceRow> sourceRows, 
			Attribute code, String codeValue, boolean isDisplay) {
		Element context = (Element) segment.selectSingleNode("context");
		validateTextElement(context, "<context/>");
		Element attributeName = (Element) segment.
				selectSingleNode("attribute-name");
		validateTextElement(attributeName, "<attribute-name/>");
		Element sourceColumn = (Element) segment.selectSingleNode("source-column");
		validateTextElement(sourceColumn, "<source-column/>");

		String flexfieldMetadata = attributeName.getTextTrim() + (isDisplay?"_Display":"") +
				"(" + code.getValue() + "=" + context.getTextTrim() + ")";
		metadataValues.add(flexfieldMetadata);

		setFieldPlaceHolder(sourceRows, metadataValues, mergeValueSet, flexfieldMetadata);

		Element sourceTableReference = (Element) segment.
				selectSingleNode("source-table-reference");
		if(sourceTableReference != null) {
			String sourceTableName = sourceTableReference.getTextTrim();
			if(StringUtils.isEmpty(sourceTableName)) {
				throw new DataMappingOperationException("Null Value", 
						"The element " + sourceTableReference.getName() + 
						" of " + segment.getPath() + " is empty");
			}
			SourceTable sourceTable = getSourceTable(sourceTableName);
			List<SourceRow> sourceRowsOverride = sourceTable.getSourceRows();
			if(ArrayUtils.isEmpty(sourceRowsOverride.toArray())) {
				throw new DataMappingOperationException("Empty Source Row", 
						"The source table " + sourceTable.getName() + 
						" has no rows");
			}
			if(sourceRows.size() != sourceRowsOverride.size()) {
				throw new DataMappingOperationException("Incompatible Row Numbers", 
						"The source table that the attribute " + segment.getPath() + 
						" refers to haves the wrong row numbers");
			}
			writeFlexfiledMappingData(metadataValues, mergeValueSet, 
					sourceRowsOverride, sourceColumn, context, 
					codeValue, flexfieldMetadata);
		}else{
			writeFlexfiledMappingData(metadataValues, mergeValueSet, 
					sourceRows, sourceColumn, context, 
					codeValue, flexfieldMetadata);
		}
	}

	private boolean checkLookup(Element flexfieldSegment) {
		Attribute isLookup = flexfieldSegment.attribute("isLookup");
		if(isLookup != null && Boolean.valueOf(isLookup.getValue()) == true) {
			return true;
		}
		return false;
	}

	private void setFieldPlaceHolder(List<SourceRow> sourceRows, List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, String columnName) {
		for(int i = 0;i < sourceRows.size();i++) {
			List<String> mergeValues = mergeValueSet.get(i);
			mergeValues.add(metadataValues.indexOf(columnName), "#NULL");
		}
	}

	private void writeMappingData(List<SourceRow> sourceRows, 
			Element sourceColumn, List<ArrayList<String>> mergeValueSet) {
		for(int i = 0;i < sourceRows.size();i++) {
			SourceRow sourceRow = sourceRows.get(i);
			Map<String, SourceElement> sourceElements = sourceRow.getElements();
			SourceElement sourceElement = sourceElements.get(sourceColumn.getTextTrim());
			List<String> mergeValues = mergeValueSet.get(i);
			mergeValues.add(sourceElement.getValue());
		}
	}

	private void writeFlexfiledMappingData(List<String> metadataValues, 
			List<ArrayList<String>> mergeValueSet, 
			List<SourceRow> sourceRows, 
			Element sourceColumn, 
			Element context, 
			String codeValue, 
			String flexfieldMetadata) {
		for(int i = 0;i < sourceRows.size();i++) {
			SourceRow sourceRow = sourceRows.get(i);
			Map<String, SourceElement> sourceElements = sourceRow.getElements();
			SourceElement sourceElement = sourceElements.get(sourceColumn.getTextTrim());
			List<String> mergeValues = mergeValueSet.get(i);

			String sourceValue = sourceElement.getValue();
			String flexfieldPlaceHolderValue = mergeValues.get(metadataValues.indexOf(codeValue));
			if(flexfieldPlaceHolderValue.equals("#NULL") && StringUtils.isNotEmpty(sourceValue)) {
				mergeValues.set(metadataValues.indexOf(codeValue), context.getTextTrim());
			}
			String metadataFieldPlaceHolderValue = mergeValues.get(metadataValues.indexOf(flexfieldMetadata));
			if(metadataFieldPlaceHolderValue.equals("#NULL")) {
				//If sourceValue equals null, the field in the dat file will be null
				mergeValues.set(metadataValues.indexOf(flexfieldMetadata), sourceValue);
			}
		}
	}

	private SourceTable getSourceTable(String sourceTableName) {
		Map<String, SourceTable> sourceTables = this.sourceDataConvertor.convertData();
		SourceTable sourceTable = sourceTables.get(sourceTableName);
		if(sourceTable == null) {
			throw new DataMappingOperationException("P00000", "The source table name " + 
					sourceTableName + " is wrong. The source table could not be found.");
		}
		return sourceTable;
	}

	private void validateElement(Element element, String xmlTag) {
		if(element == null) {
			throw new DataMappingOperationException("Null Element", 
					"Cannot find the element " + xmlTag);
		}
	}

	private void validateTextElement(Element element, String xmlTag) {
		validateElement(element, xmlTag);
		if(StringUtils.isEmpty(element.getTextTrim())) {
			throw new DataMappingOperationException("Null Value", 
					"The element " + element.getPath() + " is empty");
		}
	}

	private File prepareDataFile(String fileName) {
		String targetDirectory = this.directoryService.getTargetDirectory();
		FileOperationUtils.createDirectory(targetDirectory);
		File dataFile = new File(targetDirectory + File.separator + fileName + ".dat");
		try {
			if(dataFile.createNewFile()){
				System.out.println("The data file " + dataFile.getAbsolutePath() + " was created successfully");
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dataFile;
	}

	private String getDataLine(List<String> dataValues) {
		StringBuffer dataLine = new StringBuffer();
		for(int i = 0;i < dataValues.size();i++) {
			String data = dataValues.get(i);
			if(StringUtils.isEmpty(data)) {
				dataLine.append("");
			}else{
				dataLine.append(dataValues.get(i));
			}
			dataLine.append("|");
		}
		return dataLine.substring(0, dataLine.lastIndexOf("|"));
	}

	private void logNodeInfo(Element element) {
		logger.info(String.format("Name:%s", element.getName()));
		logger.info(String.format("Qualified Name:%s", element.getQualifiedName()));
		logger.info(String.format("Namespace URI:%s", element.getNamespaceURI()));
		logger.info(String.format("Namespace Prefix:%s", element.getNamespacePrefix()));
		logger.info(String.format("Path:%s", element.getPath()));
		logger.info(String.format("Unique Path:%s", element.getUniquePath()));
		logger.info(String.format("Is Root Element:%s", element.isRootElement()));
		logger.info(String.format("Is Read Only:%s", element.isReadOnly()));
		logger.info(String.format("Is Text Only:%s", element.isTextOnly()));
		logger.info(String.format("Node Type:%s", element.getNodeType()));
		logger.info(String.format("Node Type Name:%s", element.getNodeTypeName()));
		logger.info(String.format("Text Trim:%s", element.getTextTrim()));
		logger.info(String.format("To String:%s", element.toString()));
	}
}