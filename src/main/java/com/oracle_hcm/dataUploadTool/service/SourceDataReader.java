package com.oracle_hcm.dataUploadTool.service;

import java.io.File;
import java.util.Set;

public interface SourceDataReader {

	Set<File> read(String directory);
	File read(String directory, String fileName);
}
