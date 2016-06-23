package com.oracle_hcm.dataUploadTool.directory;

public interface DirectoryService {
	
	String getSourceDirectory();
	
	String getTargetDirectory();

	void setSourceDirectory(String sourceDirectory);

	void setTargetDiractory(String targetDiractory);
}
