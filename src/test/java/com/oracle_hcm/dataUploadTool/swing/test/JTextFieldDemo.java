package com.oracle_hcm.dataUploadTool.swing.test;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class JTextFieldDemo {

	JTextArea area;
	JFrame f;

	public JTextFieldDemo() {
		f = new JFrame();

		area = new JTextArea(300, 300);
		area.setBounds(10, 30, 300, 300);

		area.setBackground(Color.black);
		area.setForeground(Color.white);

		area.append("Hello World");
		System.out.println(area.getRows());
		System.out.println(area.getColumns());

		f.add(area);

		f.setSize(400, 400);
		f.setLayout(null);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		new JTextFieldDemo();
	}
}
