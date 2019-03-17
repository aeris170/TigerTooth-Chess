package com.chess.gui;

import java.io.File;
import java.awt.Graphics;
import javax.swing.JWindow;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The Class WelcomeScreen.
 */
public class WelcomeScreen extends JWindow {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1911693312823148525L;

	/**
	 * Instantiates a new welcome screen.
	 *
	 * @throws InterruptedException
	 *             the ýnterrupted exception
	 */
	@SuppressWarnings("deprecation")
	public WelcomeScreen() throws InterruptedException {
		setSize(600, 400);
		setLocationRelativeTo(null);
		show();
		Thread.sleep(7000);
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		try {
			g.drawImage(ImageIO.read(new File("art/main_elements/welcome.png")), 0, 0, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}