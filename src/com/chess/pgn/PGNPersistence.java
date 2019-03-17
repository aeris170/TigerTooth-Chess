package com.chess.pgn;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.player.Player;

/**
 * PGN processing interface.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public interface PGNPersistence {

	/**
	 * Persist game.
	 *
	 * @param game
	 *            the game
	 */
	void persistGame(Game game);

	/**
	 * Gets the next best move.
	 *
	 * @param board
	 *            the board
	 * @param player
	 *            the player
	 * @param gameText
	 *            the game text
	 * @return the next best move
	 */
	Move getNextBestMove(Board board, Player player, String gameText);
}