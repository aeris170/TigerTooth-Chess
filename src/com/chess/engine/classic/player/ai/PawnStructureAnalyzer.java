package com.chess.engine.classic.player.ai;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ArrayListMultimap;

/**
 * Documentation will not be provided for this class for this class is an A.I.
 * implementation class.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class PawnStructureAnalyzer {

	/** The Constant INSTANCE. */
	private static final PawnStructureAnalyzer INSTANCE = new PawnStructureAnalyzer();

	/** The Constant ISOLATED_PAWN_PENALTY. */
	public static final int ISOLATED_PAWN_PENALTY = -15;

	/** The Constant DOUBLED_PAWN_PENALTY. */
	public static final int DOUBLED_PAWN_PENALTY = -35;

	/**
	 * Instantiates a new pawn structure analyzer.
	 */
	private PawnStructureAnalyzer() {
	}

	/**
	 * Gets the.
	 *
	 * @return the pawn structure analyzer
	 */
	public static PawnStructureAnalyzer get() {
		return INSTANCE;
	}

	/**
	 * Ýsolated pawn penalty.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	public int isolatedPawnPenalty(final Player player) {
		return calculateIsolatedPawnPenalty(createPawnColumnTable(calculatePlayerPawns(player)));
	}

	/**
	 * Doubled pawn penalty.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	public int doubledPawnPenalty(final Player player) {
		return calculatePawnColumnStack(createPawnColumnTable(calculatePlayerPawns(player)));
	}

	/**
	 * Pawn structure score.
	 *
	 * @param player
	 *            the player
	 * @return the int
	 */
	public int pawnStructureScore(final Player player) {
		final ListMultimap<Integer, Piece> pawnsOnColumnTable = createPawnColumnTable(calculatePlayerPawns(player));
		return calculatePawnColumnStack(pawnsOnColumnTable) + calculateIsolatedPawnPenalty(pawnsOnColumnTable);
	}

	/**
	 * Calculate player pawns.
	 *
	 * @param player
	 *            the player
	 * @return the collection
	 */
	private static Collection<Piece> calculatePlayerPawns(final Player player) {
		final List<Piece> playerPawnLocations = new ArrayList<>(8);
		for (final Piece piece : player.getActivePieces()) {
			if (piece.getPieceType().isPawn()) {
				playerPawnLocations.add(piece);
			}
		}
		return ImmutableList.copyOf(playerPawnLocations);
	}

	/**
	 * Calculate pawn column stack.
	 *
	 * @param pawnsOnColumnTable
	 *            the pawns on column table
	 * @return the int
	 */
	private static int calculatePawnColumnStack(final ListMultimap<Integer, Piece> pawnsOnColumnTable) {
		int pawnStackPenalty = 0;
		for (final Integer i : pawnsOnColumnTable.keySet()) {
			int pawnStackSize = pawnsOnColumnTable.get(i).size();
			if (pawnStackSize > 1) {
				pawnStackPenalty += pawnStackSize;
			}
		}
		return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
	}

	/**
	 * Calculate ýsolated pawn penalty.
	 *
	 * @param pawnsOnColumnTable
	 *            the pawns on column table
	 * @return the int
	 */
	private static int calculateIsolatedPawnPenalty(final ListMultimap<Integer, Piece> pawnsOnColumnTable) {
		int numIsolatedPawns = 0;
		for (final Integer i : pawnsOnColumnTable.keySet()) {
			if ((pawnsOnColumnTable.get(i - 1).isEmpty() && pawnsOnColumnTable.get(i + 1).isEmpty())) {
				numIsolatedPawns += pawnsOnColumnTable.get(i).size();
			}
		}
		return numIsolatedPawns * ISOLATED_PAWN_PENALTY;
	}

	/**
	 * Creates the pawn column table.
	 *
	 * @param playerPawns
	 *            the player pawns
	 * @return the list multimap
	 */
	private static ListMultimap<Integer, Piece> createPawnColumnTable(final Collection<Piece> playerPawns) {
		final ListMultimap<Integer, Piece> table = ArrayListMultimap.create(8, 5);
		for (final Piece playerPawn : playerPawns) {
			table.put(playerPawn.getPiecePosition() % 8, playerPawn);
		}
		return table;
	}
}