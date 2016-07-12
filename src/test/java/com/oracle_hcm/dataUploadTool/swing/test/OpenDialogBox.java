package com.oracle_hcm.dataUploadTool.swing.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

public class OpenDialogBox extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JMenuBar mb;
	JMenu file;
	JMenuItem open;
	JTextArea ta;

	public OpenDialogBox() {
		open = new JMenuItem("Open File");
		open.addActionListener(this);

		file = new JMenu("File");
		file.add(open);

		mb = new JMenuBar();
		mb.setBounds(0, 0, 800, 20);
		mb.add(file);

		ta = new JTextArea(800, 800);
		ta.setBounds(0, 20, 800, 800);

		add(mb);
		add(ta);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == open){
			openFile();
		}
	}

	void openFile(){
		JFileChooser fc = new JFileChooser();
		/*
		 * Pops up an "Open File" file chooser dialog. 
		 * Note that the text that appears in the approve button 
		 * is determined by the L&F.
		 * 
		 * Parameters:parent     the parent component of the dialog, 
		 * can be null; 
		 * 
		 * see showDialog for detailsReturns:
		 * the return state of the file chooser on popdown: 
		 * •JFileChooser.CANCEL_OPTION 
		 * •JFileChooser.APPROVE_OPTION 
		 * •JFileChooser.ERROR_OPTION if an error occurs 
		 * or the dialog is dismissed
		 * */
		int i = fc.showOpenDialog(this);

		if(i == JFileChooser.APPROVE_OPTION){
		File f = fc.getSelectedFile();
		String filepath = f.getPath();

		displayContent(filepath);
		}
	}

	void displayContent(String fpath){
		try{
			BufferedReader br = new BufferedReader(new FileReader(fpath));
			String s1 = "", s2 = "";

			while((s1 = br.readLine()) != null){
				s2 += s1+"\n";
			}
			ta.setText(s2);
			br.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		OpenDialogBox dialogBox = new OpenDialogBox();
		dialogBox.setSize(800, 800);
		dialogBox.setLayout(null);
		dialogBox.setVisible(true);
		dialogBox.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
