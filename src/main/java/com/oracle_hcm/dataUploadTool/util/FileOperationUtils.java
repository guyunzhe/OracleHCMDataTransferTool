package com.oracle_hcm.dataUploadTool.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class FileOperationUtils {

	private final static Logger logger = Logger.getLogger(FileOperationUtils.class);

	public static boolean isDirectoryRecursive(File directory) {
		boolean isRecursive = false;
		File[] files = directory.listFiles();
		if(files != null) {
			if(ArrayUtils.isNotEmpty(files)) {
				for(int i = 0;i < files.length;i++) {
					if(files[i].isDirectory()) {
						isRecursive = true;
					}
				}
			}else{
				logger.warn(String.format("The directory(%s) is empty", directory.getPath()));
			}
		}else{
			logger.fatal(String.format("The path(%s) does not denote a directory", directory.getPath()));
			System.exit(0);
		}

		return isRecursive;
	}

	public static Set<File> searchFile(File directory, String fileName) {
		return searchFile(directory, fileName, false);
	}

	/**
	 * Query the files by the filename from the directory
	 * <p>
	 * 
	 * </p>
	 * @param directory  the directory where to search the files
	 * @param filename  the filename by which to query the files
	 * @return the set of files by the filename
	 * */
	public static Set<File> searchFile(File directory, String fileName, boolean recursive) {
		Set<File> searchedFiles = new HashSet<File>();

		File[] files = directory.listFiles();
		if(files != null) {
			if(ArrayUtils.isNotEmpty(files)) {
				List<File> fileList = Arrays.asList(files);
				Iterator<File> fileIterator = fileList.iterator();
				while(fileIterator.hasNext()) {
					File file = fileIterator.next();
					if(file.isFile()) {
						String name = FilenameUtils.removeExtension(file.getName());
						if(name.equals(fileName)) {
							logger.info(String.format("The file searched: %s", file.getPath()));
							searchedFiles.add(file);
						}
					}else if(file.isDirectory()) {
						if(recursive) {
							searchedFiles.addAll(searchFile(file, fileName, true));
						}
					}
				}
			}else{
				logger.error(String.format("The directory(%s) is empty", directory.getPath()));
				return null;
			}
		}else{
			logger.error(String.format("The abstract pathname(%s) does not denote a directory", 
					directory.getPath()));
			return null;
		}

		return searchedFiles;
	}

	public static Set<File> searchAllFiles(File directory) {
		return searchAllFiles(directory, false);
	}
	
	
	/**
	 * Query all the files from the directory
	 * @param directory  the directory where to search all the files
	 * @return the set of files
	 * */
	public static Set<File> searchAllFiles(File directory, boolean recursive) {
		Set<File> allFiles = new HashSet<File>();

		File[] files = directory.listFiles();
		if(files != null) {
			if(ArrayUtils.isNotEmpty(files)) {
				List<File> fileList = Arrays.asList(files);
				Iterator<File> fileIterator = fileList.iterator();
				while(fileIterator.hasNext()) {
					File file = fileIterator.next();
					if(file.isFile()) {
						logger.info(String.format("The file searched: %s", file.getPath()));
						allFiles.add(file);
					}else if(file.isDirectory()) {
						if(recursive) {
							allFiles.addAll(searchAllFiles(file, true));
						}
					}
				}
			}else{
				logger.error(String.format("The directory(%s) is empty", directory.getPath()));
				return null;
			}
		}else{
			logger.error(String.format("The abstract pathname(%s) does not denote a directory", 
					directory.getPath()));
			return null;
		}

		return allFiles;
	}

	/**
	 * @param fileLocated The file whose root directory is to be switched
	 * @return null If the fileLocated dose not denote a file 
	 * */
	public static File switchRootDirectory(File fileLocated, String sourceRootDirectory, String targetRootDirectory) {
		File targetParentDirectory = null;

		if(fileLocated.isFile()) {
			String locatedParentDirectory = fileLocated.getParent();

			int lengthOfSourceRootDirectory = sourceRootDirectory.length();
			String subDirectory = locatedParentDirectory.substring(lengthOfSourceRootDirectory);
			targetParentDirectory = new File(targetRootDirectory + subDirectory);
		}else{
			logger.error(String.format("The file(%s) dose not denote a file", fileLocated.getPath()));
		}

		return targetParentDirectory;
	}

	public static void createDirectory(String path) {
		createDirectory(path, true);
	}

	public static void createDirectory(String path, boolean recursive) {
		boolean success = false;

		if(StringUtils.isEmpty(path)) {
			logger.warn("Unable to create the directory(The path is null)");
			return;
		}

		File directory = new File(path);
		if(directory.exists()) {
			logger.info(String.format("The path(%s) has already been created", directory.getPath()));
			return;
		}

		if(recursive) {
			success = directory.mkdirs();
		}else{
			success = directory.mkdir();
		}

		if(success) {
			logger.info(String.format("The directory(%s) was created successfully", directory.getPath()));
		}else{
			logger.error(String.format("The directory(%s) was created with error", directory.getPath()));
		}
	}

	public static void createFile(File file) {
		boolean success = false;

		if(file == null) {
			logger.error("Tatal error: The file is null");
			return;
		}
		if(file.exists()) {
			logger.info(String.format("The file(%s) has already been created", file.getPath()));
			return;
		}

		File parentDirectory = file.getParentFile();
		if(!parentDirectory.exists()) {
			createDirectory(parentDirectory.getPath());
		}

		try {
			success = file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(success) {
			logger.info(String.format("The file(%s) was created successfully", file.getPath()));
		}else{
			logger.error(String.format("The file(%s) was created with error", file.getPath()));
		}
	}
}