package com.chess.engine.classic.board;

import com.chess.engine.classic.board.Move.MoveStatus;

/**
 * A chess move transition. Used mainly to make moves between boards. This class
 * is essential since all the Board objects are immutable
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class MoveTransition {

	/** The from board. */
	private final Board fromBoard;

	/** The to board. */
	private final Board toBoard;

	/** The transition move. */
	private final Move transitionMove;

	/** The move status. */
	private final MoveStatus moveStatus;

	/**
	 * Instantiates a new move transition.
	 *
	 * @param fromBoard
	 *            the from board
	 * @param toBoard
	 *            the to board
	 * @param transitionMove
	 *            the transition move
	 * @param moveStatus
	 *            the move status
	 */
	public MoveTransition(final Board fromBoard, final Board toBoard, final Move transitionMove,
			final MoveStatus moveStatus) {
		this.fromBoard = fromBoard;
		this.toBoard = toBoard;
		this.transitionMove = transitionMove;
		this.moveStatus = moveStatus;
	}

	/**
	 * Gets the from board.
	 *
	 * @return the from board
	 */
	public Board getFromBoard() {
		return this.fromBoard;
	}

	/**
	 * Gets the to board.
	 *
	 * @return the to board
	 */
	public Board getToBoard() {
		return this.toBoard;
	}

	/**
	 * Gets the transition move.
	 *
	 * @return the transition move
	 */
	public Move getTransitionMove() {
		return this.transitionMove;
	}

	/**
	 * Gets the move status.
	 *
	 * @return the move status
	 */
	public MoveStatus getMoveStatus() {
		return this.moveStatus;
	}
}