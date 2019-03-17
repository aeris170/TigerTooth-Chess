package com.chess.engine.classic.player.ai;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;

/**
 * Documentation will not be provided for this interface for this interface is
 * an A.I. implementation interface.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public interface MoveStrategy {

	/**
	 * Gets the num boards evaluated.
	 *
	 * @return the num boards evaluated
	 */
	long getNumBoardsEvaluated();

	/**
	 * Execute.
	 *
	 * @param board
	 *            the board
	 * @return the move
	 */
	Move execute(Board board);
}