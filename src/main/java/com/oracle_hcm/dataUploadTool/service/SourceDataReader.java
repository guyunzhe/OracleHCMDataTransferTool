package com.oracle_hcm.dataUploadTool.service;

import java.io.File;
import java.util.Map;

public interface SourceDataReader {

	Map<String, File> readData();
}
