package com.chess.pgn;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.pieces.Pawn;
import com.chess.engine.classic.pieces.King;
import com.chess.engine.classic.pieces.Rook;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Queen;
import com.chess.engine.classic.pieces.Bishop;
import com.chess.engine.classic.pieces.Knight;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Board.Builder;

/**
 * The class responsible for FEN(Forsythe-Edwards Notation) operations.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class FenUtilities {

	/**
	 * Instantiates a new fen utilities.
	 */
	private FenUtilities() {
		throw new RuntimeException("Not Instantiable!");
	}

	/**
	 * Creates the game from FEN.
	 *
	 * @param fenString
	 *            the fen string
	 * @return the board
	 */
	public static Board createGameFromFEN(final String fenString) {
		return parseFEN(fenString);
	}

	/**
	 * Creates the FEN from game.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	public static String createFENFromGame(final Board board) {
		return calculateBoardText(board) + " " + calculateCurrentPlayerText(board) + " " + calculateCastleText(board)
				+ " " + calculateEnPassantSquare(board) + " " + "0 1";
	}

	/**
	 * Parses the FEN.
	 *
	 * @param fenString
	 *            the fen string
	 * @return the board
	 */
	private static Board parseFEN(final String fenString) {
		final String[] fenPartitions = fenString.trim().split(" ");
		final Builder builder = new Builder();
		final boolean whiteKingSideCastle = whiteKingSideCastle(fenPartitions[2]);
		final boolean whiteQueenSideCastle = whiteQueenSideCastle(fenPartitions[2]);
		final boolean blackKingSideCastle = blackKingSideCastle(fenPartitions[2]);
		final boolean blackQueenSideCastle = blackQueenSideCastle(fenPartitions[2]);
		final String gameConfiguration = fenPartitions[0];
		final char[] boardTiles = gameConfiguration.replaceAll("/", "").replaceAll("8", "--------")
				.replaceAll("7", "-------").replaceAll("6", "------").replaceAll("5", "-----").replaceAll("4", "----")
				.replaceAll("3", "---").replaceAll("2", "--").replaceAll("1", "-").toCharArray();
		int i = 0;
		while (i < boardTiles.length) {
			switch (boardTiles[i]) {
			case 'r':
				builder.setPiece(new Rook(Alliance.BLACK, i));
				i++;
				break;
			case 'n':
				builder.setPiece(new Knight(Alliance.BLACK, i));
				i++;
				break;
			case 'b':
				builder.setPiece(new Bishop(Alliance.BLACK, i));
				i++;
				break;
			case 'q':
				builder.setPiece(new Queen(Alliance.BLACK, i));
				i++;
				break;
			case 'k':
				@SuppressWarnings("unused")
				final boolean isCastled = !blackKingSideCastle && !blackQueenSideCastle;
				builder.setPiece(new King(Alliance.BLACK, i, blackKingSideCastle, blackQueenSideCastle));
				i++;
				break;
			case 'p':
				builder.setPiece(new Pawn(Alliance.BLACK, i));
				i++;
				break;
			case 'R':
				builder.setPiece(new Rook(Alliance.WHITE, i));
				i++;
				break;
			case 'N':
				builder.setPiece(new Knight(Alliance.WHITE, i));
				i++;
				break;
			case 'B':
				builder.setPiece(new Bishop(Alliance.WHITE, i));
				i++;
				break;
			case 'Q':
				builder.setPiece(new Queen(Alliance.WHITE, i));
				i++;
				break;
			case 'K':
				builder.setPiece(new King(Alliance.WHITE, i, whiteKingSideCastle, whiteQueenSideCastle));
				i++;
				break;
			case 'P':
				builder.setPiece(new Pawn(Alliance.WHITE, i));
				i++;
				break;
			case '-':
				i++;
				break;
			default:
				throw new RuntimeException("Invalid FEN String " + gameConfiguration);
			}
		}
		builder.setMoveMaker(moveMaker(fenPartitions[1]));
		return builder.build();
	}

	/**
	 * Move maker.
	 *
	 * @param moveMakerString
	 *            the move maker string
	 * @return the alliance
	 */
	private static Alliance moveMaker(final String moveMakerString) {
		if (moveMakerString.equals("w")) {
			return Alliance.WHITE;
		} else if (moveMakerString.equals("b")) {
			return Alliance.BLACK;
		}
		throw new RuntimeException("Invalid FEN String " + moveMakerString);
	}

	/**
	 * White king side castle.
	 *
	 * @param fenCastleString
	 *            the fen castle string
	 * @return true, if successful
	 */
	private static boolean whiteKingSideCastle(final String fenCastleString) {
		return fenCastleString.contains("K");
	}

	/**
	 * White queen side castle.
	 *
	 * @param fenCastleString
	 *            the fen castle string
	 * @return true, if successful
	 */
	private static boolean whiteQueenSideCastle(final String fenCastleString) {
		return fenCastleString.contains("Q");
	}

	/**
	 * Black king side castle.
	 *
	 * @param fenCastleString
	 *            the fen castle string
	 * @return true, if successful
	 */
	private static boolean blackKingSideCastle(final String fenCastleString) {
		return fenCastleString.contains("k");
	}

	/**
	 * Black queen side castle.
	 *
	 * @param fenCastleString
	 *            the fen castle string
	 * @return true, if successful
	 */
	private static boolean blackQueenSideCastle(final String fenCastleString) {
		return fenCastleString.contains("q");
	}

	/**
	 * Calculate castle text.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	private static String calculateCastleText(final Board board) {
		final StringBuilder builder = new StringBuilder();
		if (board.whitePlayer().isKingSideCastleCapable()) {
			builder.append("K");
		}
		if (board.whitePlayer().isQueenSideCastleCapable()) {
			builder.append("Q");
		}
		if (board.blackPlayer().isKingSideCastleCapable()) {
			builder.append("k");
		}
		if (board.blackPlayer().isQueenSideCastleCapable()) {
			builder.append("q");
		}
		final String result = builder.toString();

		return result.isEmpty() ? "-" : result;
	}

	/**
	 * Calculate en passant square.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	private static String calculateEnPassantSquare(final Board board) {
		final Pawn enPassantPawn = board.getEnPassantPawn();
		if (enPassantPawn != null) {
			return BoardUtils.INSTANCE.getPositionAtCoordinate(
					enPassantPawn.getPiecePosition() + (8) * enPassantPawn.getPieceAllegiance().getOppositeDirection());
		}
		return "-";
	}

	/**
	 * Calculate board text.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	private static String calculateBoardText(final Board board) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
			final String tileText = board.getTile(i).toString();
			builder.append(tileText);
		}
		builder.insert(8, "/");
		builder.insert(17, "/");
		builder.insert(26, "/");
		builder.insert(35, "/");
		builder.insert(44, "/");
		builder.insert(53, "/");
		builder.insert(62, "/");
		return builder.toString().replaceAll("--------", "8").replaceAll("-------", "7").replaceAll("------", "6")
				.replaceAll("-----", "5").replaceAll("----", "4").replaceAll("---", "3").replaceAll("--", "2")
				.replaceAll("-", "1");
	}

	/**
	 * Calculate current player text.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	private static String calculateCurrentPlayerText(final Board board) {
		return board.currentPlayer().toString().substring(0, 1).toLowerCase();
	}
}