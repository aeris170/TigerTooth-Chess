package com.chess.engine.classic;

import java.io.Serializable;

import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.player.WhitePlayer;
import com.chess.engine.classic.player.BlackPlayer;

/**
 * Player alliance, in chess we have 2 alliances. Those being WHITE and BLACK
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public enum Alliance implements Serializable {

	/** The whýte. */
	WHITE() {

		@Override
		public boolean isWhite() {
			return true;
		}

		@Override
		public boolean isBlack() {
			return false;
		}

		@Override
		public int getDirection() {
			return UP_DIRECTION;
		}

		@Override
		public int getOppositeDirection() {
			return DOWN_DIRECTION;
		}

		@Override
		public boolean isPawnPromotionSquare(final int position) {
			return BoardUtils.INSTANCE.FIRST_ROW.get(position);
		}

		@Override
		public Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return whitePlayer;
		}

		@Override
		public String toString() {
			return "White";
		}

		@Override
		public int pawnBonus(final int position) {
			return WHITE_PAWN_PREFERRED_COORDINATES[position];
		}

		@Override
		public int knightBonus(final int position) {
			return WHITE_KNIGHT_PREFERRED_COORDINATES[position];
		}

		@Override
		public int bishopBonus(final int position) {
			return WHITE_BISHOP_PREFERRED_COORDINATES[position];
		}

		@Override
		public int rookBonus(final int position) {
			return WHITE_ROOK_PREFERRED_COORDINATES[position];
		}

		@Override
		public int queenBonus(final int position) {
			return WHITE_QUEEN_PREFERRED_COORDINATES[position];
		}

		@Override
		public int kingBonus(final int position) {
			return WHITE_KING_PREFERRED_COORDINATES[position];
		}

	},

	/** The black. */
	BLACK() {

		@Override
		public boolean isWhite() {
			return false;
		}

		@Override
		public boolean isBlack() {
			return true;
		}

		@Override
		public int getDirection() {
			return DOWN_DIRECTION;
		}

		@Override
		public int getOppositeDirection() {
			return UP_DIRECTION;
		}

		@Override
		public boolean isPawnPromotionSquare(final int position) {
			return BoardUtils.INSTANCE.EIGHTH_ROW.get(position);
		}

		@Override
		public Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
			return blackPlayer;
		}

		@Override
		public String toString() {
			return "Black";
		}

		@Override
		public int pawnBonus(final int position) {
			return BLACK_PAWN_PREFERRED_COORDINATES[position];
		}

		@Override
		public int knightBonus(final int position) {
			return BLACK_KNIGHT_PREFERRED_COORDINATES[position];
		}

		@Override
		public int bishopBonus(final int position) {
			return BLACK_BISHOP_PREFERRED_COORDINATES[position];
		}

		@Override
		public int rookBonus(final int position) {
			return BLACK_ROOK_PREFERRED_COORDINATES[position];
		}

		@Override
		public int queenBonus(final int position) {
			return BLACK_QUEEN_PREFERRED_COORDINATES[position];
		}

		@Override
		public int kingBonus(final int position) {
			return BLACK_KING_PREFERRED_COORDINATES[position];
		}
	};

	/**
	 * Gets the direction.
	 *
	 * @return the direction
	 */
	public abstract int getDirection();

	/**
	 * Gets the opposite direction.
	 *
	 * @return the opposite direction
	 */
	public abstract int getOppositeDirection();

	/**
	 * Pawn bonus.
	 *
	 * @param position
	 *            the position
	 * @return the int
	 */
	public abstract int pawnBonus(int position);

	/**
	 * Knight bonus.
	 *
	 * @param position
	 *            the position
	 * @return the int
	 */
	public abstract int knightBonus(int position);

	/**
	 * Bishop bonus.
	 *
	 * @param position
	 *            the position
	 * @return the int
	 */
	public abstract int bishopBonus(int position);

	/**
	 * Rook bonus.
	 *
	 * @param position
	 *            the position
	 * @return the int
	 */
	public abstract int rookBonus(int position);

	/**
	 * Queen bonus.
	 *
	 * @param position
	 *            the position
	 * @return the int
	 */
	public abstract int queenBonus(int position);

	/**
	 * King bonus.
	 *
	 * @param position
	 *            the position
	 * @return the int
	 */
	public abstract int kingBonus(int position);

	/**
	 * Checks if is white.
	 *
	 * @return true, if is white
	 */
	public abstract boolean isWhite();

	/**
	 * Checks if is black.
	 *
	 * @return true, if is black
	 */
	public abstract boolean isBlack();

	/**
	 * Checks if is pawn promotion square.
	 *
	 * @param position
	 *            the position
	 * @return true, if is pawn promotion square
	 */
	public abstract boolean isPawnPromotionSquare(int position);

	/**
	 * Choose player by alliance.
	 *
	 * @param whitePlayer
	 *            the white player
	 * @param blackPlayer
	 *            the black player
	 * @return the player
	 */
	public abstract Player choosePlayerByAlliance(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

	/** The Constant WHITE_PAWN_PREFERRED_COORDINATES. */
	private final static int[] WHITE_PAWN_PREFERRED_COORDINATES = { 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50,
			50, 10, 10, 20, 30, 30, 20, 10, 10, 5, 5, 10, 25, 25, 10, 5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, -5, -10, 0, 0,
			-10, -5, 5, 5, 10, 10, -20, -20, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0 };

	/** The Constant BLACK_PAWN_PREFERRED_COORDINATES. */
	private final static int[] BLACK_PAWN_PREFERRED_COORDINATES = { 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, -20, -20, 10, 10,
			5, 5, -5, -10, 0, 0, -10, -5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, 5, 10, 25, 25, 10, 5, 5, 10, 10, 20, 30, 30,
			20, 10, 10, 50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0 };

	/** The Constant WHITE_KNIGHT_PREFERRED_COORDINATES. */
	private final static int[] WHITE_KNIGHT_PREFERRED_COORDINATES = { -50, -40, -30, -30, -30, -30, -40, -50, -40, -20,
			0, 0, 0, 0, -20, -40, -30, 0, 10, 15, 15, 10, 0, -30, -30, 5, 15, 20, 20, 15, 5, -30, -30, 0, 15, 20, 20,
			15, 0, -30, -30, 5, 10, 15, 15, 10, 5, -30, -40, -20, 0, 5, 5, 0, -20, -40, -50, -40, -30, -30, -30, -30,
			-40, -50 };

	/** The Constant BLACK_KNIGHT_PREFERRED_COORDINATES. */
	private final static int[] BLACK_KNIGHT_PREFERRED_COORDINATES = { -50, -40, -30, -30, -30, -30, -40, -50, -40, -20,
			0, 5, 5, 0, -20, -40, -30, 5, 10, 15, 15, 10, 5, -30, -30, 0, 15, 20, 20, 15, 0, -30, -30, 5, 15, 20, 20,
			15, 5, -30, -30, 0, 10, 15, 15, 10, 0, -30, -40, -20, 0, 0, 0, 0, -20, -40, -50, -40, -30, -30, -30, -30,
			-40, -50, };

	/** The Constant WHITE_BISHOP_PREFERRED_COORDINATES. */
	private final static int[] WHITE_BISHOP_PREFERRED_COORDINATES = { -20, -10, -10, -10, -10, -10, -10, -20, -10, 0, 0,
			0, 0, 0, 0, -10, -10, 0, 5, 10, 10, 5, 0, -10, -10, 5, 5, 10, 10, 5, 5, -10, -10, 0, 10, 10, 10, 10, 0, -10,
			-10, 10, 10, 10, 10, 10, 10, -10, -10, 5, 0, 0, 0, 0, 5, -10, -20, -10, -10, -10, -10, -10, -10, -20 };

	/** The Constant BLACK_BISHOP_PREFERRED_COORDINATES. */
	private final static int[] BLACK_BISHOP_PREFERRED_COORDINATES = { -20, -10, -10, -10, -10, -10, -10, -20, -10, 5, 0,
			0, 0, 0, 5, -10, -10, 10, 10, 10, 10, 10, 10, -10, -10, 0, 10, 10, 10, 10, 0, -10, -10, 5, 5, 10, 10, 5, 5,
			-10, -10, 0, 5, 10, 10, 5, 0, -10, -10, 0, 0, 0, 0, 0, 0, -10, -20, -10, -10, -10, -10, -10, -10, -20, };

	/** The Constant WHITE_ROOK_PREFERRED_COORDINATES. */
	private final static int[] WHITE_ROOK_PREFERRED_COORDINATES = { 0, 0, 0, 0, 0, 0, 0, 0, 5, 20, 20, 20, 20, 20, 20,
			5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5, 0, 0, 0, 5, 5, 0, 0, 0 };

	/** The Constant BLACK_ROOK_PREFERRED_COORDINATES. */
	private final static int[] BLACK_ROOK_PREFERRED_COORDINATES = { 0, 0, 0, 5, 5, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5,
			-5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, 5,
			20, 20, 20, 20, 20, 20, 5, 0, 0, 0, 0, 0, 0, 0, 0, };

	/** The Constant WHITE_QUEEN_PREFERRED_COORDINATES. */
	private final static int[] WHITE_QUEEN_PREFERRED_COORDINATES = { -20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 0, 0,
			0, 0, 0, -10, -10, 0, 5, 5, 5, 5, 0, -10, -5, 0, 5, 5, 5, 5, 0, -5, 0, 0, 5, 5, 5, 5, 0, -5, -10, 5, 5, 5,
			5, 5, 0, -10, -10, 0, 5, 0, 0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10, -20 };

	/** The Constant BLACK_QUEEN_PREFERRED_COORDINATES. */
	private final static int[] BLACK_QUEEN_PREFERRED_COORDINATES = { -20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 5, 0,
			0, 0, 0, -10, -10, 5, 5, 5, 5, 5, 0, -10, 0, 0, 5, 5, 5, 5, 0, -5, 0, 0, 5, 5, 5, 5, 0, -5, -10, 0, 5, 5, 5,
			5, 0, -10, -10, 0, 0, 0, 0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10, -20 };

	/** The Constant WHITE_KING_PREFERRED_COORDINATES. */
	private final static int[] WHITE_KING_PREFERRED_COORDINATES = { -30, -40, -40, -50, -50, -40, -40, -30, -30, -40,
			-40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40,
			-30, -20, -30, -30, -40, -40, -30, -30, -20, -10, -20, -20, -20, -20, -20, -20, -10, 20, 20, 0, 0, 0, 0, 20,
			20, 20, 30, 10, 0, 0, 10, 30, 20 };

	/** The Constant BLACK_KING_PREFERRED_COORDINATES. */
	private final static int[] BLACK_KING_PREFERRED_COORDINATES = { 20, 30, 10, 0, 0, 10, 30, 20, 20, 20, 0, 0, 0, 0,
			20, 20, -10, -20, -20, -20, -20, -20, -20, -10, -20, -30, -30, -40, -40, -30, -30, -20, -30, -40, -40, -50,
			-50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -30,
			-40, -40, -50, -50, -40, -40, -30 };

	/** The Constant UP_DIRECTION. */
	private static final int UP_DIRECTION = -1;

	/** The Constant DOWN_DIRECTION. */
	private static final int DOWN_DIRECTION = 1;
}