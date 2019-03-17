package com.chess.pgn;

import java.util.List;

/**
 * Valid Game
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class ValidGame extends Game {

	/**
	 * Instantiates a new valid game.
	 *
	 * @param tags
	 *            the tags
	 * @param moves
	 *            the moves
	 * @param outcome
	 *            the outcome
	 */
	public ValidGame(final PGNGameTags tags, List<String> moves, final String outcome) {
		super(tags, moves, outcome);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.pgn.Playable#isValid()
	 */
	@Override
	public boolean isValid() {
		return true;
	}
}