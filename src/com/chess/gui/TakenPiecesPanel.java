package com.chess.gui;

import java.io.File;
import java.util.List;
import java.awt.Color;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.io.IOException;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.border.EtchedBorder;

import com.chess.gui.Table.MoveLog;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.pieces.Piece;

import com.google.common.primitives.Ints;

/**
 * Located on westmost corner of the app.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class TakenPiecesPanel extends JPanel {

	/** The north panel. */
	private final JPanel northPanel;

	/** The south panel. */
	private final JPanel southPanel;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant PANEL_COLOR. */
	private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");

	/** The Constant TAKEN_PIECES_PANEL_DIMENSION. */
	private static final Dimension TAKEN_PIECES_PANEL_DIMENSION = new Dimension(40, 80);

	/** The Constant PANEL_BORDER. */
	private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

	/**
	 * Instantiates a new taken pieces panel.
	 */
	public TakenPiecesPanel() {
		super(new BorderLayout());
		setBackground(Color.decode("0xFDF5E6"));
		setBorder(PANEL_BORDER);
		this.northPanel = new JPanel(new GridLayout(8, 2));
		this.southPanel = new JPanel(new GridLayout(8, 2));
		this.northPanel.setBackground(PANEL_COLOR);
		this.southPanel.setBackground(PANEL_COLOR);
		add(this.northPanel, BorderLayout.NORTH);
		add(this.southPanel, BorderLayout.SOUTH);
		setPreferredSize(TAKEN_PIECES_PANEL_DIMENSION);
	}

	/**
	 * Redo.
	 *
	 * @param moveLog
	 *            the move log
	 */
	public void redo(final MoveLog moveLog) {
		southPanel.removeAll();
		northPanel.removeAll();
		final List<Piece> whiteTakenPieces = new ArrayList<>();
		final List<Piece> blackTakenPieces = new ArrayList<>();
		for (final Move move : moveLog.getMoves()) {
			if (move.isAttack()) {
				final Piece takenPiece = move.getAttackedPiece();
				if (takenPiece.getPieceAllegiance().isWhite()) {
					whiteTakenPieces.add(takenPiece);
				} else if (takenPiece.getPieceAllegiance().isBlack()) {
					blackTakenPieces.add(takenPiece);
				} else {
					throw new RuntimeException("Should not reach here!");
				}
			}
		}
		Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
			@Override
			public int compare(final Piece p1, final Piece p2) {
				return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
			}
		});
		Collections.sort(blackTakenPieces, new Comparator<Piece>() {
			@Override
			public int compare(final Piece p1, final Piece p2) {
				return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
			}
		});
		for (final Piece takenPiece : whiteTakenPieces) {
			try {
				BufferedImage img = null;
				try {
					final BufferedImage image = ImageIO.read(
							new File(Table.getImageSet() + takenPiece.getPieceAllegiance().toString().substring(0, 1)
									+ "" + takenPiece.toString() + ".gif"));
					img = image;
				} catch (IOException ex) {
					final BufferedImage image = ImageIO.read(
							new File(Table.getImageSet() + takenPiece.getPieceAllegiance().toString().substring(0, 1)
									+ "" + takenPiece.toString() + ".png"));
					img = image;
				} finally {
					final ImageIcon ic = new ImageIcon(img);
					final JLabel imageLabel = new JLabel(new ImageIcon(ic.getImage()
							.getScaledInstance(ic.getIconWidth() - 15, ic.getIconWidth() - 15, Image.SCALE_SMOOTH)));
					this.southPanel.add(imageLabel);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		for (final Piece takenPiece : blackTakenPieces) {
			try {
				BufferedImage img = null;
				try {
					final BufferedImage image = ImageIO.read(
							new File(Table.getImageSet() + takenPiece.getPieceAllegiance().toString().substring(0, 1)
									+ "" + takenPiece.toString() + ".gif"));
					img = image;
				} catch (IOException ex) {
					final BufferedImage image = ImageIO.read(
							new File(Table.getImageSet() + takenPiece.getPieceAllegiance().toString().substring(0, 1)
									+ "" + takenPiece.toString() + ".png"));
					img = image;
				} finally {
					final ImageIcon ic = new ImageIcon(img);
					final JLabel imageLabel = new JLabel(new ImageIcon(ic.getImage()
							.getScaledInstance(ic.getIconWidth() - 15, ic.getIconWidth() - 15, Image.SCALE_SMOOTH)));
					this.northPanel.add(imageLabel);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		validate();
		repaint();
	}
}