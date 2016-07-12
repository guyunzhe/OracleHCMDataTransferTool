package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SimpleFrameDemo02 extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimpleFrameDemo02() {
		JButton b = new JButton("click");

		//setBounds(int x-axis, int y-axis, int width, int height)
		b.setBounds(130, 100, 100, 40);

		add(b);
		setSize(400, 500);
		setLayout(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new SimpleFrameDemo02();
	}
}
