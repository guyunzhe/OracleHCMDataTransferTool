package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

public class JRadioButtonDemo {

	JFrame f;

	public JRadioButtonDemo() {
		f = new JFrame();

		JRadioButton r1=new JRadioButton("A) Male");
		JRadioButton r2=new JRadioButton("B) FeMale");
		r1.setBounds(50, 100, 100, 30);
		r2.setBounds(50, 150, 100, 30);

		//Group the radios
		ButtonGroup bg = new ButtonGroup();
		bg.add(r1);
		bg.add(r2);

		f.add(r1);
		f.add(r2);

		f.setSize(300, 300);
		f.setLayout(null);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		new JRadioButtonDemo();
	}
}
