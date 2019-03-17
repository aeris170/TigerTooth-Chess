package com.chess.engine.classic.player.ai;

import static com.chess.engine.classic.board.Move.MoveFactory;

import java.util.concurrent.atomic.AtomicLong;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.MoveTransition;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class MiniMax implements MoveStrategy {

	/** The evaluator. */
	private final BoardEvaluator evaluator;

	/** The search depth. */
	private final int searchDepth;

	/** The boards evaluated. */
	private long boardsEvaluated;

	/** The execution time. */
	private long executionTime;

	/** The freq table. */
	private FreqTableRow[] freqTable;

	/** The freq table ýndex. */
	private int freqTableIndex;

	/**
	 * Instantiates a new mini max.
	 *
	 * @param searchDepth
	 *            the search depth
	 */
	public MiniMax(final int searchDepth) {
		this.evaluator = StandardBoardEvaluator.get();
		this.boardsEvaluated = 0;
		this.searchDepth = searchDepth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MiniMax";
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
	public Move execute(final Board board) {
		final long startTime = System.currentTimeMillis();
		Move bestMove = MoveFactory.getNullMove();
		int highestSeenValue = Integer.MIN_VALUE;
		int lowestSeenValue = Integer.MAX_VALUE;
		int currentValue;
		System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
		this.freqTable = new FreqTableRow[board.currentPlayer().getLegalMoves().size()];
		this.freqTableIndex = 0;
		int moveCounter = 1;
		final int numMoves = board.currentPlayer().getLegalMoves().size();
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
			if (moveTransition.getMoveStatus().isDone()) {
				final FreqTableRow row = new FreqTableRow(move);
				this.freqTable[this.freqTableIndex] = row;
				currentValue = board.currentPlayer().getAlliance().isWhite()
						? min(moveTransition.getToBoard(), this.searchDepth - 1)
						: max(moveTransition.getToBoard(), this.searchDepth - 1);
				System.out.println("\t" + toString() + " analyzing move (" + moveCounter + "/" + numMoves + ") " + move
						+ " scores " + currentValue + " " + this.freqTable[this.freqTableIndex]);
				this.freqTableIndex++;
				if (board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue) {
					highestSeenValue = currentValue;
					bestMove = move;
				} else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
					lowestSeenValue = currentValue;
					bestMove = move;
				}
			} else {
				System.out.println(
						"\t" + toString() + " can't execute move (" + moveCounter + "/" + numMoves + ") " + move);
			}
			moveCounter++;
		}

		this.executionTime = System.currentTimeMillis() - startTime;
		System.out.printf("%s SELECTS %s [#boards = %d time taken = %d ms, rate = %.1f\n", board.currentPlayer(),
				bestMove, this.boardsEvaluated, this.executionTime,
				(1000 * ((double) this.boardsEvaluated / this.executionTime)));
		long total = 0;
		for (final FreqTableRow row : this.freqTable) {
			if (row != null) {
				total += row.getCount();
			}
		}
		if (this.boardsEvaluated != total) {
			System.out.println("somethings wrong with the # of boards evaluated!");
		}
		return bestMove;
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
		if (depth == 0) {
			this.boardsEvaluated++;
			this.freqTable[this.freqTableIndex].increment();
			return this.evaluator.evaluate(board, depth);
		}
		if (isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int lowestSeenValue = Integer.MAX_VALUE;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
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
		if (depth == 0) {
			this.boardsEvaluated++;
			this.freqTable[this.freqTableIndex].increment();
			return this.evaluator.evaluate(board, depth);
		}
		if (isEndGameScenario(board)) {
			return this.evaluator.evaluate(board, depth);
		}
		int highestSeenValue = Integer.MIN_VALUE;
		for (final Move move : board.currentPlayer().getLegalMoves()) {
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

	/**
	 * The Class FreqTableRow.
	 */
	private static class FreqTableRow {

		/** The move. */
		private final Move move;

		/** The count. */
		private final AtomicLong count;

		/**
		 * Instantiates a new freq table row.
		 *
		 * @param move
		 *            the move
		 */
		FreqTableRow(final Move move) {
			this.count = new AtomicLong();
			this.move = move;
		}

		/**
		 * Gets the count.
		 *
		 * @return the count
		 */
		public long getCount() {
			return this.count.get();
		}

		/**
		 * Ýncrement.
		 */
		public void increment() {
			this.count.incrementAndGet();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getCurrentCoordinate())
					+ BoardUtils.INSTANCE.getPositionAtCoordinate(this.move.getDestinationCoordinate()) + " : "
					+ this.count;
		}
	}
}