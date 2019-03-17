package com.chess.gui;

import java.util.List;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.awt.BorderLayout;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.chess.gui.Table.MoveLog;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;

/**
 * Located on eastmost corner of the app.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class GameHistoryPanel extends JPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6558548076207283945L;

	/** The model. */
	private final DataModel model;

	/** The scroll pane. */
	private final JScrollPane scrollPane;

	/** The Constant HISTORY_PANEL_DIMENSION. */
	private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(100, 40);

	/**
	 * Instantiates a new game history panel.
	 */
	public GameHistoryPanel() {
		this.setLayout(new BorderLayout());
		this.model = new DataModel();
		final JTable table = new JTable(model);
		table.setRowHeight(15);
		this.scrollPane = new JScrollPane(table);
		scrollPane.setColumnHeaderView(table.getTableHeader());
		scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setVisible(true);
	}

	/**
	 * Redo.
	 *
	 * @param board
	 *            the board
	 * @param moveHistory
	 *            the move history
	 */
	public void redo(final Board board, final MoveLog moveHistory) {
		int currentRow = 0;
		this.model.clear();
		for (final Move move : moveHistory.getMoves()) {
			final String moveText = move.toString();
			if (move.getMovedPiece().getPieceAllegiance().isWhite()) {
				this.model.setValueAt(moveText, currentRow, 0);
			} else if (move.getMovedPiece().getPieceAllegiance().isBlack()) {
				this.model.setValueAt(moveText, currentRow, 1);
				currentRow++;
			}
		}
		if (moveHistory.getMoves().size() > 0) {
			final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
			final String moveText = lastMove.toString();

			if (lastMove.getMovedPiece().getPieceAllegiance().isWhite()) {
				this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow, 0);
			} else if (lastMove.getMovedPiece().getPieceAllegiance().isBlack()) {
				this.model.setValueAt(moveText + calculateCheckAndCheckMateHash(board), currentRow - 1, 1);
			}
		}
		final JScrollBar vertical = scrollPane.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	/**
	 * Calculate check and check mate hash.
	 *
	 * @param board
	 *            the board
	 * @return the string
	 */
	private static String calculateCheckAndCheckMateHash(final Board board) {
		if (board.currentPlayer().isInCheckMate()) {
			return "#";
		} else if (board.currentPlayer().isInCheck()) {
			return "+";
		}
		return "";
	}

	/**
	 * The Class Row.
	 */
	private static class Row {

		/** The white move. */
		private String whiteMove;

		/** The black move. */
		private String blackMove;

		/**
		 * Instantiates a new row.
		 */
		Row() {
		}

		/**
		 * Gets the white move.
		 *
		 * @return the white move
		 */
		public String getWhiteMove() {
			return this.whiteMove;
		}

		/**
		 * Gets the black move.
		 *
		 * @return the black move
		 */
		public String getBlackMove() {
			return this.blackMove;
		}

		/**
		 * Sets the white move.
		 *
		 * @param move
		 *            the new white move
		 */
		public void setWhiteMove(final String move) {
			this.whiteMove = move;
		}

		/**
		 * Sets the black move.
		 *
		 * @param move
		 *            the new black move
		 */
		public void setBlackMove(final String move) {
			this.blackMove = move;
		}

	}

	/**
	 * The Class DataModel.
	 */
	private static class DataModel extends DefaultTableModel {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -3231984252730057299L;

		/** The values. */
		private final List<Row> values;

		/** The Constant NAMES. */
		private static final String[] NAMES = { "White", "Black" };

		/**
		 * Instantiates a new data model.
		 */
		DataModel() {
			this.values = new ArrayList<>();
		}

		/**
		 * Clear.
		 */
		public void clear() {
			this.values.clear();
			setRowCount(0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.DefaultTableModel#getRowCount()
		 */
		@Override
		public int getRowCount() {
			if (this.values == null) {
				return 0;
			}
			return this.values.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.DefaultTableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return NAMES.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(final int row, final int col) {
			final Row currentRow = this.values.get(row);
			if (col == 0) {
				return currentRow.getWhiteMove();
			} else if (col == 1) {
				return currentRow.getBlackMove();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object,
		 * int, int)
		 */
		@Override
		public void setValueAt(final Object aValue, final int row, final int col) {
			final Row currentRow;
			if (this.values.size() <= row) {
				currentRow = new Row();
				this.values.add(currentRow);
			} else {
				currentRow = this.values.get(row);
			}
			if (col == 0) {
				currentRow.setWhiteMove((String) aValue);
				fireTableRowsInserted(row, row);
			} else if (col == 1) {
				currentRow.setBlackMove((String) aValue);
				fireTableCellUpdated(row, col);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(final int col) {
			return Move.class;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.DefaultTableModel#getColumnName(int)
		 */
		@Override
		public String getColumnName(final int col) {
			return NAMES[col];
		}
	}
}