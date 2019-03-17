package com.chess.engine.classic.player;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Tile;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Rook;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.KingSideCastleMove;
import com.chess.engine.classic.board.Move.QueenSideCastleMove;

import com.google.common.collect.ImmutableList;

/**
 * One of the players present in the game, the Black Player.
 *
 * @author Do�a Oru�
 * @version 06.08.2017
 */
public final class BlackPlayer extends Player {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6426997127311124500L;

	/**
	 * Instantiates a new black player.
	 *
	 * @param board
	 *            the board
	 * @param whiteStandardLegals
	 *            the white standard legals
	 * @param blackStandardLegals
	 *            the black standard legals
	 */
	public BlackPlayer(final Board board, final Collection<Move> whiteStandardLegals,
			final Collection<Move> blackStandardLegals) {
		super(board, blackStandardLegals, whiteStandardLegals);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.engine.classic.player.Player#calculateKingCastles(java.util.
	 * Collection, java.util.Collection)
	 */
	@Override
	protected Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
			final Collection<Move> opponentLegals) {
		if (this.isInCheck() || this.isCastled()
				|| !(this.isKingSideCastleCapable() || this.isQueenSideCastleCapable())) {
			return ImmutableList.of();
		}
		final List<Move> kingCastles = new ArrayList<>();
		if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 4 && !this.isInCheck) {
			// blacks king side castle
			if (!this.board.getTile(5).isTileOccupied() && !this.board.getTile(6).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(7);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
						&& Player.calculateAttacksOnTile(5, opponentLegals).isEmpty()
						&& Player.calculateAttacksOnTile(6, opponentLegals).isEmpty()
						&& rookTile.getPiece().getPieceType().isRook()) {
					if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
						kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 6,
								(Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
					}
				}
			}
			// blacks queen side castle
			if (!this.board.getTile(1).isTileOccupied() && !this.board.getTile(2).isTileOccupied()
					&& !this.board.getTile(3).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(0);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()
						&& Player.calculateAttacksOnTile(2, opponentLegals).isEmpty()
						&& Player.calculateAttacksOnTile(3, opponentLegals).isEmpty()
						&& rookTile.getPiece().getPieceType().isRook()) {
					if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 12)) {
						kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 2,
								(Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
					}
				}
			}
		}
		return ImmutableList.copyOf(kingCastles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.player.Player#getOpponent()
	 */
	@Override
	public WhitePlayer getOpponent() {
		return this.board.whitePlayer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.player.Player#getActivePieces()
	 */
	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getBlackPieces();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.player.Player#getAlliance()
	 */
	@Override
	public Alliance getAlliance() {
		return Alliance.BLACK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Alliance.BLACK.toString();
	}
}