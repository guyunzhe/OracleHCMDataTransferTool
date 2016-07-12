package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class JComboBoxDemo {

	JFrame f;

	public JComboBoxDemo() {
		f = new JFrame("Combo ex");

		String country[] = {"India", "Aus", "U.S.A", "England", "Newzeland"};

		//JComboBox(Object[] items)
		JComboBox<String> cb = new JComboBox<String>(country);
		cb.setBounds(50, 50, 90, 20);
		f.add(cb);

		f.setLayout(null);
		f.setSize(400,500);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		new JComboBoxDemo();
	}
}
