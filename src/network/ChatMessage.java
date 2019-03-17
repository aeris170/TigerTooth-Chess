package network;

import java.io.Serializable;

import com.chess.gui.Table.MoveLog;

/**
 * Responsible for classification of messages sent between server and client.
 *
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class ChatMessage implements Serializable {

	/** The Constant serialVersionUID. */
	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	// FEN is an FEN string
	// MOVE_LOG is list of the moves made
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2, FEN = 3, MOVE_LOG = 4;

	/** The type. */
	private int type;

	/** The message. */
	private String message;

	/** The log. */
	private MoveLog log;

	/**
	 * Instantiates a new chat message.
	 *
	 * @param type
	 *            the type
	 * @param message
	 *            the message
	 */
	// constructor
	public ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}

	/**
	 * Instantiates a new chat message.
	 *
	 * @param type
	 *            the type
	 * @param log
	 *            the log
	 */
	public ChatMessage(int type, MoveLog log) {
		this.type = type;
		this.log = log;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	// getters
	int getType() {
		return type;
	}

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	String getMessage() {
		return message;
	}

	/**
	 * Gets the log.
	 *
	 * @return the log
	 */
	MoveLog getLog() {
		return log;
	}
}