package com.chess.engine.classic.pieces;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Tile;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.MajorMove;
import com.chess.engine.classic.board.Move.MajorAttackMove;

import com.google.common.collect.ImmutableList;

/**
 * King as a chess piece.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class King extends Piece {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -773078811659835667L;

	/** The Constant CANDIDATE_MOVE_COORDINATES. */
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

	/** The is castled. */
	private final boolean isCastled;

	/** The king side castle capable. */
	private final boolean kingSideCastleCapable;

	/** The queen side castle capable. */
	private final boolean queenSideCastleCapable;

	/**
	 * Instantiates a new king.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param kingSideCastleCapable
	 *            the king side castle capable
	 * @param queenSideCastleCapable
	 *            the queen side castle capable
	 */
	public King(final Alliance alliance, final int piecePosition, final boolean kingSideCastleCapable,
			final boolean queenSideCastleCapable) {
		super(PieceType.KING, alliance, piecePosition, true);
		this.isCastled = false;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	/**
	 * Instantiates a new king.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param isFirstMove
	 *            the is first move
	 * @param isCastled
	 *            the is castled
	 * @param kingSideCastleCapable
	 *            the king side castle capable
	 * @param queenSideCastleCapable
	 *            the queen side castle capable
	 */
	public King(final Alliance alliance, final int piecePosition, final boolean isFirstMove, final boolean isCastled,
			final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
		super(PieceType.KING, alliance, piecePosition, isFirstMove);
		this.isCastled = isCastled;
		this.kingSideCastleCapable = kingSideCastleCapable;
		this.queenSideCastleCapable = queenSideCastleCapable;
	}

	/**
	 * Checks if is castled.
	 *
	 * @return true, if is castled
	 */
	public boolean isCastled() {
		return this.isCastled;
	}

	/**
	 * Checks if is king side castle capable.
	 *
	 * @return true, if is king side castle capable
	 */
	public boolean isKingSideCastleCapable() {
		return this.kingSideCastleCapable;
	}

	/**
	 * Checks if is queen side castle capable.
	 *
	 * @return true, if is queen side castle capable
	 */
	public boolean isQueenSideCastleCapable() {
		return this.queenSideCastleCapable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.pieces.Piece#calculateLegalMoves(com.chess.
	 * engine.classic.board.Board)
	 */
	@Override
	public Collection<Move> calculateLegalMoves(final Board board) {
		final List<Move> legalMoves = new ArrayList<>();
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset)
					|| isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}
			final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;
			if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				if (!candidateDestinationTile.isTileOccupied()) {
					legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
				} else {
					final Piece pieceAtDestination = candidateDestinationTile.getPiece();
					final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
					if (this.pieceAlliance != pieceAtDestinationAllegiance) {
						legalMoves.add(
								new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
					}
				}
			}
		}
		return ImmutableList.copyOf(legalMoves);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.pieceType.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.pieces.Piece#locationBonus()
	 */
	@Override
	public int locationBonus() {
		return this.pieceAlliance.kingBonus(this.piecePosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.pieces.Piece#movePiece(com.chess.engine.classic.
	 * board.Move)
	 */
	@Override
	public King movePiece(final Move move) {
		return new King(this.pieceAlliance, move.getDestinationCoordinate(), false, move.isCastlingMove(), false,
				false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.pieces.Piece#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof King)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}
		final King king = (King) other;
		return isCastled == king.isCastled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.pieces.Piece#hashCode()
	 */
	@Override
	public int hashCode() {
		return (31 * super.hashCode()) + (isCastled ? 1 : 0);
	}

	/**
	 * Checks if is first column exclusion.
	 *
	 * @param currentCandidate
	 *            the current candidate
	 * @param candidateDestinationCoordinate
	 *            the candidate destination coordinate
	 * @return true, if is first column exclusion
	 */
	private static boolean isFirstColumnExclusion(final int currentCandidate,
			final int candidateDestinationCoordinate) {
		return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentCandidate) && ((candidateDestinationCoordinate == -9)
				|| (candidateDestinationCoordinate == -1) || (candidateDestinationCoordinate == 7));
	}

	/**
	 * Checks if is eighth column exclusion.
	 *
	 * @param currentCandidate
	 *            the current candidate
	 * @param candidateDestinationCoordinate
	 *            the candidate destination coordinate
	 * @return true, if is eighth column exclusion
	 */
	private static boolean isEighthColumnExclusion(final int currentCandidate,
			final int candidateDestinationCoordinate) {
		return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentCandidate) && ((candidateDestinationCoordinate == -7)
				|| (candidateDestinationCoordinate == 1) || (candidateDestinationCoordinate == 9));
	}
}