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
 * Knight as a chess piece.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class Knight extends Piece {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7188240228859053447L;

	/** The Constant CANDIDATE_MOVE_COORDINATES. */
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -17, -15, -10, -6, 6, 10, 15, 17 };

	/**
	 * Instantiates a new knight.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 */
	public Knight(final Alliance alliance, final int piecePosition) {
		super(PieceType.KNIGHT, alliance, piecePosition, true);
	}

	/**
	 * Instantiates a new knight.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param isFirstMove
	 *            the is first move
	 */
	public Knight(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.KNIGHT, alliance, piecePosition, isFirstMove);
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
					|| isSecondColumnExclusion(this.piecePosition, currentCandidateOffset)
					|| isSeventhColumnExclusion(this.piecePosition, currentCandidateOffset)
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
	 * @see com.chess.engine.classic.pieces.Piece#locationBonus()
	 */
	@Override
	public int locationBonus() {
		return this.pieceAlliance.knightBonus(this.piecePosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.pieces.Piece#movePiece(com.chess.engine.classic.
	 * board.Move)
	 */
	@Override
	public Knight movePiece(final Move move) {
		return PieceUtils.INSTANCE.getMovedKnight(move.getMovedPiece().getPieceAllegiance(),
				move.getDestinationCoordinate());
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

	/**
	 * Checks if is first column exclusion.
	 *
	 * @param currentPosition
	 *            the current position
	 * @param candidateOffset
	 *            the candidate offset
	 * @return true, if is first column exclusion
	 */
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.FIRST_COLUMN.get(currentPosition) && ((candidateOffset == -17)
				|| (candidateOffset == -10) || (candidateOffset == 6) || (candidateOffset == 15));
	}

	/**
	 * Checks if is second column exclusion.
	 *
	 * @param currentPosition
	 *            the current position
	 * @param candidateOffset
	 *            the candidate offset
	 * @return true, if is second column exclusion
	 */
	private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.SECOND_COLUMN.get(currentPosition)
				&& ((candidateOffset == -10) || (candidateOffset == 6));
	}

	/**
	 * Checks if is seventh column exclusion.
	 *
	 * @param currentPosition
	 *            the current position
	 * @param candidateOffset
	 *            the candidate offset
	 * @return true, if is seventh column exclusion
	 */
	private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.SEVENTH_COLUMN.get(currentPosition)
				&& ((candidateOffset == -6) || (candidateOffset == 10));
	}

	/**
	 * Checks if is eighth column exclusion.
	 *
	 * @param currentPosition
	 *            the current position
	 * @param candidateOffset
	 *            the candidate offset
	 * @return true, if is eighth column exclusion
	 */
	private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
		return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(currentPosition) && ((candidateOffset == -15)
				|| (candidateOffset == -6) || (candidateOffset == 10) || (candidateOffset == 17));
	}
}