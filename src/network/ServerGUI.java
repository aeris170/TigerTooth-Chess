package network;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.net.InetAddress;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.WindowConstants;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.net.UnknownHostException;

/**
 * In-line comments are provided along with Doc-comments on this class due to
 * Socket programming being a hard thing to understand at first glance. This
 * class is the serverGUI class. Responsible for supplying a GUI for the server.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The stop start. */
	// the stop and start buttons
	private JButton stopStart;

	/** The event. */
	// JTextArea for the chat room and the events
	private JTextArea chat, event;

	/** The t port number. */
	// The port number
	private JTextField tPortNumber;

	/** The server. */
	// my server
	private Server server;

	/** The client that will automatically connect. */
	// my client
	private ClientGUI autoClient;

	// server constructor that receive the port to listen to for connection as
	/**
	 * Instantiates a new server GUI.
	 *
	 * @param port
	 *            the port
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	// parameter
	public ServerGUI(int port) throws UnknownHostException {
		super("Chat Server");
		server = new Server(9999, this);
		// in the NorthPanel the PortNumber the Start and Stop buttons
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);
		// to stop or start the server, we start with "Start"
		stopStart = new JButton("Stop");
		stopStart.addActionListener(this);
		tPortNumber.setEditable(false);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		// the event and chat room
		JPanel center = new JPanel(new GridLayout(2, 1));
		chat = new JTextArea(80, 80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80, 80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));
		add(center);

		// need to be informed when the user click the close button on the frame
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		addWindowListener(this);
		setSize(400, 600);
		// setVisible(true);

		new ServerRunning().start();
		autoClient = new ClientGUI(InetAddress.getLocalHost().getHostAddress(), 9999);
		autoClient.autoConnect();
	}

	// append message to the two JTextArea
	/**
	 * Append room.
	 *
	 * @param str
	 *            the str
	 */
	// position at the end
	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}

	/**
	 * Append event.
	 *
	 * @param str
	 *            the str
	 */
	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if (server != null) {
			server.stop();
			server = null;
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			return;
		}
		// OK start the server
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		} catch (Exception er) {
			appendEvent("Invalid port number");
			return;
		}
		// ceate a new Server
		server = new Server(port, this);
		// and start it as a thread
		new ServerRunning().start();
		stopStart.setText("Stop");
		tPortNumber.setEditable(false);
	}

	/**
	 * The main method.
	 *
	 * @param arg
	 *            the arguments
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	// entry point to start the Server
	public static void main(String[] arg) throws UnknownHostException {
		// start server default port 9999
		new ServerGUI(9999);
	}

	/**
	 * Gets the auto client.
	 *
	 * @return the auto client
	 */
	public ClientGUI getAutoClient() {
		return autoClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	/*
	 * If the user click the X button to close the application I need to close
	 * the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if (server != null) {
			try {
				server.stop(); // ask the server to close the conection
			} catch (Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.
	 * WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.
	 * WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	/**
	 * The Class ServerRunning.
	 */
	/*
	 * A thread to run the Server
	 */
	class ServerRunning extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			server.start(); // should execute until if fails
			// the server failed
			stopStart.setText("Start");
			tPortNumber.setEditable(true);
			appendEvent("Server crashed\n");
			server = null;
		}
	}
}