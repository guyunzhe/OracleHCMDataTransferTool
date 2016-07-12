package com.oracle_hcm.dataUploadTool.swing.test;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class JSliderDemo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JSliderDemo() {
		/*
		 * JSlider(int orientation, int min, int max, int value): 
		 * creates a slider using the given orientation, min, max and value.
		 * */
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, 25);
		/*
		 * public void setMinorTickSpacing(int n): 
		 * is used to set the minor tick spacing to the slider.
		 * */
		slider.setMinorTickSpacing(2);
		/*
		 * public void setMajorTickSpacing(int n): 
		 * is used to set the major tick spacing to the slider.
		 * */
		slider.setMajorTickSpacing(10);
		/*
		 * public void setPaintTicks(boolean b): 
		 * is used to determine whether tick marks are painted.
		 * */
		slider.setPaintTicks(true);
		/*
		 * public void setPaintLabels(boolean b): 
		 * is used to determine whether labels are painted.
		 * */
		slider.setPaintLabels(true);
		/*
		 * public void setPaintTracks(boolean b): 
		 * is used to determine whether track is painted.
		 * */
		slider.setPaintTrack(true);

		JPanel panel=new JPanel();
		panel.add(slider);

		add(panel);
	}

	public static void main(String[] args) {
		JSliderDemo demo = new JSliderDemo();
		demo.pack();
		demo.setVisible(true);
	}
}
