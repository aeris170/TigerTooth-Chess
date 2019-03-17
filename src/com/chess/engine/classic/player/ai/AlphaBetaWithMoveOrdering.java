package com.chess.engine.classic.player.ai;

import java.util.Collection;
import java.util.Comparator;
import java.util.Observable;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.MoveFactory;
import com.chess.engine.classic.board.MoveTransition;

import com.google.common.collect.Ordering;
import com.google.common.collect.ComparisonChain;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class AlphaBetaWithMoveOrdering extends Observable implements MoveStrategy {

	/** The quiescence factor. */
	@SuppressWarnings("unused")
	private final int quiescenceFactor;

	/** The search depth. */
	private final int searchDepth;

	/** The move sorter. */
	private final MoveSorter moveSorter;

	/** The evaluator. */
	private final BoardEvaluator evaluator;

	/** The boards evaluated. */
	private long boardsEvaluated;

	/** The execution time. */
	private long executionTime;

	/** The quiescence count. */
	private int quiescenceCount;

	/** The cut offs produced. */
	private int cutOffsProduced;

	/**
	 * The Enum MoveSorter.
	 */
	private enum MoveSorter {

		/** The sort. */
		SORT {
			@Override
			Collection<Move> sort(final Collection<Move> moves) {
				return Ordering.from(SMART_SORT).immutableSortedCopy(moves);
			}
		};

		/** The smart sort. */
		public static Comparator<Move> SMART_SORT = new Comparator<Move>() {
			@Override
			public int compare(final Move move1, final Move move2) {
				return ComparisonChain.start()
						.compareTrueFirst(BoardUtils.isThreatenedBoardImmediate(move1.getBoard()),
								BoardUtils.isThreatenedBoardImmediate(move2.getBoard()))
						.compareTrueFirst(move1.isAttack(), move2.isAttack())
						.compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
						.compare(move2.getMovedPiece().getPieceValue(), move1.getMovedPiece().getPieceValue()).result();
			}
		};

		/**
		 * Sort.
		 *
		 * @param moves
		 *            the moves
		 * @return the collection
		 */
		abstract Collection<Move> sort(Collection<Move> moves);
	}

	/**
	 * Instantiates a new alpha beta with move ordering.
	 *
	 * @param searchDepth
	 *            the search depth
	 * @param quiescenceFactor
	 *            the quiescence factor
	 */
	public AlphaBetaWithMoveOrdering(final int searchDepth, final int quiescenceFactor) {
		this.evaluator = StandardBoardEvaluator.get();
		this.searchDepth = searchDepth;
		this.quiescenceFactor = quiescenceFactor;
		this.moveSorter = MoveSorter.SORT;
		this.boardsEvaluated = 0;
		this.quiescenceCount = 0;
		this.cutOffsProduced = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AB+MO";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.player.ai.MoveStrategy#getNumBoardsEvaluated()
	 */
	@Override
	public long getNumBoardsEvaluated() {
		return this.boardsEvaluated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.player.ai.MoveStrategy#execute(com.chess.engine.
	 * classic.board.Board)
	 */
	@Override
	public Move execute(final Board board) {
		final long startTime = System.currentTimeMillis();
		final Player currentPlayer = board.currentPlayer();
		final Alliance alliance = currentPlayer.getAlliance();
		Move bestMove = MoveFactory.getNullMove();
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		int moveCounter = 1;
		final int numMoves = this.moveSorter.sort(board.currentPlayer().getLegalMoves()).size();
		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
		System.out.println("\tOrdered moves! : " + this.moveSorter.sort(board.currentPlayer().getLegalMoves()));
		for (final Move move : this.moveSorter.sort(board.currentPlayer().getLegalMoves())) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			this.quiescenceCount = 0;
			final String s;
			if (moveTransition.getMoveStatus().isDone()) {
				final long candidateMoveStartTime = System.nanoTime();
				currentValue = alliance.isWhite()
						? min(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue)
						: max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
				if (alliance.isWhite() && currentValue > highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
					// setChanged();
					// notifyObservers(bestMove);
				} else if (alliance.isBlack() && currentValue < lowestSeenValue) {
					lowestSeenValue = currentValue;
					bestMove = move;
					// setChanged();
					// notifyObservers(bestMove);
				}
				final String quiescenceInfo = " [h: " + highestSeenValue + " l: " + lowestSeenValue + "] q: "
						+ this.quiescenceCount;
				s = "\t" + toString() + "(" + this.searchDepth + "), m: (" + moveCounter + "/" + numMoves + ") " + move
						+ ", best:  " + bestMove + quiescenceInfo + ", t: "
						+ calculateTimeTaken(candidateMoveStartTime, System.nanoTime());
			} else {
				s = "\t" + toString() + ", m: (" + moveCounter + "/" + numMoves + ") " + move + " is illegal, best: "
						+ bestMove;
			}
			System.out.println(s);
			setChanged();
			notifyObservers(s);
			moveCounter++;
		}
		this.executionTime = System.currentTimeMillis() - startTime;
		System.out.printf(
				"%s SELECTS %s [#boards evaluated = %d, time taken = %d ms, eval rate = %.1f cutoffCount = %d prune percent = %.2f\n",
				board.currentPlayer(), bestMove, this.boardsEvaluated, this.executionTime,
				(1000 * ((double) this.boardsEvaluated / this.executionTime)), this.cutOffsProduced,
				100 * ((double) this.cutOffsProduced / this.boardsEvaluated));
		return bestMove;
	}

	/**
	 * Max.
	 *
	 * @param board
	 *            the board
	 * @param depth
	 *            the depth
	 * @param highest
	 *            the highest
	 * @param lowest
	 *            the lowest
	 * @return the int
	 */
	public int max(final Board board, final int depth, final int highest, final int lowest) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.boardsEvaluated++;
			return this.evaluator.evaluate(board, depth);
		}
		int currentHighest = highest;
		for (final Move move : this.moveSorter.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				currentHighest = Math.max(currentHighest, min(moveTransition.getToBoard(),
						calculateQuiescenceDepth(board, move, depth), currentHighest, lowest));
				if (lowest <= currentHighest) {
					this.cutOffsProduced++;
					break;
				}
			}
		}
		return currentHighest;
	}

	/**
	 * Min.
	 *
	 * @param board
	 *            the board
	 * @param depth
	 *            the depth
	 * @param highest
	 *            the highest
	 * @param lowest
	 *            the lowest
	 * @return the int
	 */
	public int min(final Board board, final int depth, final int highest, final int lowest) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.boardsEvaluated++;
			return this.evaluator.evaluate(board, depth);
		}
		int currentLowest = lowest;
		for (final Move move : this.moveSorter.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				currentLowest = Math.min(currentLowest, max(moveTransition.getToBoard(),
						calculateQuiescenceDepth(board, move, depth), highest, currentLowest));
				if (currentLowest <= highest) {
					this.cutOffsProduced++;
					break;
				}
			}
		}
		return currentLowest;
	}

	/**
	 * Calculate quiescence depth.
	 *
	 * @param board
	 *            the board
	 * @param move
	 *            the move
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	private int calculateQuiescenceDepth(final Board board, final Move move, final int depth) {
		return depth - 1;
	}

	/**
	 * Calculate time taken.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the string
	 */
	private static String calculateTimeTaken(final long start, final long end) {
		final long timeTaken = (end - start) / 1000000;
		return timeTaken + " ms";
	}
}