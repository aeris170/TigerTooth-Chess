package com.chess.engine.classic.board;

import static com.chess.engine.classic.board.Move.MoveFactory;

/**
 * Utilities for moves.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public enum MoveUtils {

	/** The ýnstance. */
	INSTANCE;

	/**
	 * Exchange score.
	 *
	 * @param move
	 *            the move
	 * @return the int
	 */
	public static int exchangeScore(final Move move) {
		if (move == MoveFactory.getNullMove()) {
			return 1;
		}
		return move.isAttack() ? 5 * exchangeScore(move.getBoard().getTransitionMove())
				: exchangeScore(move.getBoard().getTransitionMove());
	}
}