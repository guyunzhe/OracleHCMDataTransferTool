package com.oracle_hcm.dataUploadTool.AWT.event.test;

import java.awt.Button;
import java.awt.Frame;
import java.awt.TextField;

public class AEvent2 extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TextField tf;

	public AEvent2() {
		tf = new TextField();
		tf.setBounds(60, 50, 170, 20);

		Button b = new Button("click me");
		b.setBounds(100, 120, 80, 30);

		Outer o = new Outer(this);
		b.addActionListener(o);

		add(b);
		add(tf);

		setSize(300, 300);
		setLayout(null);
		setVisible(true);
	}

	public static void main(String[] args) {
		new AEvent2();
	}
}
