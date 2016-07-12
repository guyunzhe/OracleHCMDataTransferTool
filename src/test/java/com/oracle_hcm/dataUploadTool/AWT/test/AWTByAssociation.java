package com.oracle_hcm.dataUploadTool.AWT.test;

import java.awt.Button;
import java.awt.Frame;

public class AWTByAssociation {

	public AWTByAssociation() {
		Frame f = new Frame();

		Button b = new Button("click me");
		b.setBounds(30, 50, 80, 30);

		f.add(b);
		f.setSize(300, 300);
		f.setLayout(null);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AWTByInheritance instance = new AWTByInheritance();
	}
}
