package com.oracle_hcm.dataUploadTool.AWT.event.test;

import java.awt.Button;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AEvent extends Frame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TextField tf;

	public AEvent() {
		tf = new TextField();
		tf.setBounds(60, 50, 170, 20);

		Button b = new Button("click me");
		b.setBounds(100, 120, 80, 30);

		b.addActionListener(this);

		add(b);
		add(tf);

		setSize(300, 300);
		setLayout(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		tf.setText("Welcome");
	}

	public static void main(String[] args) {
		new AEvent();
	}
}
