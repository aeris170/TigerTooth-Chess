package network;

import java.awt.Color;
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
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

import com.chess.gui.Table;
import com.chess.gui.Table.PlayerType;

/**
 * In-line comments are provided along with Doc-comments on this class due to
 * Socket programming being a hard thing to understand at first glance. This
 * class is the clientGUI class. Responsible for supplying a GUI for clients.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class ClientGUI extends JFrame implements ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The label. */
	// will first hold "Username:", later on "Enter message"
	private JLabel label;

	/** The tf. */
	// to hold the Username and later on the messages
	private JTextField tf;

	/** The tf port. */
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;

	/** The who ýs ýn. */
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;

	/** The ta. */
	// for the chat room
	private JTextArea ta;

	/** The connected. */
	// if it is for connection
	private boolean connected;

	/** The client. */
	// the Client object
	private Client client;

	/** The default port. */
	// the default port number
	private int defaultPort;

	/** The default host. */
	private String defaultHost;

	/**
	 * Instantiates a new client GUI.
	 *
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	// Constructor connection receiving a socket number
	public ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;

		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(3, 1));
		// the server name and the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
		// the two JTextField with default value for server address and port
		// number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your username below (DEFAULT GUEST)", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("guest");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1, 1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false); // you have to login before being able to
									// logout
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false); // you have to login before being able to Who
									// is in

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}

	/**
	 * Append.
	 *
	 * @param str
	 *            the str
	 */
	// called by the Client to append text in the TextArea
	void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}

	// called by the GUI is the connection failed
	/**
	 * Connection failed.
	 */
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your username below (DEFAULT GUEST)");
		tf.setText("guest");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(true);
		tfPort.setEditable(true);
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		connected = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/*
	 * Button or JTextField clicked
	 */
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if (o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			return;
		}
		// if it the who is in button
		if (o == whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
			return;
		}
		// ok it is coming from the JTextField
		if (connected) {
			// just have to send the message
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tf.getText()));
			tf.setText("");
			return;
		}
		if (o == login) {
			// ok it is a connection request
			String username = tf.getText().trim();
			// empty username ignore it
			if (username.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if (server.length() == 0)
				return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if (portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			} catch (Exception en) {
				return; // nothing I can do if port number is not valid
			}
			// try creating a new Client with GUI
			client = new Client(server, port, username, this);
			// test if we can start the Client
			if (!client.start())
				return;
			tf.setText("");
			label.setText("Enter your message below");
			connected = true;
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
			Table.get().getGameSetup().setWhitePlayerType(PlayerType.ANOTHER_HUMAN);
		}
	}

	/**
	 * Auto connect.
	 *
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	public void autoConnect() throws UnknownHostException {
		client = new Client(InetAddress.getLocalHost().getHostAddress(), 9999, "host", this);
		if (!client.start())
			return;
		tf.setText("");
		label.setText("Enter your message below");
		connected = true;
		login.setEnabled(false);
		logout.setEnabled(true);
		whoIsIn.setEnabled(true);
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tf.addActionListener(this);
		Table.get().getGameSetup().setBlackPlayerType(PlayerType.ANOTHER_HUMAN);
	}

	/**
	 * Gets the client.
	 *
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 9999);
	}
}