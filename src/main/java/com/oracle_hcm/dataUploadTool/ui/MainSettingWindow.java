package com.oracle_hcm.dataUploadTool.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle_hcm.dataUploadTool.service.DataMapper;
import com.oracle_hcm.dataUploadTool.service.DirectoryService;

@Component("mainSettingWindow")
public class MainSettingWindow extends JFrame implements ActionListener {
	
	private final static Logger logger = Logger.getLogger(MainSettingWindow.class);
	
	final private JFileChooser fileChooser;
	private JTextArea textArea;
	
	final private JButton openSourceDirectoryButton;
	final private JButton opentargetDirectoryButton;
	final private JButton openConfigurationFileButton;

	final private JButton startButton;
	
	final private DirectoryService directoryService;
	final private DataMapper dataMapper;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == openSourceDirectoryButton) {
			openSourceDirectory();
		}else if(e.getSource() == opentargetDirectoryButton) {
			openTargetDirectory();
		}else if(e.getSource() == openConfigurationFileButton) {
			openConfigurationFile();
		}
		
		if(e.getSource() == startButton) {
			dataMapper.mapData();
		}
	}
	
	@Autowired
	public MainSettingWindow(DirectoryService directoryService, 
			DataMapper dataMapper) {
		this.directoryService = directoryService;
		this.dataMapper = dataMapper;
		
		openSourceDirectoryButton = 
				new JButton("Choose Source Directory");
		opentargetDirectoryButton = 
				new JButton("Choose Target Directory");
		openConfigurationFileButton = 
				new JButton("Choose Configuration Directory");
		
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		startButton.setBounds(50, 200, 900, 50);
		startButton.setBackground(Color.ORANGE);
		
		openSourceDirectoryButton.addActionListener(this);
		opentargetDirectoryButton.addActionListener(this);
		openConfigurationFileButton.addActionListener(this);
		
		openSourceDirectoryButton.setBounds(50, 100, 250, 50);
		opentargetDirectoryButton.setBounds(350, 100, 250, 50);
		openConfigurationFileButton.setBounds(650, 100, 250, 50);
		
		textArea = new JTextArea(300, 300);
		textArea.setBounds(50, 300, 300, 300);
		textArea.setBackground(Color.black);
		textArea.setForeground(Color.white);
		
		fileChooser = new JFileChooser();
		
		add(openSourceDirectoryButton);
		add(opentargetDirectoryButton);
		add(openConfigurationFileButton);
		add(startButton);
		add(textArea);
		
		setVisible(true);
		setSize(1080, 760);
		setLayout(null);
	}
	
	private void openSourceDirectory() {
		fileChooser.setDialogTitle("Open Source Directory");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(MainSettingWindow.this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File sourceDirectory = fileChooser.getSelectedFile();
			logger.info(String.format("The source directory is:%s", 
					sourceDirectory.getPath()));
			textArea.append(sourceDirectory.getPath());
			directoryService.setSourceDirectory(sourceDirectory.getPath());
		}else{
			logger.warn("Open command cancelled by user");
		}
	}
	
	private void openTargetDirectory() {
		fileChooser.setDialogTitle("Open Target Directory");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(MainSettingWindow.this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File targetDirectory = fileChooser.getSelectedFile();
			logger.info(String.format("The target directory is:%s", 
					targetDirectory.getPath()));
			textArea.append(targetDirectory.getPath());
			directoryService.setTargetDiractory(targetDirectory.getPath());
		}else{
			logger.warn("Open command cancelled by user");
		}
	}
	
	private void openConfigurationFile() {
		fileChooser.setDialogTitle("Open Configuration File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fileChooser.showOpenDialog(MainSettingWindow.this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File configurationFile = fileChooser.getSelectedFile();
			logger.info(String.format("The configuration file is:%s", 
					configurationFile.getPath()));
			textArea.append(configurationFile.getPath());
			directoryService.setMappingConfigurationFile(
					configurationFile.getPath());
		}else{
			logger.warn("Open command cancelled by user");
		}
	}
}
