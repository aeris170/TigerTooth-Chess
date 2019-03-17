package com.chess.engine.classic.board;

import static com.chess.engine.classic.board.Move.MoveFactory;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import com.chess.engine.classic.pieces.King;
import com.chess.engine.classic.pieces.Piece;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;

/**
 * Utilities for the chess board, used mainly for initializing rows, columns and
 * determining king's possible moves etc. etc
 * 
 * @author Do�a Oru�
 * @version 06.08.2017
 */
public enum BoardUtils {

	/** The �nstance. */
	INSTANCE;

	/** The f�rst column. */
	public final List<Boolean> FIRST_COLUMN = initColumn(0);

	/** The second column. */
	public final List<Boolean> SECOND_COLUMN = initColumn(1);

	/** The th�rd column. */
	public final List<Boolean> THIRD_COLUMN = initColumn(2);

	/** The fourth column. */
	public final List<Boolean> FOURTH_COLUMN = initColumn(3);

	/** The f�fth column. */
	public final List<Boolean> FIFTH_COLUMN = initColumn(4);

	/** The s�xth column. */
	public final List<Boolean> SIXTH_COLUMN = initColumn(5);

	/** The seventh column. */
	public final List<Boolean> SEVENTH_COLUMN = initColumn(6);

	/** The e�ghth column. */
	public final List<Boolean> EIGHTH_COLUMN = initColumn(7);

	/** The f�rst row. */
	public final List<Boolean> FIRST_ROW = initRow(0);

	/** The second row. */
	public final List<Boolean> SECOND_ROW = initRow(8);

	/** The th�rd row. */
	public final List<Boolean> THIRD_ROW = initRow(16);

	/** The fourth row. */
	public final List<Boolean> FOURTH_ROW = initRow(24);

	/** The f�fth row. */
	public final List<Boolean> FIFTH_ROW = initRow(32);

	/** The s�xth row. */
	public final List<Boolean> SIXTH_ROW = initRow(40);

	/** The seventh row. */
	public final List<Boolean> SEVENTH_ROW = initRow(48);

	/** The e�ghth row. */
	public final List<Boolean> EIGHTH_ROW = initRow(56);

	/** The algebra�c notat�on. */
	public final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();

	/** The pos�t�on to coord�nate. */
	public final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

	/** The Constant START_TILE_INDEX. */
	public static final int START_TILE_INDEX = 0;

	/** The Constant NUM_TILES_PER_ROW. */
	public static final int NUM_TILES_PER_ROW = 8;

	/** The Constant NUM_TILES. */
	public static final int NUM_TILES = 64;

	/**
	 * Inits the column.
	 *
	 * @param columnNumber
	 *            the column number
	 * @return the list
	 */
	private static List<Boolean> initColumn(int columnNumber) {
		final Boolean[] column = new Boolean[NUM_TILES];
		for (int i = 0; i < column.length; i++) {
			column[i] = false;
		}
		do {
			column[columnNumber] = true;
			columnNumber += NUM_TILES_PER_ROW;
		} while (columnNumber < NUM_TILES);
		return ImmutableList.copyOf(column);
	}

	/**
	 * Inits the row.
	 *
	 * @param rowNumber
	 *            the row number
	 * @return the list
	 */
	private static List<Boolean> initRow(int rowNumber) {
		final Boolean[] row = new Boolean[NUM_TILES];
		for (int i = 0; i < row.length; i++) {
			row[i] = false;
		}
		do {
			row[rowNumber] = true;
			rowNumber++;
		} while (rowNumber % NUM_TILES_PER_ROW != 0);
		return ImmutableList.copyOf(row);
	}

