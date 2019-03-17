package com.chess.engine.classic.pieces;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.PawnJump;
import com.chess.engine.classic.board.Move.PawnMove;
import com.chess.engine.classic.board.Move.PawnPromotion;
import com.chess.engine.classic.board.Move.PawnAttackMove;
import com.chess.engine.classic.board.Move.PawnEnPassantAttack;

import com.google.common.collect.ImmutableList;

/**
 * Pawn as a chess piece.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class Pawn extends Piece {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7192250547682132836L;

	/** The Constant CANDIDATE_MOVE_COORDINATES. */
	private final static int[] CANDIDATE_MOVE_COORDINATES = { 8, 16, 7, 9 };

	/**
	 * Instantiates a new pawn.
	 *
	 * @param allegiance
	 *            the allegiance
	 * @param piecePosition
	 *            the piece position
	 */
	public Pawn(final Alliance allegiance, final int piecePosition) {
		super(PieceType.PAWN, allegiance, piecePosition, true);
	}

	/**
	 * Instantiates a new pawn.
	 *
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param isFirstMove
	 *            the is first move
	 */
	public Pawn(final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
		super(PieceType.PAWN, alliance, piecePosition, isFirstMove);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.pieces.Piece#locationBonus()
	 */
	@Override
	public int locationBonus() {
		return this.pieceAlliance.pawnBonus(this.piecePosition);
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
			int candidateDestinationCoordinate = this.piecePosition
					+ (this.pieceAlliance.getDirection() * currentCandidateOffset);
			if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				continue;
			}
			if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
				if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate),
							new Queen(this.pieceAlliance, candidateDestinationCoordinate)));
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate),
							new Rook(this.pieceAlliance, candidateDestinationCoordinate)));
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate),
							new Bishop(this.pieceAlliance, candidateDestinationCoordinate)));
					legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate),
							new Knight(this.pieceAlliance, candidateDestinationCoordinate)));
				} else {
					legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
				}
			} else if (currentCandidateOffset == 16 && this.isFirstMove()
					&& ((BoardUtils.INSTANCE.SECOND_ROW.get(this.piecePosition) && this.pieceAlliance.isBlack())
							|| (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.piecePosition)
									&& this.pieceAlliance.isWhite()))) {
				final int behindCandidateDestinationCoordinate = this.piecePosition
						+ (this.pieceAlliance.getDirection() * 8);
				if (!board.getTile(candidateDestinationCoordinate).isTileOccupied()
						&& !board.getTile(behindCandidateDestinationCoordinate).isTileOccupied()) {
					legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
				}
			} else if (currentCandidateOffset == 7 && !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition)
					&& this.pieceAlliance.isWhite())
					|| (BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate),
									new Queen(this.pieceAlliance, candidateDestinationCoordinate)));
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate),
									new Rook(this.pieceAlliance, candidateDestinationCoordinate)));
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate),
									new Bishop(this.pieceAlliance, candidateDestinationCoordinate)));
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate),
									new Knight(this.pieceAlliance, candidateDestinationCoordinate)));
						} else {
							legalMoves.add(
									new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
						}
					}
				} else if (board.getEnPassantPawn() != null && board.getEnPassantPawn()
						.getPiecePosition() == (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
					final Piece pieceOnCandidate = board.getEnPassantPawn();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
						legalMoves.add(
								new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));
					}
				}
			} else if (currentCandidateOffset == 9 && !((BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition)
					&& this.pieceAlliance.isWhite())
					|| (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
				if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
					if (this.pieceAlliance != board.getTile(candidateDestinationCoordinate).getPiece()
							.getPieceAllegiance()) {
						if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate,
											board.getTile(candidateDestinationCoordinate).getPiece()),
									new Queen(this.pieceAlliance, candidateDestinationCoordinate)));
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate,
											board.getTile(candidateDestinationCoordinate).getPiece()),
									new Rook(this.pieceAlliance, candidateDestinationCoordinate)));
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate,
											board.getTile(candidateDestinationCoordinate).getPiece()),
									new Bishop(this.pieceAlliance, candidateDestinationCoordinate)));
							legalMoves.add(new PawnPromotion(
									new PawnAttackMove(board, this, candidateDestinationCoordinate,
											board.getTile(candidateDestinationCoordinate).getPiece()),
									new Knight(this.pieceAlliance, candidateDestinationCoordinate)));
						} else {
							legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,
									board.getTile(candidateDestinationCoordinate).getPiece()));
						}
					}
				} else if (board.getEnPassantPawn() != null && board.getEnPassantPawn()
						.getPiecePosition() == (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
					final Piece pieceOnCandidate = board.getEnPassantPawn();
					if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
						legalMoves.add(
								new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));
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
	 * @see
	 * com.chess.engine.classic.pieces.Piece#movePiece(com.chess.engine.classic.
	 * board.Move)
	 */
	@Override
	public Pawn movePiece(final Move move) {
		return PieceUtils.INSTANCE.getMovedPawn(move.getMovedPiece().getPieceAllegiance(),
				move.getDestinationCoordinate());
	}
}