package com.chess.engine.classic.player.ai;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Collections;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.MoveTransition;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Booleans;
import com.google.common.collect.ImmutableList;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class MoveOrdering {

	/** The evaluator. */
	private final BoardEvaluator evaluator;

	/** The Constant INSTANCE. */
	private static final MoveOrdering INSTANCE = new MoveOrdering();

	/** The Constant ORDER_SEARCH_DEPTH. */
	private static final int ORDER_SEARCH_DEPTH = 2;

	/**
	 * Instantiates a new move ordering.
	 */
	private MoveOrdering() {
		this.evaluator = StandardBoardEvaluator.get();
	}

	/**
	 * Gets the.
	 *
	 * @return the move ordering
	 */
	public static MoveOrdering get() {
		return INSTANCE;
	}

	/**
	 * Order moves.
	 *
	 * @param board
	 *            the board
	 * @return the list
	 */
	public List<Move> orderMoves(final Board board) {
		return orderImpl(board, ORDER_SEARCH_DEPTH);
	}

	/**
	 * The Class MoveOrderEntry.
	 */
	private static class MoveOrderEntry {

		/** The move. */
		final Move move;

		/** The score. */
		final int score;

		/**
		 * Instantiates a new move order entry.
		 *
		 * @param move
		 *            the move
		 * @param score
		 *            the score
		 */
		MoveOrderEntry(final Move move, final int score) {
			this.move = move;
			this.score = score;
		}

		/**
		 * Gets the move.
		 *
		 * @return the move
		 */
		final Move getMove() {
			return this.move;
		}

		/**
		 * Gets the score.
		 *
		 * @return the score
		 */
		final int getScore() {
			return this.score;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "move = " + this.move + " score = " + this.score;
		}
	}

	/**
	 * Order ýmpl.
	 *
	 * @param board
	 *            the board
	 * @param depth
	 *            the depth
	 * @return the list
	 */
	private List<Move> orderImpl(final Board board, final int depth) {
		final List<MoveOrderEntry> moveOrderEntries = new ArrayList<>();
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final int attackBonus = calculateAttackBonus(board.currentPlayer(), move);
				final int currentValue = attackBonus + (board.currentPlayer().getAlliance().isWhite()
						? min(moveTransition.getToBoard(), depth - 1) : max(moveTransition.getToBoard(), depth - 1));
				moveOrderEntries.add(new MoveOrderEntry(move, currentValue));
			}
		}
		Collections.sort(moveOrderEntries, new Comparator<MoveOrderEntry>() {
			@Override
			public int compare(final MoveOrderEntry o1, final MoveOrderEntry o2) {
				return Ints.compare(o2.getScore(), o1.getScore());
			}
		});
		final List<Move> orderedMoves = new ArrayList<>();
		for (final MoveOrderEntry entry : moveOrderEntries) {
			orderedMoves.add(entry.getMove());
		}
		return ImmutableList.copyOf(orderedMoves);
	}

	/**
	 * Calculate attack bonus.
	 *
	 * @param player
	 *            the player
	 * @param move
	 *            the move
	 * @return the int
	 */
	private int calculateAttackBonus(final Player player, final Move move) {
		final int attackBonus = move.isAttack() ? 1000 : 0;
		return attackBonus * (player.getAlliance().isWhite() ? 1 : -1);
	}

	/**
	 * Calculate simple move order.
	 *
	 * @param moves
	 *            the moves
	 * @return the collection
	 */
	private static Collection<Move> calculateSimpleMoveOrder(final Collection<Move> moves) {
		final List<Move> sortedMoves = new ArrayList<>();
		sortedMoves.addAll(moves);
		Collections.sort(sortedMoves, new Comparator<Move>() {
			@Override
			public int compare(final Move m1, final Move m2) {
				return Booleans.compare(m2.isAttack(), m1.isAttack());
			}
		});
		return sortedMoves;
	}

	/**
	 * Min.
	 *
	 * @param board
	 *            the board
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	public int min(final Board board, final int depth) {
		if (depth == 0 || isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int lowestSeenValue = Integer.MAX_VALUE;
		for (final Move move : calculateSimpleMoveOrder(board.currentPlayer().getLegalMoves())) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final int currentValue = max(moveTransition.getToBoard(), depth - 1);
				if (currentValue <= lowestSeenValue) {
					lowestSeenValue = currentValue;
				}
			}
		}
		return lowestSeenValue;
	}

	/**
	 * Max.
	 *
	 * @param board
	 *            the board
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	public int max(final Board board, final int depth) {
		if (depth == 0 || isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int highestSeenValue = Integer.MIN_VALUE;
		for (final Move move : calculateSimpleMoveOrder(board.currentPlayer().getLegalMoves())) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final int currentValue = min(moveTransition.getToBoard(), depth - 1);
				if (currentValue >= highestSeenValue) {
					highestSeenValue = currentValue;
				}
			}
		}
		return highestSeenValue;
	}

	/**
	 * Checks if is end game scenario.
	 *
	 * @param board
	 *            the board
	 * @return true, if is end game scenario
	 */
	private static boolean isEndGameScenario(final Board board) {
		return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
	}
}