package com.oracle_hcm.dataUploadTool.service.test;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oracle_hcm.dataUploadTool.service.SourceDataReader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service.xml"})
public class SourceDataReaderTest {

	@Autowired
	private SourceDataReader sourceDataReader;

	@Test
	public void testReadData() {
		Map<String, File> sourceFiles = this.sourceDataReader.readData();
		for(String fileName : sourceFiles.keySet()) {
			String name = sourceFiles.get(fileName).getName();
			System.out.println(fileName + "-->" + name + "-->" + FilenameUtils.getExtension(name));
		}
	}

}