	/**
	 * �nitialize position to coordinate map.
	 *
	 * @return the map
	 */
	private Map<String, Integer> initializePositionToCoordinateMap() {
		final Map<String, Integer> positionToCoordinate = new HashMap<>();
		for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
			positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
		}
		return ImmutableMap.copyOf(positionToCoordinate);
	}

	/**
	 * �nitialize algebraic notation.
	 *
	 * @return the list
	 */
	private static List<String> initializeAlgebraicNotation() {
		return ImmutableList.copyOf(new String[] { "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", "a7", "b7", "c7",
				"d7", "e7", "f7", "g7", "h7", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a5", "b5", "c5", "d5",
				"e5", "f5", "g5", "h5", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a3", "b3", "c3", "d3", "e3",
				"f3", "g3", "h3", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a1", "b1", "c1", "d1", "e1", "f1",
				"g1", "h1" });
	}

	/**
	 * Checks if is valid tile coordinate.
	 *
	 * @param coordinate
	 *            the coordinate
	 * @return true, if is valid tile coordinate
	 */
	public static boolean isValidTileCoordinate(final int coordinate) {
		return coordinate >= START_TILE_INDEX && coordinate < NUM_TILES;
	}

	/**
	 * Gets the coordinate at position.
	 *
	 * @param position
	 *            the position
	 * @return the coordinate at position
	 */
	public int getCoordinateAtPosition(final String position) {
		return POSITION_TO_COORDINATE.get(position);
	}

	/**
	 * Gets the position at coordinate.
	 *
	 * @param coordinate
	 *            the coordinate
	 * @return the position at coordinate
	 */
	public String getPositionAtCoordinate(final int coordinate) {
		return ALGEBRAIC_NOTATION.get(coordinate);
	}

	/**
	 * Checks if is threatened board �mmediate.
	 *
	 * @param board
	 *            the board
	 * @return true, if is threatened board �mmediate
	 */
	public static boolean isThreatenedBoardImmediate(final Board board) {
		return board.whitePlayer().isInCheck() || board.blackPlayer().isInCheck();
	}

	/**
	 * King threat.
	 *
	 * @param move
	 *            the move
	 * @return true, if successful
	 */
	public static boolean kingThreat(final Move move) {
		final Board board = move.getBoard();
		MoveTransition transition = board.currentPlayer().makeMove(move);
		return transition.getToBoard().currentPlayer().isInCheck();
	}

	/**
	 * Checks if is king pawn trap.
	 *
	 * @param board
	 *            the board
	 * @param king
	 *            the king
	 * @param frontTile
	 *            the front tile
	 * @return true, if is king pawn trap
	 */
	public static boolean isKingPawnTrap(final Board board, final King king, final int frontTile) {
		final Tile tile = board.getTile(frontTile);
		if (tile.isTileOccupied()) {
			final Piece piece = tile.getPiece();
			if (piece.getPieceType().isPawn() && piece.getPieceAllegiance() != king.getPieceAllegiance()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Mvvlva.
	 *
	 * @param move
	 *            the move
	 * @return the int
	 */
	public static int mvvlva(final Move move) {
		final Piece movingPiece = move.getMovedPiece();
		if (move.isAttack()) {
			final Piece attackedPiece = move.getAttackedPiece();
			return (attackedPiece.getPieceValue() - movingPiece.getPieceValue() + Piece.PieceType.KING.getPieceValue())
					* 100;
		}
		return Piece.PieceType.KING.getPieceValue() - movingPiece.getPieceValue();
	}

	/**
	 * Last N moves.
	 *
	 * @param board
	 *            the board
	 * @param N
	 *            the n
	 * @return the list
	 */
	public static List<Move> lastNMoves(final Board board, int N) {
		final List<Move> moveHistory = new ArrayList<>();
		Move currentMove = board.getTransitionMove();
		int i = 0;
		while (currentMove != MoveFactory.getNullMove() && i < N) {
			moveHistory.add(currentMove);
			currentMove = currentMove.getBoard().getTransitionMove();
			i++;
		}
		return ImmutableList.copyOf(moveHistory);
	}

	/**
	 * Checks if is end game.
	 *
	 * @param board
	 *            the board
	 * @return true, if is end game
	 */
	public static boolean isEndGame(final Board board) {
		return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
	}
}