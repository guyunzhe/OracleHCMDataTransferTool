package com.oracle_hcm.dataUploadTool.AWT.event.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Outer implements ActionListener {

	AEvent2 obj;

	Outer(AEvent2 obj){
		this.obj=obj;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		obj.tf.setText("welcome");
	}
}
