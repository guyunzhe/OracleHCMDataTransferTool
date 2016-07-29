package com.oracle_hcm.dataUploadTool.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class MappingConfigurationWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JButton addNewBOButton;
	
	public MappingConfigurationWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar greenMenuBar = new JMenuBar();
		greenMenuBar.setOpaque(true);
        greenMenuBar.setBackground(new Color(154, 165, 127));
        greenMenuBar.setPreferredSize(new Dimension(200, 20));
        
        setSize(1080, 760);
        setVisible(true);
        setLayout(new FlowLayout());
        
        ImageIcon addNewBOIcon = createImageIcon("images/plus.png");
        addNewBOButton = new JButton(addNewBOIcon);
        addNewBOButton.setBounds(50, 100, 32, 32);
        
        
        add(addNewBOButton);
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
    private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MappingConfigurationWindow.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
	
	public static void main(String[] args) {
		new MappingConfigurationWindow();
	}

}
