package com.chess.pgn;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.player.Player;

/**
 * Responsible for getting a PGN file and putting it to a database, then
 * retrieveing it form the database to teach the A.I. how to play based on the
 * loaded PGN files. Doesn't work as of now.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class MySqlGamePersistence implements PGNPersistence {

	/** The db connection. */
	private final Connection dbConnection;

	/** The ýnstance. */
	private static MySqlGamePersistence INSTANCE = new MySqlGamePersistence();

	/** The Constant JDBC_DRIVER. */
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	/** The Constant DB_URL. */
	private static final String DB_URL = "jdbc:mysql://localhost:3306/sys?useSSL=false";

	/** The Constant USER. */
	private static final String USER = "root";

	/** The Constant PASS. */
	private static final String PASS = "selam";

	/** The Constant NEXT_BEST_MOVE_QUERY. */
	private static final String NEXT_BEST_MOVE_QUERY = "SELECT SUBSTR(g1.moves, LENGTH('%s') + %d, INSTR(SUBSTR(g1.moves, LENGTH('%s') + %d, LENGTH(g1.moves)), ',') - 1), "
			+ "COUNT(*) FROM game g1 WHERE g1.moves LIKE '%s%%' AND (outcome = '%s') GROUP BY substr(g1.moves, LENGTH('%s') + %d, "
			+ "INSTR(substr(g1.moves, LENGTH('%s') + %d, LENGTH(g1.moves)), ',') - 1) ORDER BY 2 DESC";

	/**
	 * Instantiates a new my sql game persistence.
	 */
	private MySqlGamePersistence() {
		this.dbConnection = createDBConnection();
		createGameTable();
		createIndex("outcome", "OutcomeIndex");
		createIndex("moves", "MoveIndex");

		// createOutcomeIndex();
		// createMovesIndex();
	}

	/**
	 * Creates the DB connection.
	 *
	 * @return the connection
	 */
	private static Connection createDBConnection() {
		try {
			Class.forName(JDBC_DRIVER);
			return DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (final ClassNotFoundException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the.
	 *
	 * @return the my sql game persistence
	 */
	public static MySqlGamePersistence get() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chess.pgn.PGNPersistence#persistGame(com.chess.pgn.Game)
	 */
	@Override
	public void persistGame(final Game game) {
		executePersist(game);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chess.pgn.PGNPersistence#getNextBestMove(com.chess.engine.classic.
	 * board.Board, com.chess.engine.classic.player.Player, java.lang.String)
	 */
	@Override
	public Move getNextBestMove(final Board board, final Player player, final String gameText) {
		return queryBestMove(board, player, gameText);
	}

	/**
	 * Query best move.
	 *
	 * @param board
	 *            the board
	 * @param player
	 *            the player
	 * @param gameText
	 *            the game text
	 * @return the move
	 */
	private Move queryBestMove(final Board board, final Player player, final String gameText) {
		String bestMove = "";
		String count = "0";
		try {
			final int offSet = gameText.isEmpty() ? 1 : 3;
			final String sqlString = String.format(NEXT_BEST_MOVE_QUERY, gameText, offSet, gameText, offSet, gameText,
					player.getAlliance().name(), gameText, offSet, gameText, offSet);
			System.out.println("exec 68");
			System.out.println(sqlString);
			System.out.println("exec 70");
			final Statement gameStatement = this.dbConnection.createStatement();
			System.out.println(gameStatement);
			gameStatement.execute(sqlString);
			final ResultSet rs2 = gameStatement.getResultSet();
			System.out.println("exec 75");
			System.out.println(rs2);
			if (rs2.next()) {
				bestMove = rs2.getString(1);
				count = rs2.getString(2);
			}
			gameStatement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		System.out.println("\tselected book move = " + bestMove + " with " + count + " hits");
		return PGNUtilities.createMove(board, bestMove);
	}

	/**
	 * Creates the game table.
	 */
	private void createGameTable() {
		try {
			final Statement statement = this.dbConnection.createStatement();
			statement.execute(
					"CREATE TABLE IF NOT EXISTS Game(id int primary key, outcome varchar(10), moves varchar(4000));");
			statement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the ýndex.
	 *
	 * @param columnName
	 *            the column name
	 * @param indexName
	 *            the index name
	 */
	private void createIndex(final String columnName, final String indexName) {
		try {
			final String sqlString = "SELECT * FROM SYS.GAME WHERE MOVES IS NOT NULL";
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet resultSet = gameStatement.getResultSet();
			if (!resultSet.isBeforeFirst()) {
				final Statement indexStatement = this.dbConnection.createStatement();
				indexStatement.execute("CREATE INDEX " + indexName + " on Game(" + columnName + ");\n");
				indexStatement.close();
			}
			gameStatement.close();
		} catch (final SQLException e) {
			System.out.println("119");
			e.printStackTrace();
		}
	}

	/**
	 * Creates the outcome ýndex.
	 */
	@SuppressWarnings("unused")
	private void createOutcomeIndex() {
		try {
			final String sqlString = "SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_CATALOG = 'def' AND TABLE_SCHEMA = DATABASE() AND TABLE_NAME = \"game\" AND INDEX_NAME = \"OutcomeIndex\"";
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet resultSet = gameStatement.getResultSet();
			if (!resultSet.isBeforeFirst()) {
				final Statement indexStatement = this.dbConnection.createStatement();
				indexStatement.execute("CREATE INDEX OutcomeIndex on Game(outcome);\n");
				indexStatement.close();
			}
			gameStatement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the moves ýndex.
	 */
	@SuppressWarnings("unused")
	private void createMovesIndex() {
		try {
			final String sqlString = "SELECT * FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_CATALOG = 'def' AND TABLE_SCHEMA = DATABASE() AND TABLE_NAME = \"game\" AND INDEX_NAME = \"MoveIndex\"";
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet resultSet = gameStatement.getResultSet();
			if (!resultSet.isBeforeFirst()) {
				final Statement indexStatement = this.dbConnection.createStatement();
				indexStatement.execute("CREATE INDEX MoveIndex on Game(moves);\n");
				indexStatement.close();
			}
			gameStatement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the max game row.
	 *
	 * @return the max game row
	 */
	public int getMaxGameRow() {
		int maxId = 0;
		try {
			final String sqlString = "SELECT MAX(ID) FROM Game";
			final Statement gameStatement = this.dbConnection.createStatement();
			gameStatement.execute(sqlString);
			final ResultSet rs2 = gameStatement.getResultSet();
			if (rs2.next()) {
				maxId = rs2.getInt(1);
			}
			gameStatement.close();
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return maxId;
	}

	/**
	 * Execute persist.
	 *
	 * @param game
	 *            the game
	 */
	private void executePersist(final Game game) {
		try {
			final String gameSqlString = "INSERT INTO Game(id, outcome, moves) VALUES(?, ?, ?);";
			final PreparedStatement gameStatement = this.dbConnection.prepareStatement(gameSqlString);
			gameStatement.setInt(1, getMaxGameRow() + 1);
			gameStatement.setString(2, game.getWinner());
			gameStatement.setString(3, game.getMoves().toString().replaceAll("\\[", "").replaceAll("\\]", ""));
			gameStatement.executeUpdate();
			gameStatement.close();
		} catch (final SQLException e) {
			System.out.print("a");
			e.printStackTrace();
		}
	}
}