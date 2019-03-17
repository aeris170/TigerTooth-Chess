package com.chess.engine.classic.player.ai;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.BoardUtils;

import com.google.common.collect.ImmutableList;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class KingSafetyAnalyzer {

	/** The Constant COLUMNS. */
	@SuppressWarnings("unused")
	private static final List<List<Boolean>> COLUMNS = initColumns();

	/** The Constant INSTANCE. */
	private static final KingSafetyAnalyzer INSTANCE = new KingSafetyAnalyzer();

	/**
	 * Instantiates a new king safety analyzer.
	 */
	private KingSafetyAnalyzer() {
	}

	/**
	 * Gets the.
	 *
	 * @return the king safety analyzer
	 */
	public static KingSafetyAnalyzer get() {
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
	 * Calculate king tropism.
	 *
	 * @param player
	 *            the player
	 * @return the king distance
	 */
	public KingDistance calculateKingTropism(final Player player) {
		final int playerKingSquare = player.getPlayerKing().getPiecePosition();
		final Collection<Move> enemyMoves = player.getOpponent().getLegalMoves();
		Piece closestPiece = null;
		int closestDistance = Integer.MAX_VALUE;
		for (final Move move : enemyMoves) {
			final int currentDistance = calculateChebyshevDistance(playerKingSquare, move.getDestinationCoordinate());
			if (currentDistance < closestDistance) {
				closestDistance = currentDistance;
				closestPiece = move.getMovedPiece();
			}
		}
		return new KingDistance(closestPiece, closestDistance);
	}

	/**
	 * Calculate chebyshev distance.
	 *
	 * @param kingTileId
	 *            the king tile ýd
	 * @param enemyAttackTileId
	 *            the enemy attack tile ýd
	 * @return the int
	 */
	private int calculateChebyshevDistance(final int kingTileId, final int enemyAttackTileId) {

		final int squareOneRank = getRank(kingTileId);
		final int squareTwoRank = getRank(enemyAttackTileId);

		final int squareOneFile = getFile(kingTileId);
		final int squareTwoFile = getFile(enemyAttackTileId);

		final int rankDistance = Math.abs(squareTwoRank - squareOneRank);
		final int fileDistance = Math.abs(squareTwoFile - squareOneFile);

		return Math.max(rankDistance, fileDistance);
	}

	/**
	 * Gets the file.
	 *
	 * @param coordinate
	 *            the coordinate
	 * @return the file
	 */
	private static int getFile(final int coordinate) {
		if (BoardUtils.INSTANCE.FIRST_COLUMN.get(coordinate)) {
			return 1;
		} else if (BoardUtils.INSTANCE.SECOND_COLUMN.get(coordinate)) {
			return 2;
		} else if (BoardUtils.INSTANCE.THIRD_COLUMN.get(coordinate)) {
			return 3;
		} else if (BoardUtils.INSTANCE.FOURTH_COLUMN.get(coordinate)) {
			return 4;
		} else if (BoardUtils.INSTANCE.FIFTH_COLUMN.get(coordinate)) {
			return 5;
		} else if (BoardUtils.INSTANCE.SIXTH_COLUMN.get(coordinate)) {
			return 6;
		} else if (BoardUtils.INSTANCE.SEVENTH_COLUMN.get(coordinate)) {
			return 7;
		} else if (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(coordinate)) {
			return 8;
		}
		throw new RuntimeException("should not reach here!");
	}

	/**
	 * Gets the rank.
	 *
	 * @param coordinate
	 *            the coordinate
	 * @return the rank
	 */
	private static int getRank(final int coordinate) {
		if (BoardUtils.INSTANCE.FIRST_ROW.get(coordinate)) {
			return 1;
		} else if (BoardUtils.INSTANCE.SECOND_ROW.get(coordinate)) {
			return 2;
		} else if (BoardUtils.INSTANCE.THIRD_ROW.get(coordinate)) {
			return 3;
		} else if (BoardUtils.INSTANCE.FOURTH_ROW.get(coordinate)) {
			return 4;
		} else if (BoardUtils.INSTANCE.FIFTH_ROW.get(coordinate)) {
			return 5;
		} else if (BoardUtils.INSTANCE.SIXTH_ROW.get(coordinate)) {
			return 6;
		} else if (BoardUtils.INSTANCE.SEVENTH_ROW.get(coordinate)) {
			return 7;
		} else if (BoardUtils.INSTANCE.EIGHTH_ROW.get(coordinate)) {
			return 8;
		}
		throw new RuntimeException("should not reach here!");
	}

	/**
	 * The Class KingDistance.
	 */
	static class KingDistance {

		/** The enemy piece. */
		final Piece enemyPiece;

		/** The distance. */
		final int distance;

		/**
		 * Instantiates a new king distance.
		 *
		 * @param enemyDistance
		 *            the enemy distance
		 * @param distance
		 *            the distance
		 */
		KingDistance(final Piece enemyDistance, final int distance) {
			this.enemyPiece = enemyDistance;
			this.distance = distance;
		}

		/**
		 * Gets the enemy piece.
		 *
		 * @return the enemy piece
		 */
		public Piece getEnemyPiece() {
			return enemyPiece;
		}

		/**
		 * Gets the distance.
		 *
		 * @return the distance
		 */
		public int getDistance() {
			return distance;
		}

		/**
		 * Tropism score.
		 *
		 * @return the int
		 */
		public int tropismScore() {
			return (enemyPiece.getPieceValue() / 10) * distance;
		}
	}
}