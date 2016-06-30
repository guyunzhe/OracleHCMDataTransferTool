package com.oracle_hcm.dataUploadTool.application;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.oracle_hcm.dataUploadTool.service.DirectoryService;
import com.oracle_hcm.dataUploadTool.util.SpringContextUtil;

public class Application {

	public static void main(String[] args) {
		String sourceDirectory = args[0] != null?args[0]:null;
		String targetDirectory = args[1] != null?args[1]:null;
		String configurationDirectory = args[2] != null?args[2]:null;

		SpringContextUtil contextUtils = new SpringContextUtil();
		contextUtils.setApplicationContext(new ClassPathXmlApplicationContext("service.xml"));
		DirectoryService directoryService = (DirectoryService) SpringContextUtil.
				getBean("directoryService", DirectoryService.class);
		if(StringUtils.isNotEmpty(sourceDirectory)) {
			directoryService.setSourceDirectory(sourceDirectory);
		}
		if(StringUtils.isNotEmpty(configurationDirectory)) {
			directoryService.setTargetDiractory(targetDirectory);
		}
		if(StringUtils.isNotEmpty(configurationDirectory)) {
			directoryService.setMappingConfigurationDirectory(configurationDirectory);
		}
		System.out.println(directoryService.getSourceDirectory());
		System.out.println(directoryService.getTargetDirectory());
		System.out.println(directoryService.getMappingConfigurationDirectory());
	}
}
