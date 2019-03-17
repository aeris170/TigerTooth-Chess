package com.chess.engine.classic.pieces;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.BoardUtils;

import com.google.common.collect.Table;
import com.google.common.collect.ImmutableTable;

/**
 * Utilities for pieces. Used to create possible move-able pieces and getting
 * moved pieces. So, in short helps me create moves and update(not mutate, but
 * build) the board.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
enum PieceUtils {

	/** The ýnstance. */
	INSTANCE;

	/** The all possýble queens. */
	private final Table<Alliance, Integer, Queen> ALL_POSSIBLE_QUEENS = PieceUtils.createAllPossibleMovedQueens();

	/** The all possýble rooks. */
	private final Table<Alliance, Integer, Rook> ALL_POSSIBLE_ROOKS = PieceUtils.createAllPossibleMovedRooks();

	/** The all possýble knýghts. */
	private final Table<Alliance, Integer, Knight> ALL_POSSIBLE_KNIGHTS = PieceUtils.createAllPossibleMovedKnights();

	/** The all possýble býshops. */
	private final Table<Alliance, Integer, Bishop> ALL_POSSIBLE_BISHOPS = PieceUtils.createAllPossibleMovedBishops();

	/** The all possýble pawns. */
	private final Table<Alliance, Integer, Pawn> ALL_POSSIBLE_PAWNS = PieceUtils.createAllPossibleMovedPawns();

	/**
	 * Gets the moved pawn.
	 *
	 * @param alliance
	 *            the alliance
	 * @param destinationCoordinate
	 *            the destination coordinate
	 * @return the moved pawn
	 */
	public Pawn getMovedPawn(final Alliance alliance, final int destinationCoordinate) {
		return ALL_POSSIBLE_PAWNS.get(alliance, destinationCoordinate);
	}

	/**
	 * Gets the moved knight.
	 *
	 * @param alliance
	 *            the alliance
	 * @param destinationCoordinate
	 *            the destination coordinate
	 * @return the moved knight
	 */
	public Knight getMovedKnight(final Alliance alliance, final int destinationCoordinate) {
		return ALL_POSSIBLE_KNIGHTS.get(alliance, destinationCoordinate);
	}

	/**
	 * Gets the moved bishop.
	 *
	 * @param alliance
	 *            the alliance
	 * @param destinationCoordinate
	 *            the destination coordinate
	 * @return the moved bishop
	 */
	public Bishop getMovedBishop(final Alliance alliance, final int destinationCoordinate) {
		return ALL_POSSIBLE_BISHOPS.get(alliance, destinationCoordinate);
	}

	/**
	 * Gets the moved rook.
	 *
	 * @param alliance
	 *            the alliance
	 * @param destinationCoordinate
	 *            the destination coordinate
	 * @return the moved rook
	 */
	public Rook getMovedRook(final Alliance alliance, final int destinationCoordinate) {
		return ALL_POSSIBLE_ROOKS.get(alliance, destinationCoordinate);
	}

	/**
	 * Gets the moved queen.
	 *
	 * @param alliance
	 *            the alliance
	 * @param destinationCoordinate
	 *            the destination coordinate
	 * @return the moved queen
	 */
	public Queen getMovedQueen(final Alliance alliance, final int destinationCoordinate) {
		return ALL_POSSIBLE_QUEENS.get(alliance, destinationCoordinate);
	}

	/**
	 * Creates the all possible moved pawns.
	 *
	 * @return the table
	 */
	private static Table<Alliance, Integer, Pawn> createAllPossibleMovedPawns() {
		final ImmutableTable.Builder<Alliance, Integer, Pawn> pieces = ImmutableTable.builder();
		for (final Alliance alliance : Alliance.values()) {
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				pieces.put(alliance, i, new Pawn(alliance, i, false));
			}
		}
		return pieces.build();
	}

	/**
	 * Creates the all possible moved knights.
	 *
	 * @return the table
	 */
	private static Table<Alliance, Integer, Knight> createAllPossibleMovedKnights() {
		final ImmutableTable.Builder<Alliance, Integer, Knight> pieces = ImmutableTable.builder();
		for (final Alliance alliance : Alliance.values()) {
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				pieces.put(alliance, i, new Knight(alliance, i, false));
			}
		}
		return pieces.build();
	}

	/**
	 * Creates the all possible moved bishops.
	 *
	 * @return the table
	 */
	private static Table<Alliance, Integer, Bishop> createAllPossibleMovedBishops() {
		final ImmutableTable.Builder<Alliance, Integer, Bishop> pieces = ImmutableTable.builder();
		for (final Alliance alliance : Alliance.values()) {
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				pieces.put(alliance, i, new Bishop(alliance, i, false));
			}
		}
		return pieces.build();
	}

	/**
	 * Creates the all possible moved rooks.
	 *
	 * @return the table
	 */
	private static Table<Alliance, Integer, Rook> createAllPossibleMovedRooks() {
		final ImmutableTable.Builder<Alliance, Integer, Rook> pieces = ImmutableTable.builder();
		for (final Alliance alliance : Alliance.values()) {
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				pieces.put(alliance, i, new Rook(alliance, i, false));
			}
		}
		return pieces.build();
	}

	/**
	 * Creates the all possible moved queens.
	 *
	 * @return the table
	 */
	private static Table<Alliance, Integer, Queen> createAllPossibleMovedQueens() {
		final ImmutableTable.Builder<Alliance, Integer, Queen> pieces = ImmutableTable.builder();
		for (final Alliance alliance : Alliance.values()) {
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				pieces.put(alliance, i, new Queen(alliance, i, false));
			}
		}
		return pieces.build();
	}
}