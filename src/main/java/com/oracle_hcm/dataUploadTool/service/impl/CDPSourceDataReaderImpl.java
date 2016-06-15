package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.util.Set;

import org.apache.log4j.Logger;

import com.oracle_hcm.dataUploadTool.service.SourceDataReader;
import com.oracle_hcm.dataUploadTool.util.FileOperationUtils;

public class CDPSourceDataReaderImpl implements SourceDataReader {
	
	private final Logger logger = Logger.getLogger(CDPSourceDataReaderImpl.class);
	
	public Set<File> read(String directory) {
		File exportedDirectory = new File(directory);
		if(!exportedDirectory.exists()) {
			String exportedPath = exportedDirectory.getPath();
			logger.error(String.format("The directory(%s) dose not exist", exportedPath));

			FileOperationUtils.createDirectory(exportedPath);
			logger.info(String.format("The directory(%s) has been created", exportedPath));
			
			//TODO throw new RuntimeException
		}
		Set<File> sourceFiles = FileOperationUtils.searchAllFiles(exportedDirectory, true);
		if(sourceFiles == null || sourceFiles.isEmpty()) {
			//TODO throw new RuntimeException
		}
		
		return sourceFiles;
	}

	public File read(String directory, String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

}
