package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

public class ImageButton {
	
	JFrame f;
	
	public ImageButton() {
		f = new JFrame();
		JButton b = new JButton(new ImageIcon(
				this.getClass().getResource("/youtube-play-button_318-41694.jpg")));

		//setBounds(int x-axis, int y-axis, int width, int height)
		b.setBounds(130, 100, 100, 40);

		f.add(b);
		f.setSize(300, 400);
		f.setLayout(null);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		new ImageButton();
	}

}
