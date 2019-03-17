package com.chess.engine.classic.player;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.io.Serializable;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.pieces.King;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.board.MoveTransition;
import com.chess.engine.classic.board.Move.MoveStatus;

import com.google.common.collect.ImmutableList;

/**
 * A chyess player, in chess there can only be two players present in a single
 * game. White and Black players.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public abstract class Player implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5090461048593011990L;

	/** The board. */
	protected final Board board;

	/** The player king. */
	protected final King playerKing;

	/** The legal moves. */
	protected final Collection<Move> legalMoves;

	/** The is ýn check. */
	protected final boolean isInCheck;

	/**
	 * Instantiates a new player.
	 *
	 * @param board
	 *            the board
	 * @param playerLegals
	 *            the player legals
	 * @param opponentLegals
	 *            the opponent legals
	 */
	Player(final Board board, final Collection<Move> playerLegals, final Collection<Move> opponentLegals) {
		this.board = board;
		this.playerKing = establishKing();
		this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
		playerLegals.addAll(calculateKingCastles(playerLegals, opponentLegals));
		this.legalMoves = ImmutableList.copyOf(playerLegals);
	}

	/**
	 * Checks if is move legal.
	 *
	 * @param move
	 *            the move
	 * @return true, if is move legal
	 */
	private boolean isMoveLegal(final Move move) {
		return this.legalMoves.contains(move);
	}

	/**
	 * Checks if is ýn check.
	 *
	 * @return true, if is ýn check
	 */
	public boolean isInCheck() {
		return this.isInCheck;
	}

	/**
	 * Checks if is ýn check mate.
	 *
	 * @return true, if is ýn check mate
	 */
	public boolean isInCheckMate() {
		return this.isInCheck && !hasEscapeMoves();
	}

	/**
	 * Checks if is ýn stale mate.
	 *
	 * @return true, if is ýn stale mate
	 */
	public boolean isInStaleMate() {
		return !this.isInCheck && !hasEscapeMoves();
	}

	/**
	 * Checks if is castled.
	 *
	 * @return true, if is castled
	 */
	public boolean isCastled() {
		return this.playerKing.isCastled();
	}

	/**
	 * Checks if is king side castle capable.
	 *
	 * @return true, if is king side castle capable
	 */
	public boolean isKingSideCastleCapable() {
		return this.playerKing.isKingSideCastleCapable();
	}

	/**
	 * Checks if is queen side castle capable.
	 *
	 * @return true, if is queen side castle capable
	 */
	public boolean isQueenSideCastleCapable() {
		return this.playerKing.isQueenSideCastleCapable();
	}

	/**
	 * Gets the player king.
	 *
	 * @return the player king
	 */
	public King getPlayerKing() {
		return this.playerKing;
	}

	/**
	 * Establish king.
	 *
	 * @return the king
	 */
	private King establishKing() {
		for (final Piece piece : getActivePieces()) {
			if (piece.getPieceType().isKing()) {
				return (King) piece;
			}
		}
		throw new RuntimeException("Should not reach here! " + this.getAlliance() + " king could not be established!");
	}

	/**
	 * Checks for escape moves.
	 *
	 * @return true, if successful
	 */
	private boolean hasEscapeMoves() {
		for (final Move move : getLegalMoves()) {
			final MoveTransition transition = makeMove(move);
			if (transition.getMoveStatus().isDone()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the legal moves.
	 *
	 * @return the legal moves
	 */
	public Collection<Move> getLegalMoves() {
		return this.legalMoves;
	}

	/**
	 * Calculate attacks on tile.
	 *
	 * @param tile
	 *            the tile
	 * @param moves
	 *            the moves
	 * @return the collection
	 */
	static Collection<Move> calculateAttacksOnTile(final int tile, final Collection<Move> moves) {
		final List<Move> attackMoves = new ArrayList<>();
		for (final Move move : moves) {
			if (tile == move.getDestinationCoordinate()) {
				attackMoves.add(move);
			}
		}
		return ImmutableList.copyOf(attackMoves);
	}

	/**
	 * Make move.
	 *
	 * @param move
	 *            the move
	 * @return the move transition
	 */
	public MoveTransition makeMove(final Move move) {
		if (!isMoveLegal(move)) {
			return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
		}
		final Board transitionedBoard = move.execute();
		final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
				transitionedBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
				transitionedBoard.currentPlayer().getLegalMoves());
		if (!kingAttacks.isEmpty()) {
			return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
		}
		return new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
	}

	/**
	 * Un make move.
	 *
	 * @param move
	 *            the move
	 * @return the move transition
	 */
	public MoveTransition unMakeMove(final Move move) {
		return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
	}

	/**
	 * Gets the active pieces.
	 *
	 * @return the active pieces
	 */
	public abstract Collection<Piece> getActivePieces();

	/**
	 * Gets the alliance.
	 *
	 * @return the alliance
	 */
	public abstract Alliance getAlliance();

	/**
	 * Gets the opponent.
	 *
	 * @return the opponent
	 */
	public abstract Player getOpponent();

	/**
	 * Calculate king castles.
	 *
	 * @param playerLegals
	 *            the player legals
	 * @param opponentLegals
	 *            the opponent legals
	 * @return the collection
	 */
	protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
			Collection<Move> opponentLegals);
}