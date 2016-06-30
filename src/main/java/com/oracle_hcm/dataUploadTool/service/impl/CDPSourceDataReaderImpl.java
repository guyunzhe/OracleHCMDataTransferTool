package com.oracle_hcm.dataUploadTool.service.impl;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle_hcm.dataUploadTool.exceptions.SourceDataReadException;
import com.oracle_hcm.dataUploadTool.service.DirectoryService;
import com.oracle_hcm.dataUploadTool.service.SourceDataReader;
import com.oracle_hcm.dataUploadTool.util.FileOperationUtils;

@Component
public class CDPSourceDataReaderImpl implements SourceDataReader {

	final private DirectoryService directoryService;

	@Autowired
	public CDPSourceDataReaderImpl(DirectoryService directoryService) {
		this.directoryService = directoryService;
	}

	public Map<String, File> readData() {
		String sourceDirectory = this.directoryService.getSourceDirectory();
		File exportedDirectory = new File(sourceDirectory);
		if(!exportedDirectory.exists()) {
			String exportedPath = exportedDirectory.getPath();
			FileOperationUtils.createDirectory(exportedPath);
			throw new SourceDataReadException("B00000", "The source directory does not exist");
		}

		Map<String, File> sourceFiles = FileOperationUtils.searchAllFiles(exportedDirectory, true);
		if(sourceFiles.isEmpty()) {
			throw new SourceDataReadException("B00001", "There is no source data file in the source directory");
		}
		return sourceFiles;
	}
}
