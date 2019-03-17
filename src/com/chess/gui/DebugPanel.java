package com.chess.gui;

import java.io.File;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.Observer;
import java.io.IOException;
import java.util.Observable;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Located on southmost corner of the app.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class DebugPanel extends JPanel implements Observer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8690488325089027578L;

	/** The Constant CHAT_PANEL_DIMENSION. */
	private static final Dimension CHAT_PANEL_DIMENSION = new Dimension(750, 150);

	/** The Constant ELAPSED_TIME. */
	private static final String ELAPSED_TIME = " Elapsed Time: ";

	/** The AI think area. */
	private final JTextArea AIThinkArea;

	/** The time label. */
	private final JLabel timeLabel;

	/** The game timer. */
	private final GameTimer gameTimer;

	/**
	 * Instantiates a new debug panel.
	 */
	public DebugPanel() {
		super.setLayout(new BorderLayout());
		this.AIThinkArea = new JTextArea("");
		this.gameTimer = new GameTimer();
		this.timeLabel = new JLabel(ELAPSED_TIME + "00:00:00");
		this.timeLabel.setForeground(Color.decode("#593E1A"));
		this.timeLabel.setFont(new Font("Georgia", Font.ITALIC, 30));
		this.timeLabel.setBackground(Color.decode("#FFFACD"));
		timeLabel.setOpaque(true);
		AIThinkArea.setEditable(false);
		super.add(this.AIThinkArea, BorderLayout.NORTH);
		super.add(this.timeLabel, BorderLayout.CENTER);
		super.setPreferredSize(CHAT_PANEL_DIMENSION);
		super.validate();
		super.setVisible(true);
	}

	/**
	 * Redo.
	 */
	public void redo() {
		validate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(final Observable obs, final Object obj) {
		this.AIThinkArea.setText(obj.toString().trim());
		redo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		try {
			g.drawImage(ImageIO.read(new File("art/main_elements/logo.png")), 650, 20, 120, 120, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the timer.
	 *
	 * @return the timer
	 */
	public GameTimer getTimer() {
		return gameTimer;
	}

	/**
	 * Gets the timer label.
	 *
	 * @return the timer label
	 */
	public JLabel getTimerLabel() {
		return timeLabel;
	}

	/**
	 * Gets the AI think area.
	 *
	 * @return the AI think area
	 */
	public JTextArea getAIThinkArea() {
		return AIThinkArea;
	}

	/**
	 * The Class GameTimer.
	 */
	protected class GameTimer {

		/** The timer. */
		Timer timer;

		/** The second. */
		int second;

		/** The minute. */
		int minute;

		/** The hour. */
		int hour;

		/**
		 * Instantiates a new game timer.
		 */
		public GameTimer() {
			second = 0;
			minute = 0;
			hour = 0;
			timer = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (minute == 59 && second == 59) {
						hour++;
						minute = 0;
						second = 0;
					} else if (second == 59) {
						minute++;
						second = 0;
					} else {
						second++;
					}
					DebugPanel.this.timeLabel.setText(ELAPSED_TIME + gameTimer.getElapsedTime());
				}
			});
			// timer.start();
		}

		/**
		 * Start.
		 */
		public void start() {
			if (timer != null) {
				timer.start();
			}
		}

		/**
		 * Stop.
		 */
		public void stop() {
			if (timer != null)
				timer.stop();
		}

		/**
		 * Reset.
		 */
		public void reset() {
			if (timer != null) {
				hour = 0;
				minute = 0;
				second = 0;
			}
		}

		/**
		 * Checks if is ýn ýnitial state.
		 *
		 * @return true, if is ýn ýnitial state
		 */
		public boolean isInInitialState() {
			return timer != null && second == 0 && minute == 0 && hour == 0;
		}

		/**
		 * Gets the elapsed time.
		 *
		 * @return the elapsed time
		 */
		public String getElapsedTime() {
			String newHour;
			String newMinute;
			String newSecond;
			if (hour < 10) {
				newHour = "0" + hour;
			} else {
				newHour = "" + hour;
			}
			if (minute < 10) {
				newMinute = "0" + minute;
			} else {
				newMinute = "" + minute;
			}
			if (second < 10) {
				newSecond = "0" + second;
			} else {
				newSecond = "" + second;
			}
			return newHour + ":" + newMinute + ":" + newSecond;
		}
	}
}