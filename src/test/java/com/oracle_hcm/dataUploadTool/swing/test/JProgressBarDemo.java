package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class JProgressBarDemo extends JFrame {

	JProgressBar jb;
	int i = 0, num = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JProgressBarDemo() {
		/*
		 * JProgressBar(int min, int max): 
		 * is used to create a horizontal progress bar 
		 * with the specified minimum and maximum value.
		 * */
		jb = new JProgressBar(0, 2000);
		jb.setBounds(40, 40, 200, 30);

		/*
		 * public void setValue(int value): 
		 * is used to set the current value on the progress bar.
		 * */
		jb.setValue(0);
		/*
		 * public void setStringPainted(boolean b): 
		 * is used to determine whether string should be displayed.
		 * */
		jb.setStringPainted(true);

		add(jb);
		setSize(400, 400);
		setLayout(null);
	}

	public static void main(String[] args) {
		JProgressBarDemo m = new JProgressBarDemo();
		m.setVisible(true);
		m.iterate();
	}

	public void iterate(){
		while(i <= 2000){
			jb.setValue(i);
			i += 20;
			try{
				Thread.sleep(150);
			}catch(Exception e){
				e.printStackTrace();
			}
		} 
	}
}
