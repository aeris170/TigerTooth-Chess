package com.chess.engine.classic.pieces;

import java.util.Collection;
import java.io.Serializable;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;

/**
 * A chess piece. Abstract because all pieces are unique in chess unlike other
 * games like backgammon.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public abstract class Piece implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5451172267023066689L;

	/** The piece type. */
	final PieceType pieceType;

	/** The piece alliance. */
	final Alliance pieceAlliance;

	/** The piece position. */
	final int piecePosition;

	/** The is first move. */
	private final boolean isFirstMove;

	/** The cached hash code. */
	private final int cachedHashCode;

	/**
	 * Instantiates a new piece.
	 *
	 * @param type
	 *            the type
	 * @param alliance
	 *            the alliance
	 * @param piecePosition
	 *            the piece position
	 * @param isFirstMove
	 *            the is first move
	 */
	Piece(final PieceType type, final Alliance alliance, final int piecePosition, final boolean isFirstMove) {
		this.pieceType = type;
		this.piecePosition = piecePosition;
		this.pieceAlliance = alliance;
		this.isFirstMove = isFirstMove;
		this.cachedHashCode = computeHashCode();
	}

	/**
	 * Gets the piece type.
	 *
	 * @return the piece type
	 */
	public PieceType getPieceType() {
		return this.pieceType;
	}

	/**
	 * Gets the piece allegiance.
	 *
	 * @return the piece allegiance
	 */
	public Alliance getPieceAllegiance() {
		return this.pieceAlliance;
	}

	/**
	 * Gets the piece position.
	 *
	 * @return the piece position
	 */
	public int getPiecePosition() {
		return this.piecePosition;
	}

	/**
	 * Checks if is first move.
	 *
	 * @return true, if is first move
	 */
	public boolean isFirstMove() {
		return this.isFirstMove;
	}

	/**
	 * Gets the piece value.
	 *
	 * @return the piece value
	 */
	public int getPieceValue() {
		return this.pieceType.getPieceValue();
	}

	/**
	 * Location bonus.
	 *
	 * @return the int
	 */
	public abstract int locationBonus();

	/**
	 * Move piece.
	 *
	 * @param move
	 *            the move
	 * @return the piece
	 */
	public abstract Piece movePiece(Move move);

	/**
	 * Calculate legal moves.
	 *
	 * @param board
	 *            the board
	 * @return the collection
	 */
	public abstract Collection<Move> calculateLegalMoves(final Board board);

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
		if (!(other instanceof Piece)) {
			return false;
		}
		final Piece otherPiece = (Piece) other;
		return piecePosition == otherPiece.piecePosition && pieceType == otherPiece.pieceType
				&& pieceAlliance == otherPiece.pieceAlliance && isFirstMove == otherPiece.isFirstMove;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	/**
	 * Compute hash code.
	 *
	 * @return the int
	 */
	private int computeHashCode() {
		int result = pieceType.hashCode();
		result = 31 * result + pieceAlliance.hashCode();
		result = 31 * result + piecePosition;
		result = 31 * result + (isFirstMove ? 1 : 0);
		return result;
	}

	/**
	 * The Enum PieceType.
	 */
	public enum PieceType {

		/** The pawn. */
		PAWN(100, "P") {
			@Override
			public boolean isPawn() {
				return true;
			}

			@Override
			public boolean isBishop() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

			@Override
			public boolean isKing() {
				return false;
			}
		},

		/** The knýght. */
		KNIGHT(320, "N") {
			@Override
			public boolean isPawn() {
				return false;
			}

			@Override
			public boolean isBishop() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

			@Override
			public boolean isKing() {
				return false;
			}
		},

		/** The býshop. */
		BISHOP(350, "B") {
			@Override
			public boolean isPawn() {
				return false;
			}

			@Override
			public boolean isBishop() {
				return true;
			}

			@Override
			public boolean isRook() {
				return false;
			}

			@Override
			public boolean isKing() {
				return false;
			}
		},

		/** The rook. */
		ROOK(500, "R") {
			@Override
			public boolean isPawn() {
				return false;
			}

			@Override
			public boolean isBishop() {
				return false;
			}

			@Override
			public boolean isRook() {
				return true;
			}

			@Override
			public boolean isKing() {
				return false;
			}
		},

		/** The queen. */
		QUEEN(900, "Q") {
			@Override
			public boolean isPawn() {
				return false;
			}

			@Override
			public boolean isBishop() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

			@Override
			public boolean isKing() {
				return false;
			}
		},

		/** The kýng. */
		KING(20000, "K") {
			@Override
			public boolean isPawn() {
				return false;
			}

			@Override
			public boolean isBishop() {
				return false;
			}

			@Override
			public boolean isRook() {
				return false;
			}

			@Override
			public boolean isKing() {
				return true;
			}
		};

		/** The value. */
		private final int value;

		/** The piece name. */
		private final String pieceName;

		/**
		 * Gets the piece value.
		 *
		 * @return the piece value
		 */
		public int getPieceValue() {
			return this.value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.pieceName;
		}

		/**
		 * Instantiates a new piece type.
		 *
		 * @param val
		 *            the val
		 * @param pieceName
		 *            the piece name
		 */
		PieceType(final int val, final String pieceName) {
			this.value = val;
			this.pieceName = pieceName;
		}

		/**
		 * Checks if is pawn.
		 *
		 * @return true, if is pawn
		 */
		public abstract boolean isPawn();

		/**
		 * Checks if is bishop.
		 *
		 * @return true, if is bishop
		 */
		public abstract boolean isBishop();

		/**
		 * Checks if is rook.
		 *
		 * @return true, if is rook
		 */
		public abstract boolean isRook();

		/**
		 * Checks if is king.
		 *
		 * @return true, if is king
		 */
		public abstract boolean isKing();
	}
}