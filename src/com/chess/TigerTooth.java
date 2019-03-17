package com.chess;

import com.chess.gui.Table;
import com.chess.gui.WelcomeScreen;

/**
 * Launcher class, launch this to launch game.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class TigerTooth {

	/**
	 * Main Method.
	 *
	 * @param args
	 *            Command Line arguments
	 */
	public static void main(final String args[]) {
		try {
			@SuppressWarnings("unused")
			WelcomeScreen welcome = new WelcomeScreen();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(99);
		}
		Table.get().show();
	}
}