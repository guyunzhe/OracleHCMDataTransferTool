package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.JButton;
import javax.swing.JFrame;

public class SimpleFrameDemo01 {

	JFrame f;

	public SimpleFrameDemo01() {
		f = new JFrame();
		JButton b = new JButton("click");

		//setBounds(int x-axis, int y-axis, int width, int height)
		b.setBounds(130, 100, 100, 40);

		f.add(b);
		f.setSize(400, 500);
		f.setLayout(null);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		new SimpleFrameDemo01();
	}
}
