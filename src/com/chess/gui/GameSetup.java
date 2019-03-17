package com.chess.gui;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.GridLayout;
import javax.swing.JSpinner;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.SpinnerModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SpinnerNumberModel;

import com.chess.gui.Table.PlayerType;
import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.player.Player;

/**
 * A Setup screen for single-player games.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class GameSetup extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4112765171421579935L;

	/** The white player type. */
	private PlayerType whitePlayerType;

	/** The black player type. */
	private PlayerType blackPlayerType;

	/** The search depth spinner. */
	private JSpinner searchDepthSpinner;

	/** The Constant HUMAN_TEXT. */
	private static final String HUMAN_TEXT = "Human";

	/** The Constant COMPUTER_TEXT. */
	private static final String COMPUTER_TEXT = "Computer";

	/**
	 * Instantiates a new game setup.
	 *
	 * @param frame
	 *            the frame
	 * @param modal
	 *            the modal
	 */
	public GameSetup(final JFrame frame, final boolean modal) {
		super(frame, modal);
		final JPanel myPanel = new JPanel(new GridLayout(0, 1));
		final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
		final JRadioButton whiteComputerButton = new JRadioButton(COMPUTER_TEXT);
		final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
		final JRadioButton blackComputerButton = new JRadioButton(COMPUTER_TEXT);
		whiteHumanButton.setActionCommand(HUMAN_TEXT);
		final ButtonGroup whiteGroup = new ButtonGroup();
		whiteGroup.add(whiteHumanButton);
		whiteGroup.add(whiteComputerButton);
		whiteHumanButton.setSelected(true);

		final ButtonGroup blackGroup = new ButtonGroup();
		blackGroup.add(blackHumanButton);
		blackGroup.add(blackComputerButton);
		blackHumanButton.setSelected(true);

		getContentPane().add(myPanel);
		myPanel.add(new JLabel("White"));
		myPanel.add(whiteHumanButton);
		myPanel.add(whiteComputerButton);
		myPanel.add(new JLabel("Black"));
		myPanel.add(blackHumanButton);
		myPanel.add(blackComputerButton);

		this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth(AI difficulty [1-8])",
				new SpinnerNumberModel(3, 1, 8, 1));

		final JButton cancelButton = new JButton("Cancel");
		final JButton okButton = new JButton("OK");

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whitePlayerType = whiteComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
				blackPlayerType = blackComputerButton.isSelected() ? PlayerType.COMPUTER : PlayerType.HUMAN;
				GameSetup.this.setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Cancel");
				GameSetup.this.setVisible(false);
			}
		});

		myPanel.add(cancelButton);
		myPanel.add(okButton);

		setLocationRelativeTo(frame);
		pack();
		setVisible(false);
	}

	/**
	 * Prompt user.
	 */
	public void promptUser() {
		setVisible(true);
		repaint();
	}

	/**
	 * Checks if is AI player.
	 *
	 * @param player
	 *            the player
	 * @return true, if is AI player
	 */
	public boolean isAIPlayer(final Player player) {
		if (player.getAlliance() == Alliance.WHITE) {
			return getWhitePlayerType() == PlayerType.COMPUTER;
		}
		return getBlackPlayerType() == PlayerType.COMPUTER;
	}

	public boolean isAnotherHuman(final Player player) {
		if (player.getAlliance() == Alliance.WHITE) {
			return getWhitePlayerType() == PlayerType.ANOTHER_HUMAN;
		}
		return getBlackPlayerType() == PlayerType.ANOTHER_HUMAN;
	}

	/**
	 * Gets the white player type.
	 *
	 * @return the white player type
	 */
	public PlayerType getWhitePlayerType() {
		return this.whitePlayerType;
	}

	/**
	 * Gets the black player type.
	 *
	 * @return the black player type
	 */
	public PlayerType getBlackPlayerType() {
		return this.blackPlayerType;
	}

	/**
	 * Sets the white player type.
	 */
	public void setWhitePlayerType(PlayerType type) {
		this.whitePlayerType = type;
	}

	/**
	 * Sets the black player type.
	 */
	public void setBlackPlayerType(PlayerType type) {
		this.blackPlayerType = type;
	}

	/**
	 * Adds the labeled spinner.
	 *
	 * @param c
	 *            the c
	 * @param label
	 *            the label
	 * @param model
	 *            the model
	 * @return the j spinner
	 */
	private static JSpinner addLabeledSpinner(final Container c, final String label, final SpinnerModel model) {
		final JLabel l = new JLabel(label);
		c.add(l);
		final JSpinner spinner = new JSpinner(model);
		l.setLabelFor(spinner);
		c.add(spinner);
		return spinner;
	}

	/**
	 * Gets the search depth.
	 *
	 * @return the search depth
	 */
	public int getSearchDepth() {
		return (Integer) this.searchDepthSpinner.getValue();
	}
}