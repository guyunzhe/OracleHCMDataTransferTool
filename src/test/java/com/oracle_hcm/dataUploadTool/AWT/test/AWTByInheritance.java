package com.oracle_hcm.dataUploadTool.AWT.test;

import java.awt.Button;
import java.awt.Frame;

public class AWTByInheritance extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AWTByInheritance() {
		Button b = new Button("click me");
		b.setBounds(30, 100, 80, 30);

		add(b);

		//frame size 300 width and 300 height
		setSize(300, 300);
		setLayout(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		AWTByInheritance instance = new AWTByInheritance();
	}
}
