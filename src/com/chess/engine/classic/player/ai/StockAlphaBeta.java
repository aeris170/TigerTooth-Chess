package com.chess.engine.classic.player.ai;

import static com.chess.engine.classic.board.Move.MoveFactory;
import static com.chess.engine.classic.board.BoardUtils.mvvlva;

import java.util.Collection;
import java.util.Comparator;
import java.util.Observable;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.MoveTransition;

import com.google.common.collect.Ordering;
import com.google.common.collect.ComparisonChain;

/**
 * StockFish.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class StockAlphaBeta extends Observable implements MoveStrategy {

	/** The evaluator. */
	private final BoardEvaluator evaluator;

	/** The search depth. */
	private final int searchDepth;

	/** The boards evaluated. */
	private long boardsEvaluated;

	/** The execution time. */
	private long executionTime;

	/** The quiescence count. */
	private int quiescenceCount;

	/** The Constant MAX_QUIESCENCE. */
	private static final int MAX_QUIESCENCE = 5000;

	/**
	 * The Enum MoveSorter.
	 */
	private enum MoveSorter {

		/** The standard. */
		STANDARD {
			@Override
			Collection<Move> sort(final Collection<Move> moves) {
				return Ordering.from(new Comparator<Move>() {
					@Override
					public int compare(final Move move1, final Move move2) {
						return ComparisonChain.start().compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
								.compare(mvvlva(move2), mvvlva(move1)).result();
					}
				}).immutableSortedCopy(moves);
			}
		},

		/** The expensýve. */
		EXPENSIVE {
			@Override
			Collection<Move> sort(final Collection<Move> moves) {
				return Ordering.from(new Comparator<Move>() {
					@Override
					public int compare(final Move move1, final Move move2) {
						return ComparisonChain.start()
								.compareTrueFirst(BoardUtils.kingThreat(move1), BoardUtils.kingThreat(move2))
								.compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
								.compare(mvvlva(move2), mvvlva(move1)).result();
					}
				}).immutableSortedCopy(moves);
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
	 * Instantiates a new stock alpha beta.
	 *
	 * @param searchDepth
	 *            the search depth
	 */
	public StockAlphaBeta(final int searchDepth) {
		this.evaluator = StandardBoardEvaluator.get();
		this.searchDepth = searchDepth;
		this.boardsEvaluated = 0;
		this.quiescenceCount = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StockAlphaBeta";
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
		Move bestMove = MoveFactory.getNullMove();
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
		int moveCounter = 1;
		int numMoves = board.currentPlayer().getLegalMoves().size();
		for (final Move move : MoveSorter.EXPENSIVE.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			this.quiescenceCount = 0;
			final String s;
			if (moveTransition.getMoveStatus().isDone()) {
				final long candidateMoveStartTime = System.nanoTime();
				currentValue = currentPlayer.getAlliance().isWhite()
						? min(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue)
						: max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
				if (currentPlayer.getAlliance().isWhite() && currentValue > highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
					if (moveTransition.getToBoard().blackPlayer().isInCheckMate()) {
						break;
					}
				} else if (currentPlayer.getAlliance().isBlack() && currentValue < lowestSeenValue) {
					lowestSeenValue = currentValue;
					bestMove = move;
					if (moveTransition.getToBoard().whitePlayer().isInCheckMate()) {
						break;
					}
				}
				final String quiescenceInfo = " " + score(currentPlayer, highestSeenValue, lowestSeenValue) + " q: "
						+ this.quiescenceCount;
				s = "\t" + toString() + "(" + this.searchDepth + "), m: (" + moveCounter + "/" + numMoves + ") " + move
						+ ", best:  " + bestMove + quiescenceInfo + ", t: "
						+ calculateTimeTaken(candidateMoveStartTime, System.nanoTime());
			} else {
				s = "\t" + toString() + ", m: (" + moveCounter + "/" + numMoves + ") " + move + " is illegal! best: "
						+ bestMove;
			}
			System.out.println(s);
			setChanged();
			notifyObservers(s);
			moveCounter++;
		}
		this.executionTime = System.currentTimeMillis() - startTime;
		System.out.printf("%s SELECTS %s [#boards evaluated = %d, time taken = %d ms, rate = %.1f\n",
				board.currentPlayer(), bestMove, this.boardsEvaluated, this.executionTime,
				(1000 * ((double) this.boardsEvaluated / this.executionTime)));
		return bestMove;
	}

	/**
	 * Score.
	 *
	 * @param currentPlayer
	 *            the current player
	 * @param highestSeenValue
	 *            the highest seen value
	 * @param lowestSeenValue
	 *            the lowest seen value
	 * @return the string
	 */
	private static String score(final Player currentPlayer, final int highestSeenValue, final int lowestSeenValue) {
		if (currentPlayer.getAlliance().isWhite()) {
			return "[score: " + highestSeenValue + "]";
		} else if (currentPlayer.getAlliance().isBlack()) {
			return "[score: " + lowestSeenValue + "]";
		}
		throw new RuntimeException("olmadý guzel kardesim...");
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
	private int max(final Board board, final int depth, final int highest, final int lowest) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.boardsEvaluated++;
			return this.evaluator.evaluate(board, depth);
		}
		int currentHighest = highest;
		for (final Move move : MoveSorter.STANDARD.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				currentHighest = Math.max(currentHighest, min(moveTransition.getToBoard(),
						calculateQuiescenceDepth(moveTransition, depth), currentHighest, lowest));
				if (currentHighest >= lowest) {
					return lowest;
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
	private int min(final Board board, final int depth, final int highest, final int lowest) {
		if (depth == 0 || BoardUtils.isEndGame(board)) {
			this.boardsEvaluated++;
			return this.evaluator.evaluate(board, depth);
		}
		int currentLowest = lowest;
		for (final Move move : MoveSorter.STANDARD.sort((board.currentPlayer().getLegalMoves()))) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				currentLowest = Math.min(currentLowest, max(moveTransition.getToBoard(),
						calculateQuiescenceDepth(moveTransition, depth), highest, currentLowest));
				if (currentLowest <= highest) {
					return highest;
				}
			}
		}
		return currentLowest;
	}

	/**
	 * Calculate quiescence depth.
	 *
	 * @param moveTransition
	 *            the move transition
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	private int calculateQuiescenceDepth(final MoveTransition moveTransition, final int depth) {
		if (depth == 1 && this.quiescenceCount < MAX_QUIESCENCE) {
			int activityMeasure = 0;
			if (moveTransition.getToBoard().currentPlayer().isInCheck()) {
				activityMeasure += 2;
			}
			for (final Move move : BoardUtils.lastNMoves(moveTransition.getToBoard(), 4)) {
				if (move.isAttack()) {
					activityMeasure += 1;
				}
			}
			if (activityMeasure > 3) {
				this.quiescenceCount++;
				return 2;
			}
		}
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