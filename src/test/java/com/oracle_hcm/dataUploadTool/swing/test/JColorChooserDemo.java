package com.oracle_hcm.dataUploadTool.swing.test;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;

public class JColorChooserDemo extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JButton b;
	Container c;

	public JColorChooserDemo() {
		c = getContentPane();
		c.setLayout(new FlowLayout());

		b = new JButton("color");
		b.addActionListener(this);

		c.add(b);
	}

	public static void main(String[] args) {
		JColorChooserDemo ch = new JColorChooserDemo();
		ch.setSize(400,400);
		ch.setVisible(true);
		ch.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Color initialcolor = Color.RED;

		/*
		 * public static Color showDialog(Component c, String title, 
		 * 				Color initialColor): 
		 * is used to show the color-chooser dialog box.
		 * */
		Color color = JColorChooser.showDialog(this, 
				"Select a color", initialcolor);  
		c.setBackground(color);
	}
}
