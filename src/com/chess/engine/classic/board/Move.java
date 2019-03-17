package com.chess.engine.classic.board;

import java.io.Serializable;

import com.chess.engine.classic.pieces.Pawn;
import com.chess.engine.classic.pieces.Rook;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.board.Board.Builder;

/**
 * A chess move, in chess there are types of moves defined explicitly, for
 * example the pawn can move 2 squares on it's first turn. On this instance I
 * named that move as "PawnJump". Normal moves are named as "MajorMove" etc.
 * etc.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public abstract class Move implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4378451282472583344L;

	/** The board. */
	protected final Board board;

	/** The destination coordinate. */
	protected final int destinationCoordinate;

	/** The moved piece. */
	protected final Piece movedPiece;

	/** The is first move. */
	protected final boolean isFirstMove;

	/**
	 * Instantiates a new move.
	 *
	 * @param board
	 *            the board
	 * @param pieceMoved
	 *            the piece moved
	 * @param destinationCoordinate
	 *            the destination coordinate
	 */
	private Move(final Board board, final Piece pieceMoved, final int destinationCoordinate) {
		this.board = board;
		this.destinationCoordinate = destinationCoordinate;
		this.movedPiece = pieceMoved;
		this.isFirstMove = pieceMoved.isFirstMove();
	}

	/**
	 * Instantiates a new move.
	 *
	 * @param board
	 *            the board
	 * @param destinationCoordinate
	 *            the destination coordinate
	 */
	private Move(final Board board, final int destinationCoordinate) {
		this.board = board;
		this.destinationCoordinate = destinationCoordinate;
		this.movedPiece = null;
		this.isFirstMove = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + this.destinationCoordinate;
		result = 31 * result + this.movedPiece.hashCode();
		result = 31 * result + this.movedPiece.getPiecePosition();
		result = result + (isFirstMove ? 1 : 0);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Move)) {
			return false;
		}
		final Move otherMove = (Move) other;
		return getCurrentCoordinate() == otherMove.getCurrentCoordinate()
				&& getDestinationCoordinate() == otherMove.getDestinationCoordinate()
				&& getMovedPiece().equals(otherMove.getMovedPiece());
	}

	/**
	 * Gets the board.
	 *
	 * @return the board
	 */
	public Board getBoard() {
		return this.board;
	}

	/**
	 * Gets the current coordinate.
	 *
	 * @return the current coordinate
	 */
	public int getCurrentCoordinate() {
		return this.movedPiece.getPiecePosition();
	}

	/**
	 * Gets the destination coordinate.
	 *
	 * @return the destination coordinate
	 */
	public int getDestinationCoordinate() {
		return this.destinationCoordinate;
	}

	/**
	 * Gets the moved piece.
	 *
	 * @return the moved piece
	 */
	public Piece getMovedPiece() {
		return this.movedPiece;
	}

	/**
	 * Checks if is attack.
	 *
	 * @return true, if is attack
	 */
	public boolean isAttack() {
		return false;
	}

	/**
	 * Checks if is castling move.
	 *
	 * @return true, if is castling move
	 */
	public boolean isCastlingMove() {
		return false;
	}

	/**
	 * Gets the attacked piece.
	 *
	 * @return the attacked piece
	 */
	public Piece getAttackedPiece() {
		return null;
	}

	/**
	 * Execute.
	 *
	 * @return the board
	 */
	public Board execute() {
		final Board.Builder builder = new Builder();
		for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
			if (!this.movedPiece.equals(piece)) {
				builder.setPiece(piece);
			}
		}
		for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
			builder.setPiece(piece);
		}
		builder.setPiece(this.movedPiece.movePiece(this));
		builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
		builder.setMoveTransition(this);
		return builder.build();
	}

	/**
	 * Undo.
	 *
	 * @return the board
	 */
	public Board undo() {
		final Board.Builder builder = new Builder();
		for (final Piece piece : this.board.getAllPieces()) {
			builder.setPiece(piece);
		}
		builder.setMoveMaker(this.board.currentPlayer().getAlliance());
		return builder.build();
	}

	/**
	 * Disambiguation file.
	 *
	 * @return the string
	 */
	public String disambiguationFile() {
		for (final Move move : this.board.currentPlayer().getLegalMoves()) {
			if (move.getDestinationCoordinate() == this.destinationCoordinate && !this.equals(move)
					&& this.movedPiece.getPieceType().equals(move.getMovedPiece().getPieceType())) {
				return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1);
			}
		}
		return "";
	}

	/**
	 * The Enum MoveStatus.
	 */
	public enum MoveStatus {

		/** The done. */
		DONE {
			@Override
			public boolean isDone() {
				return true;
			}
		},

		/** The ýllegal move. */
		ILLEGAL_MOVE {
			@Override
			public boolean isDone() {
				return false;
			}
		},

		/** The leaves player ýn check. */
		LEAVES_PLAYER_IN_CHECK {
			@Override
			public boolean isDone() {
				return false;
			}
		};

		/**
		 * Checks if is done.
		 *
		 * @return true, if is done
		 */
		public abstract boolean isDone();

	}

	/**
	 * The Class PawnPromotion.
	 */
	public static class PawnPromotion extends PawnMove {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -7908468201954190910L;

		/** The decorated move. */
		final Move decoratedMove;

		/** The promoted pawn. */
		final Pawn promotedPawn;

		/** The promotion piece. */
		final Piece promotionPiece;

		/**
		 * Instantiates a new pawn promotion.
		 *
		 * @param decoratedMove
		 *            the decorated move
		 * @param promotionPiece
		 *            the promotion piece
		 */
		public PawnPromotion(final Move decoratedMove, final Piece promotionPiece) {
			super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
			this.decoratedMove = decoratedMove;
			this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
			this.promotionPiece = promotionPiece;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#hashCode()
		 */
		@Override
		public int hashCode() {
			return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.chess.engine.classic.board.Move.PawnMove#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnPromotion && (super.equals(other));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#execute()
		 */
		@Override
		public Board execute() {
			final Board pawnMovedBoard = this.decoratedMove.execute();
			final Board.Builder builder = new Builder();
			for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()) {
				if (!this.promotedPawn.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			builder.setPiece(this.promotionPiece.movePiece(this));
			builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAlliance());
			builder.setMoveTransition(this);
			return builder.build();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#isAttack()
		 */
		@Override
		public boolean isAttack() {
			return this.decoratedMove.isAttack();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#getAttackedPiece()
		 */
		@Override
		public Piece getAttackedPiece() {
			return this.decoratedMove.getAttackedPiece();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move.PawnMove#toString()
		 */
		@Override
		public String toString() {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()) + "-"
					+ BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate) + "="
					+ this.promotionPiece.getPieceType();
		}
	}

	/**
	 * The Class MajorMove.
	 */
	public static class MajorMove extends Move {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 445534896057257949L;

		/**
		 * Instantiates a new major move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 */
		public MajorMove(final Board board, final Piece pieceMoved, final int destinationCoordinate) {
			super(board, pieceMoved, destinationCoordinate);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof MajorMove && super.equals(other);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return movedPiece.getPieceType().toString() + disambiguationFile()
					+ BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
		}
	}

	/**
	 * The Class MajorAttackMove.
	 */
	public static class MajorAttackMove extends AttackMove {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 9081884728901945586L;

		/**
		 * Instantiates a new major attack move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param pieceAttacked
		 *            the piece attacked
		 */
		public MajorAttackMove(final Board board, final Piece pieceMoved, final int destinationCoordinate,
				final Piece pieceAttacked) {
			super(board, pieceMoved, destinationCoordinate, pieceAttacked);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move.AttackMove#equals(java.lang.
		 * Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof MajorAttackMove && super.equals(other);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return movedPiece.getPieceType() + disambiguationFile() + "x"
					+ BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
		}
	}

	/**
	 * The Class PawnMove.
	 */
	public static class PawnMove extends Move {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 7034695000638754893L;

		/**
		 * Instantiates a new pawn move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 */
		public PawnMove(final Board board, final Piece pieceMoved, final int destinationCoordinate) {
			super(board, pieceMoved, destinationCoordinate);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnMove && super.equals(other);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
		}
	}

	/**
	 * The Class PawnAttackMove.
	 */
	public static class PawnAttackMove extends AttackMove {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 7395637766562014060L;

		/**
		 * Instantiates a new pawn attack move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param pieceAttacked
		 *            the piece attacked
		 */
		public PawnAttackMove(final Board board, final Piece pieceMoved, final int destinationCoordinate,
				final Piece pieceAttacked) {
			super(board, pieceMoved, destinationCoordinate, pieceAttacked);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move.AttackMove#equals(java.lang.
		 * Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnAttackMove && super.equals(other);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0, 1) + "x"
					+ BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
		}
	}

	/**
	 * The Class PawnEnPassantAttack.
	 */
	public static class PawnEnPassantAttack extends PawnAttackMove {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -9072543735763347618L;

		/**
		 * Instantiates a new pawn en passant attack.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param pieceAttacked
		 *            the piece attacked
		 */
		public PawnEnPassantAttack(final Board board, final Piece pieceMoved, final int destinationCoordinate,
				final Piece pieceAttacked) {
			super(board, pieceMoved, destinationCoordinate, pieceAttacked);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.chess.engine.classic.board.Move.PawnAttackMove#equals(java.lang.
		 * Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnEnPassantAttack && super.equals(other);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#execute()
		 */
		@Override
		public Board execute() {
			final Board.Builder builder = new Builder();
			for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if (!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				if (!piece.equals(this.getAttackedPiece())) {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			builder.setMoveTransition(this);
			return builder.build();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#undo()
		 */
		@Override
		public Board undo() {
			final Board.Builder builder = new Builder();
			for (final Piece piece : this.board.getAllPieces()) {
				builder.setPiece(piece);
			}
			builder.setEnPassantPawn((Pawn) this.getAttackedPiece());
			builder.setMoveMaker(this.board.currentPlayer().getAlliance());
			return builder.build();
		}
	}

	/**
	 * The Class PawnJump.
	 */
	public static class PawnJump extends Move {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -1225447648947647218L;

		/**
		 * Instantiates a new pawn jump.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 */
		public PawnJump(final Board board, final Pawn pieceMoved, final int destinationCoordinate) {
			super(board, pieceMoved, destinationCoordinate);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object other) {
			return this == other || other instanceof PawnJump && super.equals(other);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#execute()
		 */
		@Override
		public Board execute() {
			final Board.Builder builder = new Builder();
			for (final Piece piece : this.board.currentPlayer().getActivePieces()) {
				if (!this.movedPiece.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(piece);
			}
			final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
			builder.setPiece(movedPawn);
			builder.setEnPassantPawn(movedPawn);
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			builder.setMoveTransition(this);
			return builder.build();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(this.destinationCoordinate);
		}
	}

	/**
	 * The Class CastleMove.
	 */
	static abstract class CastleMove extends Move {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 3495568536618250565L;

		/** The castle rook. */
		final Rook castleRook;

		/** The castle rook start. */
		final int castleRookStart;

		/** The castle rook destination. */
		final int castleRookDestination;

		/**
		 * Instantiates a new castle move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param castleRook
		 *            the castle rook
		 * @param castleRookStart
		 *            the castle rook start
		 * @param castleRookDestination
		 *            the castle rook destination
		 */
		CastleMove(final Board board, final Piece pieceMoved, final int destinationCoordinate, final Rook castleRook,
				final int castleRookStart, final int castleRookDestination) {
			super(board, pieceMoved, destinationCoordinate);
			this.castleRook = castleRook;
			this.castleRookStart = castleRookStart;
			this.castleRookDestination = castleRookDestination;
		}

		/**
		 * Gets the castle rook.
		 *
		 * @return the castle rook
		 */
		public Rook getCastleRook() {
			return this.castleRook;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#isCastlingMove()
		 */
		@Override
		public boolean isCastlingMove() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#execute()
		 */
		@Override
		public Board execute() {
			final Board.Builder builder = new Builder();
			for (final Piece piece : this.board.getAllPieces()) {
				if (!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
					builder.setPiece(piece);
				}
			}
			builder.setPiece(this.movedPiece.movePiece(this));
			// calling movePiece here doesn't work, we need to explicitly create
			// a new Rook
			builder.setPiece(new Rook(this.castleRook.getPieceAllegiance(), this.castleRookDestination, false));
			builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());
			builder.setMoveTransition(this);
			return builder.build();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + this.castleRook.hashCode();
			result = prime * result + this.castleRookDestination;
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof CastleMove)) {
				return false;
			}
			final CastleMove otherCastleMove = (CastleMove) other;
			return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
		}
	}

	/**
	 * The Class KingSideCastleMove.
	 */
	public static class KingSideCastleMove extends CastleMove {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 145484327531677350L;

		/**
		 * Instantiates a new king side castle move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param castleRook
		 *            the castle rook
		 * @param castleRookStart
		 *            the castle rook start
		 * @param castleRookDestination
		 *            the castle rook destination
		 */
		public KingSideCastleMove(final Board board, final Piece pieceMoved, final int destinationCoordinate,
				final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
			super(board, pieceMoved, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move.CastleMove#equals(java.lang.
		 * Object)
		 */
		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof KingSideCastleMove)) {
				return false;
			}
			final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
			return super.equals(otherKingSideCastleMove)
					&& this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "O-O";
		}
	}

	/**
	 * The Class QueenSideCastleMove.
	 */
	public static class QueenSideCastleMove extends CastleMove {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -4848212028142505776L;

		/**
		 * Instantiates a new queen side castle move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param castleRook
		 *            the castle rook
		 * @param castleRookStart
		 *            the castle rook start
		 * @param rookCastleDestination
		 *            the rook castle destination
		 */
		public QueenSideCastleMove(final Board board, final Piece pieceMoved, final int destinationCoordinate,
				final Rook castleRook, final int castleRookStart, final int rookCastleDestination) {
			super(board, pieceMoved, destinationCoordinate, castleRook, castleRookStart, rookCastleDestination);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move.CastleMove#equals(java.lang.
		 * Object)
		 */
		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof QueenSideCastleMove)) {
				return false;
			}
			final QueenSideCastleMove otherQueenSideCastleMove = (QueenSideCastleMove) other;
			return super.equals(otherQueenSideCastleMove)
					&& this.castleRook.equals(otherQueenSideCastleMove.getCastleRook());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "O-O-O";
		}
	}

	/**
	 * The Class AttackMove.
	 */
	static abstract class AttackMove extends Move {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -6378615077392953483L;

		/** The attacked piece. */
		private final Piece attackedPiece;

		/**
		 * Instantiates a new attack move.
		 *
		 * @param board
		 *            the board
		 * @param pieceMoved
		 *            the piece moved
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @param pieceAttacked
		 *            the piece attacked
		 */
		AttackMove(final Board board, final Piece pieceMoved, final int destinationCoordinate,
				final Piece pieceAttacked) {
			super(board, pieceMoved, destinationCoordinate);
			this.attackedPiece = pieceAttacked;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.attackedPiece.hashCode() + super.hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof AttackMove)) {
				return false;
			}
			final AttackMove otherAttackMove = (AttackMove) other;
			return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#getAttackedPiece()
		 */
		@Override
		public Piece getAttackedPiece() {
			return this.attackedPiece;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#isAttack()
		 */
		@Override
		public boolean isAttack() {
			return true;
		}
	}

	/**
	 * The Class NullMove.
	 */
	private static class NullMove extends Move {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -6471155652813632545L;

		/**
		 * Instantiates a new null move.
		 */
		private NullMove() {
			super(null, -1);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#getCurrentCoordinate()
		 */
		@Override
		public int getCurrentCoordinate() {
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#getDestinationCoordinate()
		 */
		@Override
		public int getDestinationCoordinate() {
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.chess.engine.classic.board.Move#execute()
		 */
		@Override
		public Board execute() {
			throw new RuntimeException("cannot execute null move!");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Null Move";
		}
	}

	/**
	 * A factory for creating Move objects.
	 */
	public static class MoveFactory {

		/** The Constant NULL_MOVE. */
		private static final Move NULL_MOVE = new NullMove();

		/**
		 * Instantiates a new move factory.
		 */
		private MoveFactory() {
			throw new RuntimeException("Not instantiatable!");
		}

		/**
		 * Gets the null move.
		 *
		 * @return the null move
		 */
		public static Move getNullMove() {
			return NULL_MOVE;
		}

		/**
		 * Creates a new Move object.
		 *
		 * @param board
		 *            the board
		 * @param currentCoordinate
		 *            the current coordinate
		 * @param destinationCoordinate
		 *            the destination coordinate
		 * @return the move
		 */
		public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate) {
			for (final Move move : board.getAllLegalMoves()) {
				if (move.getCurrentCoordinate() == currentCoordinate
						&& move.getDestinationCoordinate() == destinationCoordinate) {
					return move;
				}
			}
			return NULL_MOVE;
		}
	}
}