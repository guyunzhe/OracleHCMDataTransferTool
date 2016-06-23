package com.oracle_hcm.dataUploadTool.directory.impl;

import com.oracle_hcm.dataUploadTool.directory.DirectoryService;

public class DirectoryServiceImpl implements DirectoryService {
	
	private String sourceDirectory;
	private String targetDiractory;
	
	private static DirectoryService directoryService = new DirectoryServiceImpl();
	private DirectoryServiceImpl() {}
	public static DirectoryService getInstance(String sourceDirectory, String targetDiractory) {
		directoryService.setSourceDirectory(sourceDirectory);
		directoryService.setTargetDiractory(targetDiractory);
		return directoryService;
	}
	
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}
	public void setTargetDiractory(String targetDiractory) {
		this.targetDiractory = targetDiractory;
	}
	public String getSourceDirectory() {
		return this.sourceDirectory;
	}
	public String getTargetDirectory() {
		return this.targetDiractory;
	}
}
