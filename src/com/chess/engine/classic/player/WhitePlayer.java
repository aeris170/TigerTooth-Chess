package com.chess.engine.classic.player;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Tile;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.pieces.Rook;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.KingSideCastleMove;
import com.chess.engine.classic.board.Move.QueenSideCastleMove;

import com.google.common.collect.ImmutableList;

/**
 * One of the players present in the game, the White Player.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class WhitePlayer extends Player {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7519995197866845847L;

	/**
	 * Instantiates a new white player.
	 *
	 * @param board
	 *            the board
	 * @param whiteStandardLegals
	 *            the white standard legals
	 * @param blackStandardLegals
	 *            the black standard legals
	 */
	public WhitePlayer(final Board board, final Collection<Move> whiteStandardLegals,
			final Collection<Move> blackStandardLegals) {
		super(board, whiteStandardLegals, blackStandardLegals);
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
		if (this.playerKing.isFirstMove() && this.playerKing.getPiecePosition() == 60 && !this.isInCheck()) {
			// whites king side castle
			if (!this.board.getTile(61).isTileOccupied() && !this.board.getTile(62).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(63);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
					if (Player.calculateAttacksOnTile(61, opponentLegals).isEmpty()
							&& Player.calculateAttacksOnTile(62, opponentLegals).isEmpty()
							&& rookTile.getPiece().getPieceType().isRook()) {
						if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {
							kingCastles.add(new KingSideCastleMove(this.board, this.playerKing, 62,
									(Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
						}
					}
				}
			}
			// whites queen side castle
			if (!this.board.getTile(59).isTileOccupied() && !this.board.getTile(58).isTileOccupied()
					&& !this.board.getTile(57).isTileOccupied()) {
				final Tile rookTile = this.board.getTile(56);
				if (rookTile.isTileOccupied() && rookTile.getPiece().isFirstMove()) {
					if (Player.calculateAttacksOnTile(58, opponentLegals).isEmpty()
							&& Player.calculateAttacksOnTile(59, opponentLegals).isEmpty()
							&& rookTile.getPiece().getPieceType().isRook()) {
						if (!BoardUtils.isKingPawnTrap(this.board, this.playerKing, 52)) {
							kingCastles.add(new QueenSideCastleMove(this.board, this.playerKing, 58,
									(Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
						}
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
	public BlackPlayer getOpponent() {
		return this.board.blackPlayer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.player.Player#getActivePieces()
	 */
	@Override
	public Collection<Piece> getActivePieces() {
		return this.board.getWhitePieces();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.engine.classic.player.Player#getAlliance()
	 */
	@Override
	public Alliance getAlliance() {
		return Alliance.WHITE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Alliance.WHITE.toString();
	}
}