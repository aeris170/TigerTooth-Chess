package com.chess.engine.classic.player.ai;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.player.ai.KingSafetyAnalyzer.KingDistance;

import com.google.common.annotations.VisibleForTesting;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class StandardBoardEvaluator implements BoardEvaluator {

	/** The Constant CHECK_MATE_BONUS. */
	private final static int CHECK_MATE_BONUS = 10000;

	/** The Constant CHECK_BONUS. */
	private final static int CHECK_BONUS = 50;

	/** The Constant CASTLE_BONUS. */
	private final static int CASTLE_BONUS = 60;

	/** The Constant CASTLE_CAPABLE_BONUS. */
	private final static int CASTLE_CAPABLE_BONUS = 25;

	/** The Constant MOBILITY_MULTIPLIER. */
	private final static int MOBILITY_MULTIPLIER = 2;

	/** The Constant ATTACK_MULTIPLIER. */
	private final static int ATTACK_MULTIPLIER = 2;

	/** The Constant TWO_BISHOPS_BONUS. */
	private final static int TWO_BISHOPS_BONUS = 50;

	/** The Constant INSTANCE. */
	private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

	/**
	 * Instantiates a new standard board evaluator.
	 */
	private StandardBoardEvaluator() {
	}

	/**
	 * Gets the.
	 *
	 * @return the standard board evaluator
	 */
	public static StandardBoardEvaluator get() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.player.ai.BoardEvaluator#evaluate(com.chess.
	 * engine.classic.board.Board, int)
	 */
	@Override
	public int evaluate(final Board board, final int depth) {
		return score(board.whitePlayer(), depth) - score(board.blackPlayer(), depth);
	}

	/**
	 * Score.
	 *
	 * @param player
	 *            the player
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	@VisibleForTesting
	private static int score(final Player player, final int depth) {
		return mobility(player) + kingThreats(player, depth) + attacks(player) + castle(player)
				+ pieceEvaluations(player) + pawnStructure(player);
	}

	/**
	 * Attacks.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int attacks(final Player player) {
		int attackScore = 0;
		for (final Move move : player.getLegalMoves()) {
			if (move.isAttack()) {
				final Piece movedPiece = move.getMovedPiece();
				final Piece attackedPiece = move.getAttackedPiece();
				if (movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
					attackScore++;
				}
			}
		}
		return attackScore * ATTACK_MULTIPLIER;
	}

	/**
	 * Piece evaluations.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int pieceEvaluations(final Player player) {
		int pieceValuationScore = 0;
		int numBishops = 0;
		for (final Piece piece : player.getActivePieces()) {
			pieceValuationScore += piece.getPieceValue() + piece.locationBonus();
			if (piece.getPieceType().isBishop()) {
				numBishops++;
			}
		}
		return pieceValuationScore + (numBishops == 2 ? TWO_BISHOPS_BONUS : 0);
	}

	/**
	 * Mobility.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int mobility(final Player player) {
		return MOBILITY_MULTIPLIER * mobilityRatio(player);
	}

	/**
	 * Mobility ratio.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int mobilityRatio(final Player player) {
		return (int) ((player.getLegalMoves().size() * 100.0f) / player.getOpponent().getLegalMoves().size());
	}

	/**
	 * King threats.
	 *
	 * @param player
	 *            the player
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	private static int kingThreats(final Player player, final int depth) {
		return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : check(player);
	}

	/**
	 * Check.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int check(final Player player) {
		return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
	}

	/**
	 * Depth bonus.
	 *
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	private static int depthBonus(final int depth) {
		return depth == 0 ? 1 : 100 * depth;
	}

	/**
	 * Castle.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int castle(final Player player) {
		return player.isCastled() ? CASTLE_BONUS : castleCapable(player);
	}

	/**
	 * Castle capable.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int castleCapable(final Player player) {
		return player.isKingSideCastleCapable() || player.isQueenSideCastleCapable() ? CASTLE_CAPABLE_BONUS : 0;
	}

	/**
	 * Pawn structure.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	private static int pawnStructure(final Player player) {
		return PawnStructureAnalyzer.get().pawnStructureScore(player);
	}

	/**
	 * King safety.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	@SuppressWarnings("unused")
	private static int kingSafety(final Player player) {
		final KingDistance kingDistance = KingSafetyAnalyzer.get().calculateKingTropism(player);
		return ((kingDistance.getEnemyPiece().getPieceValue() / 100) * kingDistance.getDistance());
	}

	/**
	 * Rook structure.
	 *
	 * @param board
	 *            the board
	 * @param player
	 *            the player
	 * @return the int
	 */
	@SuppressWarnings("unused")
	private static int rookStructure(final Board board, final Player player) {
		return RookStructureAnalyzer.get().rookStructureScore(board, player);
	}
}