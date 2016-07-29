package com.oracle_hcm.dataUploadTool.application;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.oracle_hcm.dataUploadTool.ui.MainSettingWindow;
import com.oracle_hcm.dataUploadTool.util.SpringContextUtil;

public class Application {

//	private final static Logger logger = Logger.getLogger(Application.class);

	public static void main(String[] args) {
//		String sourceDirectory = args[0] != null?args[0]:null;
//		String targetDirectory = args[1] != null?args[1]:null;
//		String configurationDirectory = args[2] != null?args[2]:null;

		SpringContextUtil contextUtils = new SpringContextUtil();
		contextUtils.setApplicationContext(
				new ClassPathXmlApplicationContext("service.xml"));
//		DirectoryService directoryService = (DirectoryService) SpringContextUtil.
//				getBean("directoryService", DirectoryService.class);

		SpringContextUtil.getBean("mainSettingWindow", MainSettingWindow.class);

//		if(StringUtils.isNotEmpty(sourceDirectory)) {
//			directoryService.setSourceDirectory(sourceDirectory);
//		}
//		if(StringUtils.isNotEmpty(configurationDirectory)) {
//			directoryService.setTargetDiractory(targetDirectory);
//		}
//		if(StringUtils.isNotEmpty(configurationDirectory)) {
//			directoryService.setMappingConfigurationDirectory(configurationDirectory);
//		}

//		int numberOfDirectories = args.length;
//		if(numberOfDirectories == 1) {
//			directoryService.setSourceDirectory(args[0]);
//		}else if(numberOfDirectories == 2) {
//			directoryService.setSourceDirectory(args[0]);
//			directoryService.setTargetDiractory(args[1]);
//		}else if(numberOfDirectories == 3) {
//			directoryService.setSourceDirectory(args[0]);
//			directoryService.setTargetDiractory(args[1]);
//			directoryService.setMappingConfigurationDirectory(args[2]);
//		}
//		logger.info(String.format("Source Directory Override:%s", 
//				directoryService.getSourceDirectory()));
//		logger.info(String.format("Target Directory Override:%s", 
//				directoryService.getTargetDirectory()));
//		logger.info(String.format("Mapping Configuration Directory Override:%s", 
//				directoryService.getMappingConfigurationDirectory()));
//		System.out.println(directoryService.getSourceDirectory());
//		System.out.println(directoryService.getTargetDirectory());
//		System.out.println(directoryService.getMappingConfigurationDirectory());

//		DataMapper dataMapper = (DataMapper) SpringContextUtil.
//				getBean("XMLDataMapper", XMLDataMapper.class);
//		dataMapper.mapData();
	}
}
