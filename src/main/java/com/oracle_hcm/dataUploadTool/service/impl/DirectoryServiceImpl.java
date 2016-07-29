package com.oracle_hcm.dataUploadTool.service.impl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.oracle_hcm.dataUploadTool.service.DirectoryService;

/*
 * @Component annotation is not allowed here if there is
 * bean configuraion in the xml file
 * */
@Component("directoryService")
public class DirectoryServiceImpl implements DirectoryService {

	@Value("#{locations.source}")
	private String sourceDirectory;
	@Value("#{locations.target}")
	private String targetDirectory;
	@Value("#{locations.mappingConfigurationFile}")
	private String mappingConfigurationFile;

	@Value("#{locations}")
	public void setLocationsConfiguration(Properties locations){
		this.sourceDirectory = locations.getProperty("source");
		this.targetDirectory = locations.getProperty("target");
		this.mappingConfigurationFile = locations.getProperty("mappingConfigurationFile");
	}

//	@ConstructorProperties({"sourceDirectory", "targetDiractory", "mappingConfigurationDirectory"})
//	public DirectoryServiceImpl(String sourceDirectory, String targetDiractory, String mappingConfigurationDirectory) {
//		this.sourceDirectory = sourceDirectory;
//		this.targetDiractory = targetDiractory;
//		this.mappingConfigurationDirectory = mappingConfigurationDirectory;
//	}

	@Override
	public String getSourceDirectory() {
		return this.sourceDirectory;
	}
	@Override
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	@Override
	public void setTargetDiractory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
	@Override
	public String getTargetDirectory() {
		return this.targetDirectory;
	}

	@Override
	public void setMappingConfigurationFile(String mappingConfigurationFile) {
		this.mappingConfigurationFile = mappingConfigurationFile;
	}
	@Override
	public String getMappingConfigurationFile() {
		return this.mappingConfigurationFile;
	}
}
