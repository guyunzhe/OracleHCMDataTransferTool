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
	@Value("#{locations.mappingConfiguration}")
	private String mappingConfigurationDirectory;

	@Value("#{locations}")
	public void setLocationsConfiguration(Properties locations){
		this.sourceDirectory = locations.getProperty("source");
		this.targetDirectory = locations.getProperty("target");
		this.mappingConfigurationDirectory = locations.getProperty("mappingConfiguration");
	}

//	@ConstructorProperties({"sourceDirectory", "targetDiractory", "mappingConfigurationDirectory"})
//	public DirectoryServiceImpl(String sourceDirectory, String targetDiractory, String mappingConfigurationDirectory) {
//		this.sourceDirectory = sourceDirectory;
//		this.targetDiractory = targetDiractory;
//		this.mappingConfigurationDirectory = mappingConfigurationDirectory;
//	}

	public String getSourceDirectory() {
		return this.sourceDirectory;
	}
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public void setTargetDiractory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}
	public String getTargetDirectory() {
		return this.targetDirectory;
	}

	public void setMappingConfigurationDirectory(String mappingConfigurationDirectory) {
		this.mappingConfigurationDirectory = mappingConfigurationDirectory;
	}
	public String getMappingConfigurationDirectory() {
		return this.mappingConfigurationDirectory;
	}
}
