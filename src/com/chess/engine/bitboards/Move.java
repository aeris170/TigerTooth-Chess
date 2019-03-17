package com.chess.engine.bitboards;

import com.chess.engine.bitboards.BitBoard.Piece;

/**
 * Documentation will not be provided for this class for this class is for
 * testing purposes and has no effect on how the program works.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class Move {

	/** The current location. */
	final int currentLocation;

	/** The destination location. */
	final int destinationLocation;

	/** The moved piece. */
	final Piece movedPiece;

	/**
	 * Instantiates a new move.
	 *
	 * @param currentLocation
	 *            the current location
	 * @param destinationLocation
	 *            the destination location
	 * @param moved
	 *            the moved
	 */
	public Move(final int currentLocation, final int destinationLocation, final Piece moved) {
		this.currentLocation = currentLocation;
		this.destinationLocation = destinationLocation;
		this.movedPiece = moved;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return BitBoard.getPositionAtCoordinate(this.currentLocation) + "-"
				+ BitBoard.getPositionAtCoordinate(this.destinationLocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.movedPiece.hashCode() + this.currentLocation + this.destinationLocation;
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
		return (this.movedPiece == otherMove.getMovedPiece())
				&& (this.currentLocation == otherMove.getCurrentLocation())
				&& (this.destinationLocation == otherMove.getDestinationLocation());
	}

	/**
	 * Gets the destination location.
	 *
	 * @return the destination location
	 */
	public int getDestinationLocation() {
		return this.destinationLocation;
	}

	/**
	 * Gets the current location.
	 *
	 * @return the current location
	 */
	public int getCurrentLocation() {
		return this.currentLocation;
	}

	/**
	 * Gets the moved piece.
	 *
	 * @return the moved piece
	 */
	public Piece getMovedPiece() {
		return this.movedPiece;
	}
}