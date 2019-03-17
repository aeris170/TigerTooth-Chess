package com.chess.gui;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;
import static javax.swing.JFrame.setDefaultLookAndFeelDecorated;

import static com.chess.pgn.PGNUtilities.persistPGNFile;
import static com.chess.pgn.PGNUtilities.writeGameToPGNFile;

import java.io.File;
import java.util.List;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JMenu;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.util.Observer;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Observable;
import javax.swing.JMenuBar;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import java.util.Collections;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import javax.swing.WindowConstants;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.image.WritableRaster;
import java.net.UnknownHostException;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.filechooser.FileFilter;

import network.ClientGUI;
import network.ServerGUI;
import network.ChatMessage;
import com.chess.pgn.FenUtilities;
import com.chess.pgn.MySqlGamePersistence;
import com.chess.engine.classic.board.Tile;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.pieces.Piece;
import com.chess.engine.classic.player.Player;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.MoveTransition;
import com.chess.engine.classic.board.Move.MoveFactory;
import com.chess.engine.classic.player.ai.StockAlphaBeta;
import com.chess.engine.classic.player.ai.StandardBoardEvaluator;

import com.google.common.collect.Lists;

/**
 * The ultimate UI. Everything is happening here. I recommend you not to read
 * this class. Just know that it is responsible for keeping everything in one
 * piece. This class defies the laws of MVC(Model-View-Controller) pattern. I
 * wrote this class in frustration in a single night. I'm sorry :(
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public final class Table extends Observable {

	/** The game frame. */
	private final JFrame gameFrame;

	/** The game history panel. */
	private final GameHistoryPanel gameHistoryPanel;

	/** The taken pieces panel. */
	private final TakenPiecesPanel takenPiecesPanel;

	/** The debug panel. */
	private final DebugPanel debugPanel;

	/** The board panel. */
	private final BoardPanel boardPanel;

	/** The move log. */
	private MoveLog moveLog;

	/** The game setup. */
	private final GameSetup gameSetup;

	/** The chess board. */
	private Board chessBoard;

	/** The computer move. */
	private Move computerMove;

	/** The source tile. */
	private Tile sourceTile;

	/** The destination tile. */
	private Tile destinationTile;

	/** The human moved piece. */
	private Piece humanMovedPiece;

	/** The board direction. */
	private BoardDirection boardDirection;

	/** The piece ýcon path. */
	private static String pieceIconPath;

	/** The current MP status. */
	private String currentMPStatus;

	/** The highlight legal moves. */
	private boolean highlightLegalMoves;

	/** The use book. */
	private boolean useBook;

	/** The highlight ýmage. */
	private BufferedImage highlightImage;

	/** The client GUI. */
	private ClientGUI clientGUI;

	/** The server GUI. */
	private ServerGUI serverGUI;

	/** The light tile color. */
	private Color lightTileColor = Color.decode("#FFFACD");

	/** The dark tile color. */
	private Color darkTileColor = Color.decode("#593E1A");

	/** The Constant OUTER_FRAME_DIMENSION. */
	private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(800, 800);

	/** The Constant BOARD_PANEL_DIMENSION. */
	private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(450, 400);

	/** The Constant TILE_PANEL_DIMENSION. */
	private static final Dimension TILE_PANEL_DIMENSION = new Dimension(5, 5);

	/** The Constant INSTANCE. */
	private static final Table INSTANCE = new Table();

	/**
	 * Instantiates a new table.
	 */
	private Table() {
		this.gameFrame = new JFrame("TigerTooth Chess v1.0 by The Puppet Snake");
		final JMenuBar tableMenuBar = new JMenuBar();
		populateMenuBar(tableMenuBar);
		this.gameFrame.setJMenuBar(tableMenuBar);
		this.gameFrame.setLayout(new BorderLayout());
		this.chessBoard = Board.createStandardBoard();
		this.boardDirection = BoardDirection.NORMAL;
		this.highlightLegalMoves = true;
		this.useBook = true;
		Table.pieceIconPath = "art/cburnett/";
		try {
			this.gameFrame.setIconImage(ImageIO.read(new File("art/main_elements/logo.png")));
			this.highlightImage = ImageIO.read(new File("art/misc/green_dot.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		this.gameHistoryPanel = new GameHistoryPanel();
		this.debugPanel = new DebugPanel();
		this.takenPiecesPanel = new TakenPiecesPanel();
		this.boardPanel = new BoardPanel();
		this.moveLog = new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		this.gameSetup = new GameSetup(this.gameFrame, true);
		this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
		this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
		this.gameFrame.add(debugPanel, BorderLayout.SOUTH);
		setDefaultLookAndFeelDecorated(true);
		this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
		center(this.gameFrame);
		this.gameFrame.setVisible(true);
	}

	/**
	 * Gets the.
	 *
	 * @return the table
	 */
	public static Table get() {
		return INSTANCE;
	}

	/**
	 * Gets the ýmage set.
	 *
	 * @return the ýmage set
	 */
	public static String getImageSet() {
		return pieceIconPath;
	}

	/**
	 * Gets the game frame.
	 *
	 * @return the game frame
	 */
	private JFrame getGameFrame() {
		return this.gameFrame;
	}

	/**
	 * Gets the game board.
	 *
	 * @return the game board
	 */
	private Board getGameBoard() {
		return this.chessBoard;
	}

	/**
	 * Gets the move log.
	 *
	 * @return the move log
	 */
	public MoveLog getMoveLog() {
		return this.moveLog;
	}

	/**
	 * Gets the board panel.
	 *
	 * @return the board panel
	 */
	private BoardPanel getBoardPanel() {
		return this.boardPanel;
	}

	/**
	 * Gets the game history panel.
	 *
	 * @return the game history panel
	 */
	private GameHistoryPanel getGameHistoryPanel() {
		return this.gameHistoryPanel;
	}

	/**
	 * Gets the taken pieces panel.
	 *
	 * @return the taken pieces panel
	 */
	private TakenPiecesPanel getTakenPiecesPanel() {
		return this.takenPiecesPanel;
	}

	/**
	 * Gets the debug panel.
	 *
	 * @return the debug panel
	 */
	private DebugPanel getDebugPanel() {
		return this.debugPanel;
	}

	/**
	 * Gets the game setup.
	 *
	 * @return the game setup
	 */
	public GameSetup getGameSetup() {
		return this.gameSetup;
	}

	/**
	 * Gets the highlight legal moves.
	 *
	 * @return the highlight legal moves
	 */
	private boolean getHighlightLegalMoves() {
		return this.highlightLegalMoves;
	}

	/**
	 * Gets the use book.
	 *
	 * @return the use book
	 */
	private boolean getUseBook() {
		return this.useBook;
	}

	/**
	 * Gets the current FEN.
	 *
	 * @return the current FEN
	 */
	public String getCurrentFEN() {
		return currentMPStatus;
	}

	/**
	 * Update by FEN.
	 *
	 * @param FEN
	 *            the fen
	 */
	public void updateByFEN(String FEN) {
		chessBoard = FenUtilities.createGameFromFEN(FEN);
		Table.get().getBoardPanel().drawBoard(chessBoard);
	}

	/**
	 * Synchronize.
	 */
	public void synchronize() {
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
		Table.get().getDebugPanel().redo();
		if (Table.get().getDebugPanel().getTimer().isInInitialState()) {
			Table.get().getDebugPanel().getTimer().start();
		}
	}

	/**
	 * Sets the move log.
	 *
	 * @param log
	 *            the new move log
	 */
	public void setMoveLog(MoveLog log) {
		this.moveLog = log;
	}

	/**
	 * Show.
	 */
	public void show() {
		Runnable r = new Runnable() {
			public void run() {
				Table.get().getMoveLog().clear();
				Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
				Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
				Table.get().getDebugPanel().redo();
			}
		};
		new Thread(r).start();
	}

	/**
	 * Populate menu bar.
	 *
	 * @param tableMenuBar
	 *            the table menu bar
	 */
	private void populateMenuBar(final JMenuBar tableMenuBar) {
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createPreferencesMenu());
		tableMenuBar.add(createNewGameMenu());
		tableMenuBar.add(createSinglePlayerMenu());
		tableMenuBar.add(createMultiplayerMenu());
	}

	/**
	 * Center.
	 *
	 * @param frame
	 *            the frame
	 */
	private static void center(final JFrame frame) {
		final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		final int w = frame.getSize().width;
		final int h = frame.getSize().height;
		final int x = (dim.width - w) / 2;
		final int y = (dim.height - h) / 2;
		frame.setLocation(x, y);
	}

	/**
	 * Creates the file menu.
	 *
	 * @return the j menu
	 */
	private JMenu createFileMenu() {
		final JMenu filesMenu = new JMenu("File");
		filesMenu.setMnemonic(KeyEvent.VK_F);

		final JMenuItem openPGN = new JMenuItem("Load PGN File for AI to think (Requires a database set-up)",
				KeyEvent.VK_P);
		openPGN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				int option = chooser.showOpenDialog(Table.get().getGameFrame());
				if (option == JFileChooser.APPROVE_OPTION) {
					loadPGNFile(chooser.getSelectedFile());
				}
			}
		});
		filesMenu.add(openPGN);

		final JMenuItem openFEN = new JMenuItem("Load FEN String", KeyEvent.VK_F);
		openFEN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				String fenString = JOptionPane.showInputDialog("Input FEN", "Input Here");
				undoAllMoves();
				chessBoard = FenUtilities.createGameFromFEN(fenString);
				Table.get().getBoardPanel().drawBoard(chessBoard);
			}
		});
		filesMenu.add(openFEN);

		final JMenuItem saveToPGN = new JMenuItem("Save Game", KeyEvent.VK_V);
		saveToPGN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return ".pgn";
					}

					@Override
					public boolean accept(final File file) {
						return file.isDirectory() || file.getName().toLowerCase().endsWith("pgn");
					}
				});
				final int option = chooser.showSaveDialog(Table.get().getGameFrame());
				if (option == JFileChooser.APPROVE_OPTION) {
					savePGNFile(chooser.getSelectedFile());
				}
			}
		});
		filesMenu.add(saveToPGN);

		final JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_E);
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.get().getGameFrame().dispose();
				System.exit(0);
			}
		});
		filesMenu.add(exitMenuItem);

		return filesMenu;
	}

	/**
	 * Creates the new game menu.
	 *
	 * @return the j menu
	 */
	private JMenu createNewGameMenu() {
		final JMenu newGameMenu = new JMenu("Game Utils");
		newGameMenu.setMnemonic(KeyEvent.VK_U);

		final JMenuItem newGameMenuIem = new JMenuItem("New Game", KeyEvent.VK_N);
		newGameMenuIem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				undoAllMoves();
				Table.get().getDebugPanel().getTimer().reset();
				Table.get().getDebugPanel().getTimer().stop();
				if (clientGUI != null) {
					clientGUI.getClient().sendMessage(new ChatMessage(3, FenUtilities.createFENFromGame(chessBoard)));
					clientGUI.getClient().serializeMoveLog(getMoveLog());
				} else if (serverGUI != null) {
					serverGUI.getAutoClient().getClient()
							.sendMessage(new ChatMessage(3, FenUtilities.createFENFromGame(chessBoard)));
					serverGUI.getAutoClient().getClient().serializeMoveLog(getMoveLog());
				}
			}
		});
		newGameMenu.add(newGameMenuIem);

		final JMenuItem undoMoveMenuItem = new JMenuItem("Undo last move", KeyEvent.VK_U);
		undoMoveMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (Table.get().getMoveLog().size() > 0) {
					undoLastMove();
					if (clientGUI != null) {
						clientGUI.getClient()
								.sendMessage(new ChatMessage(3, FenUtilities.createFENFromGame(chessBoard)));
						clientGUI.getClient().serializeMoveLog(getMoveLog());
					} else if (serverGUI != null) {
						serverGUI.getAutoClient().getClient()
								.sendMessage(new ChatMessage(3, FenUtilities.createFENFromGame(chessBoard)));
						serverGUI.getAutoClient().getClient().serializeMoveLog(getMoveLog());
					}
				}
			}
		});
		newGameMenu.add(undoMoveMenuItem);

		return newGameMenu;
	}

	/**
	 * Creates the options menu.
	 *
	 * @return the j menu
	 */
	private JMenu createSinglePlayerMenu() {

		final JMenu singlePlayerMenu = new JMenu("Singlepayer Menu");
		singlePlayerMenu.setMnemonic(KeyEvent.VK_S);

		final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board", KeyEvent.VK_E);
		evaluateBoardMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.out.println(StandardBoardEvaluator.get().evaluate(chessBoard, gameSetup.getSearchDepth()));
				if (Table.get().getGameSetup().getWhitePlayerType() != PlayerType.COMPUTER
						|| Table.get().getGameSetup().getBlackPlayerType() != PlayerType.COMPUTER) {
					Table.get().getDebugPanel().getAIThinkArea().setText("");
				}
				Table.get().getDebugPanel().getAIThinkArea()
						.setText(Table.get().getDebugPanel().getAIThinkArea().getText() + " Current Score is: "
								+ StandardBoardEvaluator.get().evaluate(chessBoard, gameSetup.getSearchDepth()));
			}
		});
		singlePlayerMenu.add(evaluateBoardMenuItem);

		final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game", KeyEvent.VK_S);
		setupGameMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.get().getGameSetup().promptUser();
				Table.get().setupUpdate(Table.get().getGameSetup());
				Table.get().getDebugPanel().getTimer().reset();
			}
		});
		singlePlayerMenu.add(setupGameMenuItem);

		final JMenuItem singlePlayerHelp = new JMenuItem("Help, A.I. doesn't work", KeyEvent.VK_H);
		singlePlayerHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"If you don't have a database connected to the game,\nmake sure \"Use Book Moves\" under \"Preferences\" tab is unchecked.\n\nTry lowering the search depth of the A.I. opponent under \"Setup Game\"",
						"Singleplayer Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		singlePlayerMenu.add(singlePlayerHelp);

		return singlePlayerMenu;
	}

	/**
	 * Creates the preferences menu.
	 *
	 * @return the j menu
	 */
	private JMenu createPreferencesMenu() {

		final JMenu preferencesMenu = new JMenu("Preferences");
		preferencesMenu.setMnemonic(KeyEvent.VK_P);

		final JMenu colorChooserSubMenu = new JMenu("Choose Colors");
		colorChooserSubMenu.setMnemonic(KeyEvent.VK_C);

		final JMenuItem chooseDarkMenuItem = new JMenuItem("Choose Dark Tile Color");
		chooseDarkMenuItem.setMnemonic(KeyEvent.VK_D);
		colorChooserSubMenu.add(chooseDarkMenuItem);

		final JMenuItem chooseLightMenuItem = new JMenuItem("Choose Light Tile Color");
		chooseLightMenuItem.setMnemonic(KeyEvent.VK_L);
		colorChooserSubMenu.add(chooseLightMenuItem);

		final JMenuItem chooseLegalHighlightMenuItem = new JMenuItem("Choose Legal Move Highlight Color");
		chooseLegalHighlightMenuItem.setMnemonic(KeyEvent.VK_H);
		colorChooserSubMenu.add(chooseLegalHighlightMenuItem);

		preferencesMenu.add(colorChooserSubMenu);

		chooseDarkMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(), "Choose Dark Tile Color",
						Table.get().getGameFrame().getBackground());
				if (colorChoice != null) {
					Table.get().getBoardPanel().setTileDarkColor(chessBoard, colorChoice);
					debugPanel.getTimerLabel().setForeground(colorChoice);
				}
			}
		});

		chooseLightMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(),
						"Choose Light Tile Color", Table.get().getGameFrame().getBackground());
				if (colorChoice != null) {
					Table.get().getBoardPanel().setTileLightColor(chessBoard, colorChoice);
					debugPanel.getTimerLabel().setBackground(colorChoice);
				}
			}
		});

		final JMenu chessMenChoiceSubMenu = new JMenu("Choose Chess Piece Image Set");
		chessMenChoiceSubMenu.setMnemonic(KeyEvent.VK_P);

		final JMenuItem cburnettMenuItem = new JMenuItem("Cburnett");
		cburnettMenuItem.setMnemonic(KeyEvent.VK_C);
		chessMenChoiceSubMenu.add(cburnettMenuItem);

		final JMenuItem holyWarriorsMenuItem = new JMenuItem("Holy Warriors");
		cburnettMenuItem.setMnemonic(KeyEvent.VK_H);
		chessMenChoiceSubMenu.add(holyWarriorsMenuItem);

		final JMenuItem abstractMenMenuItem = new JMenuItem("Abstract Men");
		cburnettMenuItem.setMnemonic(KeyEvent.VK_A);
		chessMenChoiceSubMenu.add(abstractMenMenuItem);

		final JMenuItem fancyMenMenuItem = new JMenuItem("Fancy Men");
		cburnettMenuItem.setMnemonic(KeyEvent.VK_F);
		chessMenChoiceSubMenu.add(fancyMenMenuItem);

		final JMenuItem fancyMenMenuItem2 = new JMenuItem("Fancy Men 2");
		cburnettMenuItem.setMnemonic(KeyEvent.VK_N);
		chessMenChoiceSubMenu.add(fancyMenMenuItem2);

		cburnettMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				pieceIconPath = "art/cburnett/";
				Table.get().getBoardPanel().drawBoard(chessBoard);
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
			}
		});

		holyWarriorsMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				pieceIconPath = "art/holywarriors/";
				Table.get().getBoardPanel().drawBoard(chessBoard);
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
			}
		});

		abstractMenMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				pieceIconPath = "art/simple/";
				Table.get().getBoardPanel().drawBoard(chessBoard);
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
			}
		});

		fancyMenMenuItem2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				pieceIconPath = "art/fancy2/";
				Table.get().getBoardPanel().drawBoard(chessBoard);
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
			}
		});

		fancyMenMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				pieceIconPath = "art/fancy/";
				Table.get().getBoardPanel().drawBoard(chessBoard);
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
			}
		});

		preferencesMenu.add(chessMenChoiceSubMenu);

		chooseLegalHighlightMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Color colorChoice = JColorChooser.showDialog(Table.get().getGameFrame(),
						"Choose Legal Highlight Color", Table.get().getGameFrame().getBackground());
				if (colorChoice != null) {
					Table.get().getBoardPanel().setHighlightLegalColor(chessBoard, colorChoice);
				}
			}
		});

		final JMenuItem flipBoardMenuItem = new JMenuItem("Flip board", KeyEvent.VK_F);

		flipBoardMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				boardDirection = boardDirection.opposite();
				boardPanel.drawBoard(chessBoard);
			}
		});

		preferencesMenu.add(flipBoardMenuItem);
		preferencesMenu.addSeparator();

		final JCheckBoxMenuItem cbLegalMoveHighlighter = new JCheckBoxMenuItem("Highlight Legal Moves", true);

		cbLegalMoveHighlighter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				highlightLegalMoves = cbLegalMoveHighlighter.isSelected();
			}
		});

		preferencesMenu.add(cbLegalMoveHighlighter);

		final JCheckBoxMenuItem cbUseBookMoves = new JCheckBoxMenuItem("Use Book Moves (Requires a database set-up)",
				true);

		cbUseBookMoves.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				useBook = cbUseBookMoves.isSelected();
			}
		});

		preferencesMenu.add(cbUseBookMoves);

		return preferencesMenu;

	}

	/**
	 * Creates the multiplayer menu.
	 *
	 * @return the j menu
	 */
	private JMenu createMultiplayerMenu() {
		final JMenu multiplayerMenu = new JMenu("Multiplayer Menu");
		multiplayerMenu.setMnemonic(KeyEvent.VK_M);

		final JMenuItem server = new JMenuItem("Launch Server (for hosting)", KeyEvent.VK_S);
		server.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					serverGUI = new ServerGUI(9999);
				} catch (UnknownHostException ex) {
					ex.printStackTrace();
				}
			}
		});
		multiplayerMenu.add(server);

		final JMenuItem client = new JMenuItem("Launch Client (for connecting)", KeyEvent.VK_C);
		client.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				clientGUI = new ClientGUI("localhost", 9999);
			}
		});
		multiplayerMenu.add(client);

		final JMenuItem help = new JMenuItem("Help", KeyEvent.VK_H);
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"To host a game, click \"Launch Server\".\n\nTo connect to a game, click \"Launch Client\"\nthen enter host's ip, designated port number\nand then click connect.",
						"Multiplayer Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		multiplayerMenu.add(help);

		return multiplayerMenu;
	}

	/**
	 * Player ýnfo.
	 *
	 * @param player
	 *            the player
	 * @return the string
	 */
	@SuppressWarnings(value = { "unused" })
	private static String playerInfo(final Player player) {
		return ("Player is: " + player.getAlliance() + "\nlegal moves (" + player.getLegalMoves().size() + ") = "
				+ player.getLegalMoves() + "\ninCheck = " + player.isInCheck() + "\nisInCheckMate = "
				+ player.isInCheckMate() + "\nisCastled = " + player.isCastled()) + "\n";
	}

	/**
	 * Update game board.
	 *
	 * @param board
	 *            the board
	 */
	private void updateGameBoard(final Board board) {
		this.chessBoard = board;
	}

	/**
	 * Update computer move.
	 *
	 * @param move
	 *            the move
	 */
	private void updateComputerMove(final Move move) {
		this.computerMove = move;
	}

	/**
	 * Undo all moves.
	 */
	private void undoAllMoves() {
		for (int i = Table.get().getMoveLog().size() - 1; i >= 0; i--) {
			final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
			this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
		}
		this.computerMove = null;
		Table.get().getMoveLog().clear();
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
		Table.get().getDebugPanel().redo();
		Table.get().getDebugPanel().getTimer().reset();
	}

	/**
	 * Load PGN file.
	 *
	 * @param pgnFile
	 *            the pgn file
	 */
	private static void loadPGNFile(final File pgnFile) {
		try {
			persistPGNFile(pgnFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save PGN file.
	 *
	 * @param pgnFile
	 *            the pgn file
	 */
	private static void savePGNFile(final File pgnFile) {
		try {
			writeGameToPGNFile(pgnFile, Table.get().getMoveLog());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Undo last move.
	 */
	private void undoLastMove() {
		final Move lastMove = Table.get().getMoveLog().removeMove(Table.get().getMoveLog().size() - 1);
		this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getToBoard();
		this.computerMove = null;
		Table.get().getMoveLog().removeMove(lastMove);
		Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
		Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
		Table.get().getBoardPanel().drawBoard(chessBoard);
		Table.get().getDebugPanel().redo();
	}

	/**
	 * Move made update.
	 *
	 * @param playerType
	 *            the player type
	 */
	private void moveMadeUpdate(final PlayerType playerType) {
		setChanged();
		notifyObservers(playerType);
	}

	/**
	 * Sets the up update.
	 *
	 * @param gameSetup
	 *            the new up update
	 */
	private void setupUpdate(final GameSetup gameSetup) {
		setChanged();
		notifyObservers(gameSetup);
	}

	/**
	 * The Class TableGameAIWatcher.
	 */
	private static class TableGameAIWatcher implements Observer {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		@Override
		public void update(final Observable o, final Object arg) {
			if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer())
					&& !Table.get().getGameBoard().currentPlayer().isInCheckMate()
					&& !Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				System.out.println(Table.get().getGameBoard().currentPlayer() + " is set to AI, thinking....");
				final AIThinkTank thinkTank = new AIThinkTank();
				thinkTank.execute();
			}
			if (Table.get().getGameBoard().currentPlayer().isInCheckMate()) {
				Table.get().getDebugPanel().getTimer().stop();
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
						"Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
				Table.get().getDebugPanel().getTimer().reset();
			}
			if (Table.get().getGameBoard().currentPlayer().isInStaleMate()) {
				Table.get().getDebugPanel().getTimer().stop();
				JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
						"Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!",
						"Game Over", JOptionPane.INFORMATION_MESSAGE);
				Table.get().getDebugPanel().getTimer().reset();
			}
		}
	}

	/**
	 * The Enum PlayerType.
	 */
	public enum PlayerType {

		/** The human. */
		HUMAN,
		/** The computer. */
		COMPUTER,
		/** The another human. */
		ANOTHER_HUMAN
	}

	/**
	 * The Class AIThinkTank.
	 */
	private static class AIThinkTank extends SwingWorker<Move, String> {

		/**
		 * Instantiates a new AI think tank.
		 */
		private AIThinkTank() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Move doInBackground() throws Exception {
			final Move bestMove;
			final Move bookMove = Table.get().getUseBook() ? MySqlGamePersistence.get().getNextBestMove(
					Table.get().getGameBoard(), Table.get().getGameBoard().currentPlayer(),
					Table.get().getMoveLog().getMoves().toString().replaceAll("\\[", "").replaceAll("\\]", ""))
					: MoveFactory.getNullMove();
			if (Table.get().getUseBook() && bookMove != MoveFactory.getNullMove()) {
				bestMove = bookMove;
			} else {
				// final int moveNumber = Table.get().getMoveLog().size();
				// final int quiescenceFactor = 2000 + (100 * moveNumber);
				final StockAlphaBeta strategy = new StockAlphaBeta(Table.get().getGameSetup().getSearchDepth());
				strategy.addObserver(Table.get().getDebugPanel());
				// Table.get().getGameBoard().currentPlayer().setMoveStrategy(strategy);
				bestMove = strategy.execute(Table.get().getGameBoard());
			}
			return bestMove;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.SwingWorker#done()
		 */
		@Override
		public void done() {
			try {
				final Move bestMove = get();
				Table.get().updateComputerMove(bestMove);
				Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getToBoard());
				Table.get().getMoveLog().addMove(bestMove);
				Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
				Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
				Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
				Table.get().getDebugPanel().redo();
				Table.get().moveMadeUpdate(PlayerType.COMPUTER);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The Class BoardPanel.
	 */
	@SuppressWarnings(value = { "serial" })
	private class BoardPanel extends JPanel {

		/** The tile panel. */
		TilePanel tilePanel = new TilePanel(this, 0);

		/** The board tiles. */
		final List<TilePanel> boardTiles;

		/**
		 * Instantiates a new board panel.
		 */
		BoardPanel() {
			super(new GridLayout(8, 8));
			this.boardTiles = new ArrayList<>();
			for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
				tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			setBackground(Color.decode("#8B4726"));
			validate();
		}

		/**
		 * Draw board.
		 *
		 * @param board
		 *            the board
		 */
		void drawBoard(final Board board) {
			removeAll();
			for (final TilePanel boardTile : boardDirection.traverse(boardTiles)) {
				boardTile.drawTile(board);
				add(boardTile);
			}
			validate();
			repaint();
		}

		/**
		 * Sets the tile dark color.
		 *
		 * @param board
		 *            the board
		 * @param darkColor
		 *            the dark color
		 */
		void setTileDarkColor(final Board board, final Color darkColor) {
			for (final TilePanel boardTile : boardTiles) {
				boardTile.setDarkTileColor(darkColor);
			}
			drawBoard(board);
		}

		/**
		 * Sets the tile light color.
		 *
		 * @param board
		 *            the board
		 * @param lightColor
		 *            the light color
		 */
		void setTileLightColor(final Board board, final Color lightColor) {
			for (final TilePanel boardTile : boardTiles) {
				boardTile.setLightTileColor(lightColor);
			}
			drawBoard(board);
		}

		/**
		 * Sets the highlight legal color.
		 *
		 * @param board
		 *            the board
		 * @param highlightColor
		 *            the highlight color
		 */
		void setHighlightLegalColor(final Board board, final Color highlightColor) {
			highlightImage = colorImage(highlightImage, highlightColor);
		}

		/**
		 * Color ýmage.
		 *
		 * @param image
		 *            the image
		 * @param color
		 *            the color
		 * @return the buffered ýmage
		 */
		BufferedImage colorImage(BufferedImage image, final Color color) {
			int width = image.getWidth();
			int height = image.getHeight();
			WritableRaster raster = image.getRaster();
			for (int xx = 0; xx < width; xx++) {
				for (int yy = 0; yy < height; yy++) {
					int[] pixels = raster.getPixel(xx, yy, (int[]) null);
					pixels[0] = color.getRed();
					pixels[1] = color.getGreen();
					pixels[2] = color.getBlue();
					raster.setPixel(xx, yy, pixels);
				}
			}
			return image;
		}
	}

	/**
	 * The Enum BoardDirection.
	 */
	enum BoardDirection {

		/** The normal. */
		NORMAL {
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles) {
				return boardTiles;
			}

			@Override
			BoardDirection opposite() {
				return FLIPPED;
			}
		},

		/** The flýpped. */
		FLIPPED {
			@Override
			List<TilePanel> traverse(final List<TilePanel> boardTiles) {
				return Lists.reverse(boardTiles);
			}

			@Override
			BoardDirection opposite() {
				return NORMAL;
			}
		};

		/**
		 * Traverse.
		 *
		 * @param boardTiles
		 *            the board tiles
		 * @return the list
		 */
		abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

		/**
		 * Opposite.
		 *
		 * @return the board direction
		 */
		abstract BoardDirection opposite();
	}

	/**
	 * The Class MoveLog.
	 */
	public static class MoveLog implements Serializable {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 9096618582932043659L;

		/** The moves. */
		private final List<Move> moves;

		/**
		 * Instantiates a new move log.
		 */
		MoveLog() {
			this.moves = new ArrayList<>();
		}

		/**
		 * Gets the moves.
		 *
		 * @return the moves
		 */
		public List<Move> getMoves() {
			return this.moves;
		}

		/**
		 * Adds the move.
		 *
		 * @param move
		 *            the move
		 */
		void addMove(final Move move) {
			if (moves.size() > 1) {
				if (!move.equals(moves.get(moves.size() - 1))) {
					this.moves.add(move);
				}
			} else {
				this.moves.add(move);
				// Table.get().getDebugPanel().getTimer().reset();
				Table.get().getDebugPanel().getTimer().start();
			}
		}

		/**
		 * Size.
		 *
		 * @return the int
		 */
		public int size() {
			return this.moves.size();
		}

		/**
		 * Clear.
		 */
		void clear() {
			this.moves.clear();
		}

		/**
		 * Removes the move.
		 *
		 * @param index
		 *            the index
		 * @return the move
		 */
		Move removeMove(final int index) {
			return this.moves.remove(index);
		}

		/**
		 * Removes the move.
		 *
		 * @param move
		 *            the move
		 * @return true, if successful
		 */
		boolean removeMove(final Move move) {
			return this.moves.remove(move);
		}

	}

	/**
	 * The Class TilePanel.
	 */
	@SuppressWarnings(value = { "serial" })
	private class TilePanel extends JPanel {

		/** The tile ýd. */
		private final int tileId;

		/**
		 * Instantiates a new tile panel.
		 *
		 * @param boardPanel
		 *            the board panel
		 * @param tileId
		 *            the tile ýd
		 */
		TilePanel(final BoardPanel boardPanel, final int tileId) {
			super(new GridBagLayout());
			this.tileId = tileId;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileColor();
			assignTilePieceIcon(chessBoard);
			highlightTileBorder(chessBoard);
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(final MouseEvent event) {

					if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer())
							|| Table.get().getGameSetup().isAnotherHuman(Table.get().getGameBoard().currentPlayer())
							|| BoardUtils.isEndGame(Table.get().getGameBoard())) {
						return;
					}

					if (isRightMouseButton(event)) {
						sourceTile = null;
						destinationTile = null;
						humanMovedPiece = null;
					} else if (isLeftMouseButton(event)) {
						if (sourceTile == null) {
							sourceTile = chessBoard.getTile(tileId);
							humanMovedPiece = sourceTile.getPiece();
							if (humanMovedPiece == null) {
								sourceTile = null;
							}
						} else {
							destinationTile = chessBoard.getTile(tileId);
							final Move move = MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(),
									destinationTile.getTileCoordinate());
							final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
							if (transition.getMoveStatus().isDone()) {
								chessBoard = transition.getToBoard();
								moveLog.addMove(move);
								if (clientGUI != null) {
									clientGUI.getClient().sendMessage(
											new ChatMessage(3, FenUtilities.createFENFromGame(chessBoard)));
									clientGUI.getClient().serializeMoveLog(getMoveLog());
								} else if (serverGUI != null) {
									serverGUI.getAutoClient().getClient().sendMessage(
											new ChatMessage(3, FenUtilities.createFENFromGame(chessBoard)));
									serverGUI.getAutoClient().getClient().serializeMoveLog(getMoveLog());
								}
							}
							sourceTile = null;
							destinationTile = null;
							humanMovedPiece = null;
						}
					}
					invokeLater(new Runnable() {
						public void run() {
							gameHistoryPanel.redo(chessBoard, moveLog);
							takenPiecesPanel.redo(moveLog);
							// if(gameSetup.isAIPlayer(chessBoard.currentPlayer())
							// ||
							// gameSetup.isAnotherHuman(chessBoard.currentPlayer())){
							Table.get().moveMadeUpdate(PlayerType.HUMAN);
							// }
							boardPanel.drawBoard(chessBoard);
							debugPanel.redo();
						}
					});
				}

				@Override
				public void mouseExited(final MouseEvent e) {
				}

				@Override
				public void mouseEntered(final MouseEvent e) {
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
				}

				@Override
				public void mousePressed(final MouseEvent e) {
				}
			});
			validate();
			repaint();
		}

		/**
		 * Draw tile.
		 *
		 * @param board
		 *            the board
		 */
		void drawTile(final Board board) {
			assignTileColor();
			assignTilePieceIcon(board);
			highlightTileBorder(board);
			highlightLegals(board);
			highlightEnemyMove();
			validate();
			repaint();
		}

		/**
		 * Sets the light tile color.
		 *
		 * @param color
		 *            the new light tile color
		 */
		void setLightTileColor(final Color color) {
			lightTileColor = color;
		}

		/**
		 * Sets the dark tile color.
		 *
		 * @param color
		 *            the new dark tile color
		 */
		void setDarkTileColor(final Color color) {
			darkTileColor = color;
		}

		/**
		 * Highlight tile border.
		 *
		 * @param board
		 *            the board
		 */
		private void highlightTileBorder(final Board board) {
			if (humanMovedPiece != null && humanMovedPiece.getPieceAllegiance() == board.currentPlayer().getAlliance()
					&& humanMovedPiece.getPiecePosition() == this.tileId) {
				setBorder(BorderFactory.createLineBorder(Color.CYAN));
			} else {
				setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}
		}

		/**
		 * Highlight AI move.
		 */
		private void highlightEnemyMove() {
			if (computerMove != null) {
				if (this.tileId == computerMove.getCurrentCoordinate()) {
					setBackground(Color.PINK);
				} else if (this.tileId == computerMove.getDestinationCoordinate()) {
					setBackground(Color.RED);
				}
			}
		}

		/**
		 * Highlight legals.
		 *
		 * @param board
		 *            the board
		 */
		private void highlightLegals(final Board board) {
			if (Table.get().getHighlightLegalMoves()) {
				for (final Move move : pieceLegalMoves(board)) {
					if (move.getDestinationCoordinate() == this.tileId) {
						add(new JLabel(new ImageIcon(highlightImage)));
					}
				}
			}
		}

		/**
		 * Piece legal moves.
		 *
		 * @param board
		 *            the board
		 * @return the collection
		 */
		private Collection<Move> pieceLegalMoves(final Board board) {
			if (humanMovedPiece != null
					&& humanMovedPiece.getPieceAllegiance() == board.currentPlayer().getAlliance()) {
				return humanMovedPiece.calculateLegalMoves(board);
			}
			return Collections.emptyList();
		}

		/**
		 * Assign tile piece ýcon.
		 *
		 * @param board
		 *            the board
		 */
		private void assignTilePieceIcon(final Board board) {
			this.removeAll();
			if (board.getTile(this.tileId).isTileOccupied()) {
				try {
					final BufferedImage image = ImageIO.read(new File(pieceIconPath
							+ board.getTile(this.tileId).getPiece().getPieceAllegiance().toString().substring(0, 1) + ""
							+ board.getTile(this.tileId).getPiece().toString() + ".png"));
					add(new JLabel(new ImageIcon(image)));
				} catch (final IOException ex1) {
					try {
						final BufferedImage image = ImageIO.read(new File(pieceIconPath
								+ board.getTile(this.tileId).getPiece().getPieceAllegiance().toString().substring(0, 1)
								+ "" + board.getTile(this.tileId).getPiece().toString() + ".gif"));
						add(new JLabel(new ImageIcon(image)));
					} catch (final IOException ex2) {
						ex2.printStackTrace();
						System.out.println("ONLY .GIF OR .PNG ARE ALLOWED");
					}
				}
			}
		}

		/**
		 * Assign tile color.
		 */
		private void assignTileColor() {
			if (BoardUtils.INSTANCE.FIRST_ROW.get(this.tileId) || BoardUtils.INSTANCE.THIRD_ROW.get(this.tileId)
					|| BoardUtils.INSTANCE.FIFTH_ROW.get(this.tileId)
					|| BoardUtils.INSTANCE.SEVENTH_ROW.get(this.tileId)) {
				setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
			} else if (BoardUtils.INSTANCE.SECOND_ROW.get(this.tileId)
					|| BoardUtils.INSTANCE.FOURTH_ROW.get(this.tileId) || BoardUtils.INSTANCE.SIXTH_ROW.get(this.tileId)
					|| BoardUtils.INSTANCE.EIGHTH_ROW.get(this.tileId)) {
				setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
			}
		}
	}
}