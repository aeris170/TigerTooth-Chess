package com.chess.engine.classic.player.ai;

import com.chess.engine.classic.board.Board;

/**
 * Documentation will not be provided for this interface for this interface is
 * an A.I. implementation interface.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public interface BoardEvaluator {

	/**
	 * Evaluate.
	 *
	 * @param board
	 *            the board
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	int evaluate(Board board, int depth);
}