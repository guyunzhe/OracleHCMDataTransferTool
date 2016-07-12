package com.oracle_hcm.dataUploadTool.swing.test;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class DisplayImage extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void paint(Graphics g) {
		Toolkit t = Toolkit.getDefaultToolkit();
		Image i = t.getImage(this.getClass().getResource("/youtube-play-button_318-41694.jpg"));
		g.drawImage(i, 120, 100, this);
	}

	public static void main(String[] args) {
		DisplayImage displayImage = new DisplayImage();
		JFrame f = new JFrame();
		f.add(displayImage);
		f.setSize(400, 400);
		f.setVisible(true);
	}
}
