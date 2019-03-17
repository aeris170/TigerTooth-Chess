package com.chess.pgn;

/**
 * An exception created by me. Thrown if and only if the PGN trying to be
 * processed is faulty.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class ParsePGNException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1063367444865718112L;

	/**
	 * Instantiates a new parses the PGN exception.
	 *
	 * @param message
	 *            the message
	 */
	public ParsePGNException(final String message) {
		super(message);
	}
}