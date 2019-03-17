package com.chess.pgn;

import java.util.Collections;

/**
 * Invalid game.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class InvalidGame extends Game {

	/** The malformed game text. */
	final String malformedGameText;

	/**
	 * Instantiates a new ýnvalid game.
	 *
	 * @param tags
	 *            the tags
	 * @param malformedGameText
	 *            the malformed game text
	 * @param outcome
	 *            the outcome
	 */
	public InvalidGame(final PGNGameTags tags, final String malformedGameText, final String outcome) {
		super(tags, Collections.<String>emptyList(), outcome);
		this.malformedGameText = malformedGameText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.pgn.Game#toString()
	 */
	@Override
	public String toString() {
		return "Invalid Game " + this.tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.pgn.Playable#isValid()
	 */
	@Override
	public boolean isValid() {
		return false;
	}
}