package com.chess.pgn;

import java.io.File;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.io.OutputStreamWriter;

import com.chess.gui.Table.MoveLog;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move.MoveFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Utilities for PGN tasks. Has things likle writing a game to a PGN or reading
 * from a PGN file. Has uses in A.I. learning too.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class PGNUtilities {

	/** The Constant PGN_PATTERN. */
	private static final Pattern PGN_PATTERN = Pattern.compile("\\[(\\w+)\\s+\"(.*?)\"\\]$");

	/** The Constant KING_SIDE_CASTLE. */
	private static final Pattern KING_SIDE_CASTLE = Pattern.compile("O-O#?\\+?");

	/** The Constant QUEEN_SIDE_CASTLE. */
	private static final Pattern QUEEN_SIDE_CASTLE = Pattern.compile("O-O-O#?\\+?");

	/** The Constant PLAIN_PAWN_MOVE. */
	private static final Pattern PLAIN_PAWN_MOVE = Pattern.compile("^([a-h][0-8])(\\+)?(#)?$");

	/** The Constant PAWN_ATTACK_MOVE. */
	private static final Pattern PAWN_ATTACK_MOVE = Pattern.compile("(^[a-h])(x)([a-h][0-8])(\\+)?(#)?$");

	/** The Constant PLAIN_MAJOR_MOVE. */
	private static final Pattern PLAIN_MAJOR_MOVE = Pattern
			.compile("^(B|N|R|Q|K)([a-h]|[1-8])?([a-h][0-8])(\\+)?(#)?$");

	/** The Constant MAJOR_ATTACK_MOVE. */
	private static final Pattern MAJOR_ATTACK_MOVE = Pattern
			.compile("^(B|N|R|Q|K)([a-h]|[1-8])?(x)([a-h][0-8])(\\+)?(#)?$");

	/** The Constant PLAIN_PAWN_PROMOTION_MOVE. */
	private static final Pattern PLAIN_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)=(.*?)");

	/** The Constant ATTACK_PAWN_PROMOTION_MOVE. */
	private static final Pattern ATTACK_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)x(.*?)=(.*?)");

	/**
	 * Instantiates a new PGN utilities.
	 */
	private PGNUtilities() {
		throw new RuntimeException("Not Instantiable!");
	}

	/**
	 * Persist PGN file.
	 *
	 * @param pgnFile
	 *            the pgn file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void persistPGNFile(final File pgnFile) throws IOException {
		int count = 0;
		int validCount = 0;
		try (final BufferedReader br = new BufferedReader(new FileReader(pgnFile))) {
			String line;
			PGNGameTags.TagsBuilder tagsBuilder = new PGNGameTags.TagsBuilder();
			StringBuilder gameTextBuilder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					if (isTag(line)) {
						final Matcher matcher = PGN_PATTERN.matcher(line);
						if (matcher.find()) {
							tagsBuilder.addTag(matcher.group(1), matcher.group(2));
						}
					} else if (isEndOfGame(line)) {
						final String[] ending = line.split(" ");
						final String outcome = ending[ending.length - 1];
						System.out.print(outcome);
						gameTextBuilder.append(line.replace(outcome, "")).append(" ");
						final String gameText = gameTextBuilder.toString().trim();
						System.out.print(gameText);
						if (!gameText.isEmpty() && gameText.length() > 80) {
							Game game = GameFactory.createGame(tagsBuilder.build(), gameText, outcome);
							System.out
									.println("(" + (++count) + ") Finished parsing " + game + " count = " + (++count));
							if (game.isValid()) {
								MySqlGamePersistence.get().persistGame(game);
								validCount++;
							}
						}
						gameTextBuilder = new StringBuilder();
						tagsBuilder = new PGNGameTags.TagsBuilder();
					} else {
						gameTextBuilder.append(line).append(" ");
					}
				}
			}
			br.readLine();
		}
		System.out.println("Finished building book from pgn file: " + pgnFile + " Parsed " + count + " games, valid = "
				+ validCount);
	}

	/**
	 * Write game to PGN file.
	 *
	 * @param pgnFile
	 *            the pgn file
	 * @param moveLog
	 *            the move log
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void writeGameToPGNFile(final File pgnFile, final MoveLog moveLog) throws IOException {
		final StringBuilder builder = new StringBuilder();
		builder.append(calculateEventString()).append("\n");
		builder.append(calculateDateString()).append("\n");
		builder.append(calculatePlyCountString(moveLog)).append("\n");
		for (final Move move : moveLog.getMoves()) {
			builder.append(move.toString()).append(" ");
		}
		try (final Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pgnFile, true)))) {
			writer.write(builder.toString());
		}
	}

	/**
	 * Calculate event string.
	 *
	 * @return the string
	 */
	private static String calculateEventString() {
		return "[Event \"" + "TigerTooth Chess Game" + "\"]";
	}

	/**
	 * Calculate date string.
	 *
	 * @return the string
	 */
	private static String calculateDateString() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		return "[Date \"" + dateFormat.format(new Date()) + "\"]";
	}

	/**
	 * Calculate ply count string.
	 *
	 * @param moveLog
	 *            the move log
	 * @return the string
	 */
	private static String calculatePlyCountString(final MoveLog moveLog) {
		return "[PlyCount \"" + moveLog.size() + "\"]";
	}

	/**
	 * Process move text.
	 *
	 * @param gameText
	 *            the game text
	 * @return the list
	 * @throws ParsePGNException
	 *             the parse PGN exception
	 */
	public static List<String> processMoveText(final String gameText) throws ParsePGNException {
		return gameText.isEmpty() ? Collections.<String>emptyList() : createMovesFromPGN(gameText);
	}

	/**
	 * Creates the moves from PGN.
	 *
	 * @param pgnText
	 *            the pgn text
	 * @return the list
	 */
	private static List<String> createMovesFromPGN(final String pgnText) {
		if (!pgnText.startsWith("1.")) {
			return Collections.emptyList();
		}
		final List<String> sanitizedMoves = new LinkedList<>(
				Arrays.asList(removeParenthesis(pgnText).replaceAll(Pattern.quote("$") + "[0-9]+", "")
						.replaceAll("[0-9]+\\s*\\.\\.\\.", "").split("\\s*[0-9]+" + Pattern.quote("."))));
		final List<String> processedData = removeEmptyText(sanitizedMoves);
		final String[] moveRows = processedData.toArray(new String[processedData.size()]);
		final ImmutableList.Builder<String> moves = new Builder<>();
		for (final String row : moveRows) {
			final String[] moveContent = removeWhiteSpace(row).split(" ");
			if (moveContent.length == 1) {
				moves.add(moveContent[0]);
			} else if (moveContent.length == 2) {
				moves.add(moveContent[0]);
				moves.add(moveContent[1]);
			} else {
				System.out.println("problem reading: " + pgnText + " skipping!");
				return Collections.emptyList();
			}
		}
		return moves.build();
	}

	/**
	 * Removes the empty text.
	 *
	 * @param moves
	 *            the moves
	 * @return the list
	 */
	private static List<String> removeEmptyText(final List<String> moves) {
		final List<String> result = new ArrayList<>();
		for (final String moveText : moves) {
			if (!moveText.isEmpty()) {
				result.add(moveText);
			}
		}
		return result;
	}

	/**
	 * Removes the white space.
	 *
	 * @param row
	 *            the row
	 * @return the string
	 */
	private static String removeWhiteSpace(final String row) {
		return row.trim().replaceAll("\\s+", " ");
	}

	/**
	 * Checks if is tag.
	 *
	 * @param gameText
	 *            the game text
	 * @return true, if is tag
	 */
	private static boolean isTag(final String gameText) {
		return gameText.startsWith("[") && gameText.endsWith("]");
	}

	/**
	 * Checks if is end of game.
	 *
	 * @param gameText
	 *            the game text
	 * @return true, if is end of game
	 */
	private static boolean isEndOfGame(final String gameText) {
		return gameText.endsWith("1-0") || gameText.endsWith("0-1") || gameText.endsWith("1/2-1/2")
				|| gameText.endsWith("*");
	}

	/**
	 * Removes the parenthesis.
	 *
	 * @param gameText
	 *            the game text
	 * @return the string
	 */
	private static String removeParenthesis(final String gameText) {
		int parenthesisCounter = 0;
		final StringBuilder builder = new StringBuilder();
		for (final char c : gameText.toCharArray()) {
			if (c == '(' || c == '{') {
				parenthesisCounter++;
			}
			if (c == ')' || c == '}') {
				parenthesisCounter--;
			}
			if (!(c == '(' || c == '{' || c == ')' || c == '}') && parenthesisCounter == 0) {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * Creates the move.
	 *
	 * @param board
	 *            the board
	 * @param pgnText
	 *            the pgn text
	 * @return the move
	 */
	public static Move createMove(final Board board, final String pgnText) {
		final Matcher kingSideCastleMatcher = KING_SIDE_CASTLE.matcher(pgnText);
		final Matcher queenSideCastleMatcher = QUEEN_SIDE_CASTLE.matcher(pgnText);
		final Matcher plainPawnMatcher = PLAIN_PAWN_MOVE.matcher(pgnText);
		final Matcher attackPawnMatcher = PAWN_ATTACK_MOVE.matcher(pgnText);
		final Matcher pawnPromotionMatcher = PLAIN_PAWN_PROMOTION_MOVE.matcher(pgnText);
		final Matcher attackPawnPromotionMatcher = ATTACK_PAWN_PROMOTION_MOVE.matcher(pgnText);
		final Matcher plainMajorMatcher = PLAIN_MAJOR_MOVE.matcher(pgnText);
		final Matcher attackMajorMatcher = MAJOR_ATTACK_MOVE.matcher(pgnText);

		int currentCoordinate;
		int destinationCoordinate;

		if (kingSideCastleMatcher.matches()) {
			return extractCastleMove(board, "O-O");
		} else if (queenSideCastleMatcher.matches()) {
			return extractCastleMove(board, "O-O-O");
		} else if (plainPawnMatcher.matches()) {
			final String destinationSquare = plainPawnMatcher.group(1);
			destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
			currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, "");
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		} else if (attackPawnMatcher.matches()) {
			final String destinationSquare = attackPawnMatcher.group(3);
			destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
			final String disambiguationFile = attackPawnMatcher.group(1) != null ? attackPawnMatcher.group(1) : "";
			currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		} else if (attackPawnPromotionMatcher.matches()) {
			final String destinationSquare = attackPawnPromotionMatcher.group(2);
			final String disambiguationFile = attackPawnPromotionMatcher.group(1) != null
					? attackPawnPromotionMatcher.group(1) : "";
			destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
			currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		} else if (pawnPromotionMatcher.find()) {
			final String destinationSquare = pawnPromotionMatcher.group(1);
			destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
			currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, "");
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		} else if (plainMajorMatcher.find()) {
			final String destinationSquare = plainMajorMatcher.group(3);
			destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
			final String disambiguationFile = plainMajorMatcher.group(2) != null ? plainMajorMatcher.group(2) : "";
			currentCoordinate = deriveCurrentCoordinate(board, plainMajorMatcher.group(1), destinationSquare,
					disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		} else if (attackMajorMatcher.find()) {
			final String destinationSquare = attackMajorMatcher.group(4);
			destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
			final String disambiguationFile = attackMajorMatcher.group(2) != null ? attackMajorMatcher.group(2) : "";
			currentCoordinate = deriveCurrentCoordinate(board, attackMajorMatcher.group(1), destinationSquare,
					disambiguationFile);
			return MoveFactory.createMove(board, currentCoordinate, destinationCoordinate);
		}
		return MoveFactory.getNullMove();
	}

	/**
	 * Extract castle move.
	 *
	 * @param board
	 *            the board
	 * @param castleMove
	 *            the castle move
	 * @return the move
	 */
	private static Move extractCastleMove(final Board board, final String castleMove) {
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			if (move.isCastlingMove() && move.toString().equals(castleMove)) {
				return move;
			}
		}
		return MoveFactory.getNullMove();
	}

	/**
	 * Derive current coordinate.
	 *
	 * @param board
	 *            the board
	 * @param movedPiece
	 *            the moved piece
	 * @param destinationSquare
	 *            the destination square
	 * @param disambiguationFile
	 *            the disambiguation file
	 * @return the int
	 * @throws RuntimeException
	 *             the runtime exception
	 */
	private static int deriveCurrentCoordinate(final Board board, final String movedPiece,
			final String destinationSquare, final String disambiguationFile) throws RuntimeException {
		final List<Move> currentCandidates = new ArrayList<>();
		final int destinationCoordinate = BoardUtils.INSTANCE.getCoordinateAtPosition(destinationSquare);
		for (final Move move : board.currentPlayer().getLegalMoves()) {
			if (move.getDestinationCoordinate() == destinationCoordinate
					&& move.getMovedPiece().toString().equals(movedPiece)) {
				currentCandidates.add(move);
			}
		}
		if (currentCandidates.size() == 0) {
			return -1;
		}
		return currentCandidates.size() == 1 ? currentCandidates.iterator().next().getCurrentCoordinate()
				: extractFurther(currentCandidates, movedPiece, disambiguationFile);
	}

	/**
	 * Extract further.
	 *
	 * @param candidateMoves
	 *            the candidate moves
	 * @param movedPiece
	 *            the moved piece
	 * @param disambiguationFile
	 *            the disambiguation file
	 * @return the int
	 */
	private static int extractFurther(final List<Move> candidateMoves, final String movedPiece,
			final String disambiguationFile) {
		final List<Move> currentCandidates = new ArrayList<>();
		for (final Move move : candidateMoves) {
			if (move.getMovedPiece().getPieceType().toString().equals(movedPiece)) {
				currentCandidates.add(move);
			}
		}
		if (currentCandidates.size() == 1) {
			return currentCandidates.iterator().next().getCurrentCoordinate();
		}
		final List<Move> candidatesRefined = new ArrayList<>();
		for (final Move move : currentCandidates) {
			final String pos = BoardUtils.INSTANCE.getPositionAtCoordinate(move.getCurrentCoordinate());
			if (pos.contains(disambiguationFile)) {
				candidatesRefined.add(move);
			}
		}
		if (candidatesRefined.size() == 1) {
			return candidatesRefined.iterator().next().getCurrentCoordinate();
		}
		return -1;
	}
}