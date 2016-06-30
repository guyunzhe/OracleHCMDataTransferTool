package com.oracle_hcm.dataUploadTool.service;

public interface DirectoryService {

	void setSourceDirectory(String sourceDirectory);
	String getSourceDirectory();

	void setTargetDiractory(String targetDirectory);
	String getTargetDirectory();

	void setMappingConfigurationDirectory(String mappingConfigurationDirectory);
	String getMappingConfigurationDirectory();
}
