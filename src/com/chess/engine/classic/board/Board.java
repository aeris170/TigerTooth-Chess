package com.chess.engine.classic.board;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.io.Serializable;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.pieces.Pawn;
import com.chess.engine.classic.pieces.King;
import com.chess.engine.classic.pieces.Rook;
import com.chess.engine.classic.pieces.Queen;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.pieces.Bishop;
import com.chess.engine.classic.pieces.Knight;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.player.WhitePlayer;
import com.chess.engine.classic.player.BlackPlayer;
import com.chess.engine.classic.board.Move.MoveFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableList;

/**
 * Chess board.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class Board implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3667858353401638001L;

	/** The game board. */
	private final List<Tile> gameBoard;

	/** The white pieces. */
	private final Collection<Piece> whitePieces;

	/** The black pieces. */
	private final Collection<Piece> blackPieces;

	/** The white player. */
	private final WhitePlayer whitePlayer;

	/** The black player. */
	private final BlackPlayer blackPlayer;

	/** The current player. */
	private final Player currentPlayer;

	/** The en passant pawn. */
	private final Pawn enPassantPawn;

	/** The transition move. */
	private final Move transitionMove;

	/** The Constant STANDARD_BOARD. */
	private static final Board STANDARD_BOARD = createStandardBoardImpl();

	/**
	 * Instantiates a new board.
	 *
	 * @param builder
	 *            the builder
	 */
	private Board(final Builder builder) {
		this.gameBoard = createGameBoard(builder);
		this.whitePieces = calculateActivePieces(builder, Alliance.WHITE);
		this.blackPieces = calculateActivePieces(builder, Alliance.BLACK);
		this.enPassantPawn = builder.enPassantPawn;
		final Collection<Move> whiteStandardMoves = calculateLegalMoves(this.whitePieces);
		final Collection<Move> blackStandardMoves = calculateLegalMoves(this.blackPieces);
		this.whitePlayer = new WhitePlayer(this, whiteStandardMoves, blackStandardMoves);
		this.blackPlayer = new BlackPlayer(this, whiteStandardMoves, blackStandardMoves);
		this.currentPlayer = builder.nextMoveMaker.choosePlayerByAlliance(this.whitePlayer, this.blackPlayer);
		this.transitionMove = builder.transitionMove != null ? builder.transitionMove : MoveFactory.getNullMove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			final String tileText = prettyPrint(this.gameBoard.get(i));
			builder.append(String.format("%3s", tileText));
			if ((i + 1) % 8 == 0) {
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	/**
	 * Pretty print.
	 *
	 * @param tile
	 *            the tile
	 * @return the string
	 */
	private static String prettyPrint(final Tile tile) {
		if (tile.isTileOccupied()) {
			return tile.getPiece().getPieceAllegiance().isBlack() ? tile.toString().toLowerCase() : tile.toString();
		}
		return tile.toString();
	}

	/**
	 * Gets the black pieces.
	 *
	 * @return the black pieces
	 */
	public Collection<Piece> getBlackPieces() {
		return this.blackPieces;
	}

	/**
	 * Gets the white pieces.
	 *
	 * @return the white pieces
	 */
	public Collection<Piece> getWhitePieces() {
		return this.whitePieces;
	}

	/**
	 * Gets the all pieces.
	 *
	 * @return the all pieces
	 */
	public Iterable<Piece> getAllPieces() {
		return Iterables.unmodifiableIterable(Iterables.concat(this.whitePieces, this.blackPieces));
	}

	/**
	 * Gets the all legal moves.
	 *
	 * @return the all legal moves
	 */
	public Iterable<Move> getAllLegalMoves() {
		return Iterables.unmodifiableIterable(
				Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
	}

	/**
	 * White player.
	 *
	 * @return the white player
	 */
	public WhitePlayer whitePlayer() {
		return this.whitePlayer;
	}

	/**
	 * Black player.
	 *
	 * @return the black player
	 */
	public BlackPlayer blackPlayer() {
		return this.blackPlayer;
	}

	/**
	 * Current player.
	 *
	 * @return the player
	 */
	public Player currentPlayer() {
		return this.currentPlayer;
	}

	/**
	 * Gets the tile.
	 *
	 * @param coordinate
	 *            the coordinate
	 * @return the tile
	 */
	public Tile getTile(final int coordinate) {
		return this.gameBoard.get(coordinate);
	}

	/**
	 * Gets the game board.
	 *
	 * @return the game board
	 */
	public List<Tile> getGameBoard() {
		return this.gameBoard;
	}

	/**
	 * Gets the en passant pawn.
	 *
	 * @return the en passant pawn
	 */
	public Pawn getEnPassantPawn() {
		return this.enPassantPawn;
	}

	/**
	 * Gets the transition move.
	 *
	 * @return the transition move
	 */
	public Move getTransitionMove() {
		return this.transitionMove;
	}

	/**
	 * Creates the standard board.
	 *
	 * @return the board
	 */
	public static Board createStandardBoard() {
		return STANDARD_BOARD;
	}

	/**
	 * Creates the standard board ýmpl.
	 *
	 * @return the board
	 */
	private static Board createStandardBoardImpl() {
		final Builder builder = new Builder();
		// Black Layout
		builder.setPiece(new Rook(Alliance.BLACK, 0));
		builder.setPiece(new Knight(Alliance.BLACK, 1));
		builder.setPiece(new Bishop(Alliance.BLACK, 2));
		builder.setPiece(new Queen(Alliance.BLACK, 3));
		builder.setPiece(new King(Alliance.BLACK, 4, true, true));
		builder.setPiece(new Bishop(Alliance.BLACK, 5));
		builder.setPiece(new Knight(Alliance.BLACK, 6));
		builder.setPiece(new Rook(Alliance.BLACK, 7));
		builder.setPiece(new Pawn(Alliance.BLACK, 8));
		builder.setPiece(new Pawn(Alliance.BLACK, 9));
		builder.setPiece(new Pawn(Alliance.BLACK, 10));
		builder.setPiece(new Pawn(Alliance.BLACK, 11));
		builder.setPiece(new Pawn(Alliance.BLACK, 12));
		builder.setPiece(new Pawn(Alliance.BLACK, 13));
		builder.setPiece(new Pawn(Alliance.BLACK, 14));
		builder.setPiece(new Pawn(Alliance.BLACK, 15));
		// White Layout
		builder.setPiece(new Pawn(Alliance.WHITE, 48));
		builder.setPiece(new Pawn(Alliance.WHITE, 49));
		builder.setPiece(new Pawn(Alliance.WHITE, 50));
		builder.setPiece(new Pawn(Alliance.WHITE, 51));
		builder.setPiece(new Pawn(Alliance.WHITE, 52));
		builder.setPiece(new Pawn(Alliance.WHITE, 53));
		builder.setPiece(new Pawn(Alliance.WHITE, 54));
		builder.setPiece(new Pawn(Alliance.WHITE, 55));
		builder.setPiece(new Rook(Alliance.WHITE, 56));
		builder.setPiece(new Knight(Alliance.WHITE, 57));
		builder.setPiece(new Bishop(Alliance.WHITE, 58));
		builder.setPiece(new Queen(Alliance.WHITE, 59));
		builder.setPiece(new King(Alliance.WHITE, 60, true, true));
		builder.setPiece(new Bishop(Alliance.WHITE, 61));
		builder.setPiece(new Knight(Alliance.WHITE, 62));
		builder.setPiece(new Rook(Alliance.WHITE, 63));
		// white to move
		builder.setMoveMaker(Alliance.WHITE);
		// build the board
		return builder.build();
	}

	/**
	 * Creates the game board.
	 *
	 * @param boardBuilder
	 *            the board builder
	 * @return the list
	 */
	private static List<Tile> createGameBoard(final Builder boardBuilder) {
		final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			tiles[i] = Tile.createTile(i, boardBuilder.boardConfig.get(i));
		}
		return ImmutableList.copyOf(tiles);
	}

	/**
	 * Calculate legal moves.
	 *
	 * @param pieces
	 *            the pieces
	 * @return the collection
	 */
	private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
		final List<Move> legalMoves = new ArrayList<>(35);
		for (final Piece piece : pieces) {
			legalMoves.addAll(piece.calculateLegalMoves(this));
		}
		return legalMoves;
	}

	/**
	 * Calculate active pieces.
	 *
	 * @param builder
	 *            the builder
	 * @param alliance
	 *            the alliance
	 * @return the collection
	 */
	private static Collection<Piece> calculateActivePieces(final Builder builder, final Alliance alliance) {
		final List<Piece> activePieces = new ArrayList<>(16);
		for (final Piece piece : builder.boardConfig.values()) {
			if (piece.getPieceAllegiance() == alliance) {
				activePieces.add(piece);
			}
		}
		return ImmutableList.copyOf(activePieces);
	}

	/**
	 * The Class Builder.
	 */
	public static class Builder {

		/** The board config. */
		Map<Integer, Piece> boardConfig;

		/** The next move maker. */
		Alliance nextMoveMaker;

		/** The en passant pawn. */
		Pawn enPassantPawn;

		/** The transition move. */
		Move transitionMove;

		/**
		 * Instantiates a new builder.
		 */
		public Builder() {
			this.boardConfig = new HashMap<>(33, 1.0f);
		}

		/**
		 * Sets the piece.
		 *
		 * @param piece
		 *            the piece
		 * @return the builder
		 */
		public Builder setPiece(final Piece piece) {
			this.boardConfig.put(piece.getPiecePosition(), piece);
			return this;
		}

		/**
		 * Sets the move maker.
		 *
		 * @param nextMoveMaker
		 *            the next move maker
		 * @return the builder
		 */
		public Builder setMoveMaker(final Alliance nextMoveMaker) {
			this.nextMoveMaker = nextMoveMaker;
			return this;
		}

		/**
		 * Sets the en passant pawn.
		 *
		 * @param enPassantPawn
		 *            the en passant pawn
		 * @return the builder
		 */
		public Builder setEnPassantPawn(final Pawn enPassantPawn) {
			this.enPassantPawn = enPassantPawn;
			return this;
		}

		/**
		 * Sets the move transition.
		 *
		 * @param transitionMove
		 *            the transition move
		 * @return the builder
		 */
		public Builder setMoveTransition(final Move transitionMove) {
			this.transitionMove = transitionMove;
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the board
		 */
		public Board build() {
			return new Board(this);
		}
	}
}