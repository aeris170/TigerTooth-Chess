package com.chess.pgn;

import java.util.List;

/**
 * An abstract(non-existing, not actually abstract) game. This object got
 * constructed when the user wants to save the game in a PGN file.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public abstract class Game implements Playable {

	/** The tags. */
	protected final PGNGameTags tags;

	/** The moves. */
	protected final List<String> moves;

	/** The winner. */
	protected final String winner;

	/**
	 * Instantiates a new game.
	 *
	 * @param tags
	 *            the tags
	 * @param moves
	 *            the moves
	 * @param outcome
	 *            the outcome
	 */
	Game(final PGNGameTags tags, final List<String> moves, final String outcome) {
		this.tags = tags;
		this.moves = moves;
		this.winner = calculateWinner(outcome);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.tags.toString();
	}

	/**
	 * Gets the moves.
	 *
	 * @return the moves
	 */
	public List<String> getMoves() {
		return this.moves;
	}

	/**
	 * Gets the winner.
	 *
	 * @return the winner
	 */
	public String getWinner() {
		return this.winner;
	}

	/**
	 * Calculate winner.
	 *
	 * @param gameOutcome
	 *            the game outcome
	 * @return the string
	 */
	private static String calculateWinner(final String gameOutcome) {
		if (gameOutcome.equals("1-0")) {
			return "White";
		}
		if (gameOutcome.equals("0-1")) {
			return "Black";
		}
		if (gameOutcome.equals("1/2-1/2")) {
			return "Tie";
		}
		return "None";
	}
}