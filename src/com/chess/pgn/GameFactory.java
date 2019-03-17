package com.chess.pgn;

/**
 * Creates a "Game" object(see com.chess.pgn package) using Factory pattern
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class GameFactory {

	/**
	 * Creates a new Game object.
	 *
	 * @param tags
	 *            the tags
	 * @param gameText
	 *            the game text
	 * @param outcome
	 *            the outcome
	 * @return the game
	 */
	public static Game createGame(final PGNGameTags tags, final String gameText, final String outcome) {
		try {
			return new ValidGame(tags, PGNUtilities.processMoveText(gameText), outcome);
		} catch (final ParsePGNException e) {
			return new InvalidGame(tags, gameText, outcome);
		}
	}
}