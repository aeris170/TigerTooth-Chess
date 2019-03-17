package com.chess.pgn;

/**
 * Thrown if and only if the PGN trying to be processed cannot be played.
 * Example case: the next move maker is black while the PGN file start's with a
 * white move. This exception is thrown when the game itself bugs out, has
 * nothing to do with PGN file's validity. PGN file's validity is checked via
 * ParsePGNException
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class PlayPGNException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4962061690352933689L;

	/**
	 * Instantiates a new play PGN exception.
	 *
	 * @param message
	 *            the message
	 */
	public PlayPGNException(final String message) {
		super(message);
	}
}