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
 * Bishop as a chess piece.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class Bishop extends Piece {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1019542655022758472L;

	/** The Constant CANDIDATE_MOVE_COORDINATES. */
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -7, 7, 9 };

	/**
	 * Instantiates a new bishop.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 */
	public Bishop(final Alliance alliance, final int piecePosition) {
		super(PieceType.BISHOP, alliance, piecePosition, true);
	}

	/**
	 * Instantiates a new bishop.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param isFirstMove
	 *            the is first move
	 */
	public Bishop(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.BISHOP, alliance, piecePosition, isFirstMove);
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
			int candidateDestinationCoordinate = this.piecePosition;
			while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)
						|| isEighthColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
					break;
				}
				candidateDestinationCoordinate += currentCandidateOffset;
				if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
					final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
					if (!candidateDestinationTile.isTileOccupied()) {
						legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
					} else {
						final Piece pieceAtDestination = candidateDestinationTile.getPiece();
						final Alliance pieceAlliance = pieceAtDestination.getPieceAllegiance();
						if (this.pieceAlliance != pieceAlliance) {
							legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate,
									pieceAtDestination));
						}
						break;
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
		return this.pieceAlliance.bishopBonus(this.piecePosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.pieces.Piece#movePiece(com.chess.engine.classic.
	 * board.Move)
	 */
	@Override
	public Bishop movePiece(final Move move) {
		return PieceUtils.INSTANCE.getMovedBishop(move.getMovedPiece().getPieceAllegiance(),
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
	 * @param currentCandidate
	 *            the current candidate
	 * @param candidateDestinationCoordinate
	 *            the candidate destination coordinate
	 * @return true, if is first column exclusion
	 */
	private static boolean isFirstColumnExclusion(final int currentCandidate,
			final int candidateDestinationCoordinate) {
		return (BoardUtils.INSTANCE.FIRST_COLUMN.get(candidateDestinationCoordinate)
				&& ((currentCandidate == -9) || (currentCandidate == 7)));
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
		return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(candidateDestinationCoordinate)
				&& ((currentCandidate == -7) || (currentCandidate == 9));
	}
}