package com.oracle_hcm.dataUploadTool.service.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oracle_hcm.dataUploadTool.service.DirectoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:service.xml"})
//@ContextConfiguration("service.xml")
/*
 * @ContextConfiguration("service.xml") annotation will cause 
 * severe Failed to load Application Context error
 * */
public class DirectoryServiceTest {

	@Autowired
	private DirectoryService directoryService;

	@Test
	public void testGetSourceDirectory() {
		System.out.println(this.directoryService.getSourceDirectory());
	}

	@Test
	public void testGetTargetDirectory() {
		System.out.println(this.directoryService.getTargetDirectory());
	}

	@Test
	public void testGetMappingConfigurationDirectory() {
		System.out.println(this.directoryService.getMappingConfigurationFile());
	}
}
