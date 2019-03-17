package com.chess.engine.classic.pieces;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Tile;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.MajorMove;
import com.chess.engine.classic.board.Move.MajorAttackMove;

import com.google.common.collect.ImmutableList;

/**
 * Queen as a chess piece.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class Queen extends Piece {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2104741690278240198L;

	/** The Constant CANDIDATE_MOVE_COORDINATES. */
	private final static int[] CANDIDATE_MOVE_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

	/**
	 * Instantiates a new queen.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 */
	public Queen(final Alliance alliance, final int piecePosition) {
		super(PieceType.QUEEN, alliance, piecePosition, true);
	}

	/**
	 * Instantiates a new queen.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param isFirstMove
	 *            the is first move
	 */
	public Queen(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.QUEEN, alliance, piecePosition, isFirstMove);
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
		int candidateDestinationCoordinate;
		for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
			candidateDestinationCoordinate = this.piecePosition;
			while (true) {
				if (isFirstColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)
						|| isEightColumnExclusion(currentCandidateOffset, candidateDestinationCoordinate)) {
					break;
				}
				candidateDestinationCoordinate += currentCandidateOffset;
				if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
					break;
				} else {
					final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
					if (!candidateDestinationTile.isTileOccupied()) {
						legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
					} else {
						final Piece pieceAtDestination = candidateDestinationTile.getPiece();
						final Alliance pieceAtDestinationAllegiance = pieceAtDestination.getPieceAllegiance();
						if (this.pieceAlliance != pieceAtDestinationAllegiance) {
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
		return this.pieceAlliance.queenBonus(this.piecePosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.pieces.Piece#movePiece(com.chess.engine.classic.
	 * board.Move)
	 */
	@Override
	public Queen movePiece(final Move move) {
		return PieceUtils.INSTANCE.getMovedQueen(move.getMovedPiece().getPieceAllegiance(),
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
	 * @param candidatePosition
	 *            the candidate position
	 * @return true, if is first column exclusion
	 */
	private static boolean isFirstColumnExclusion(final int currentPosition, final int candidatePosition) {
		return BoardUtils.INSTANCE.FIRST_COLUMN.get(candidatePosition)
				&& ((currentPosition == -9) || (currentPosition == -1) || (currentPosition == 7));
	}

	/**
	 * Checks if is eight column exclusion.
	 *
	 * @param currentPosition
	 *            the current position
	 * @param candidatePosition
	 *            the candidate position
	 * @return true, if is eight column exclusion
	 */
	private static boolean isEightColumnExclusion(final int currentPosition, final int candidatePosition) {
		return BoardUtils.INSTANCE.EIGHTH_COLUMN.get(candidatePosition)
				&& ((currentPosition == -7) || (currentPosition == 1) || (currentPosition == 9));
	}
}