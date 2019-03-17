package com.chess.engine.classic.player.ai;

import java.util.List;
import java.util.ArrayList;

import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.BoardUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class RookStructureAnalyzer {

	/** The Constant INSTANCE. */
	private static final RookStructureAnalyzer INSTANCE = new RookStructureAnalyzer();

	/** The Constant BOARD_COLUMNS. */
	private static final List<List<Boolean>> BOARD_COLUMNS = initColumns();

	/** The Constant OPEN_COLUMN_ROOK_BONUS. */
	private static final int OPEN_COLUMN_ROOK_BONUS = 25;

	/** The Constant NO_BONUS. */
	private static final int NO_BONUS = 0;

	/**
	 * Instantiates a new rook structure analyzer.
	 */
	private RookStructureAnalyzer() {
	}

	/**
	 * Gets the.
	 *
	 * @return the rook structure analyzer
	 */
	public static RookStructureAnalyzer get() {
		return INSTANCE;
	}

	/**
	 * Inits the columns.
	 *
	 * @return the list
	 */
	private static List<List<Boolean>> initColumns() {
		final List<List<Boolean>> columns = new ArrayList<>();
		columns.add(BoardUtils.INSTANCE.FIRST_COLUMN);
		columns.add(BoardUtils.INSTANCE.SECOND_COLUMN);
		columns.add(BoardUtils.INSTANCE.THIRD_COLUMN);
		columns.add(BoardUtils.INSTANCE.FOURTH_COLUMN);
		columns.add(BoardUtils.INSTANCE.FIFTH_COLUMN);
		columns.add(BoardUtils.INSTANCE.SIXTH_COLUMN);
		columns.add(BoardUtils.INSTANCE.SEVENTH_COLUMN);
		columns.add(BoardUtils.INSTANCE.EIGHTH_COLUMN);
		return ImmutableList.copyOf(columns);
	}

	/**
	 * Rook structure score.
	 *
	 * @param board
	 *            the board
	 * @param player
	 *            the player
	 * @return the int
	 */
	public int rookStructureScore(final Board board, final Player player) {
		final List<Integer> rookLocations = calculateRookLocations(player);
		return calculateOpenFileRookBonus(board, rookLocations);
	}

	/**
	 * Calculate rook locations.
	 *
	 * @param player
	 *            the player
	 * @return the list
	 */
	private static List<Integer> calculateRookLocations(final Player player) {
		final Builder<Integer> playerRookLocations = new Builder<>();
		for (final Piece piece : player.getActivePieces()) {
			if (piece.getPieceType().isRook()) {
				playerRookLocations.add(piece.getPiecePosition());
			}
		}
		return playerRookLocations.build();
	}

	/**
	 * Calculate open file rook bonus.
	 *
	 * @param board
	 *            the board
	 * @param rookLocations
	 *            the rook locations
	 * @return the int
	 */
	private static int calculateOpenFileRookBonus(final Board board, final List<Integer> rookLocations) {
		int bonus = NO_BONUS;
		for (final Integer rookLocation : rookLocations) {
			final int[] piecesOnColumn = createPiecesOnColumnTable(board);
			final int rookColumn = rookLocation / 8;
			for (int i = 0; i < piecesOnColumn.length; i++) {
				if (piecesOnColumn[i] == 1 && i == rookColumn) {
					bonus += OPEN_COLUMN_ROOK_BONUS;
				}
			}
		}
		return bonus;
	}

	/**
	 * Creates the pieces on column table.
	 *
	 * @param board
	 *            the board
	 * @return the int[]
	 */
	private static int[] createPiecesOnColumnTable(final Board board) {
		final int[] piecesOnColumnTable = new int[BOARD_COLUMNS.size()];
		for (final Piece piece : board.getAllPieces()) {
			for (int i = 0; i < BOARD_COLUMNS.size(); i++) {
				if (BOARD_COLUMNS.get(i).get(piece.getPiecePosition())) {
					piecesOnColumnTable[i]++;
				}
			}
		}
		return piecesOnColumnTable;
	}
}