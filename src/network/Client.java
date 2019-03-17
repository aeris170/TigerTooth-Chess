package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.chess.gui.Table;
import com.chess.gui.Table.MoveLog;

/**
 * In-line comments are provided along with Doc-comments on this class due to
 * Socket programming being a hard thing to understand at first glance. This
 * class is the client class. Responsible for retrieving messages from the
 * server connected.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class Client {

	/** The s ýnput. */
	// for I/O
	private ObjectInputStream sInput; // to read from the socket

	/** The s output. */
	private ObjectOutputStream sOutput; // to write on the socket

	/** The socket. */
	private Socket socket;

	/** The cg. */
	// if I use a GUI or not
	private ClientGUI cg;

	/** The username. */
	// the server, the port and the username
	private String server, username;

	/** The port. */
	private int port;

	/**
	 * Instantiates a new client.
	 *
	 * @param server the server
	 * @param port the port
	 * @param username the username
	 */
	/* Constructor called by console mode server: the server address port: the port
	 * number username: the username */
	Client(String server, int port, String username) {
		// which calls the common constructor with the GUI set to null
		this(server, port, username, null);
	}

	/**
	 * Instantiates a new client.
	 *
	 * @param server the server
	 * @param port the port
	 * @param username the username
	 * @param cg the cg
	 */
	/* Constructor call when used from a GUI in console mode the ClienGUI parameter
	 * is null */
	Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		// save if we are in GUI mode or not
		this.cg = cg;
	}

	/**
	 * Start.
	 *
	 * @return true, if successful
	 */
	/* To start the dialog */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		}
		// if it failed not much I can so
		catch (Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		/* Creating both Data Stream */
		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try {
			sOutput.writeObject(username);
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/**
	 * Display.
	 *
	 * @param msg the msg
	 */
	/* To send a message to the console or the GUI */
	private void display(String msg) {
		if (cg == null)
			System.out.println(msg); // println in console mode
		else
			cg.append(msg + "\n"); // append to the ClientGUI JTextArea (or
			                       // whatever)
	}

	/**
	 * Send message.
	 *
	 * @param msg the msg
	 */
	/* To send a message to the server */
	public void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/**
	 * Disconnect.
	 */
	/* When something goes wrong Close the Input/Output streams and disconnect not
	 * much to do in the catch clause */
	private void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {} // not much else I can do
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {} // not much else I can do
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {} // not much else I can do

		// inform the GUI
		if (cg != null)
			cg.connectionFailed();

	}

	/**
	 * Serialize move log.
	 *
	 * @param log the log
	 */
	public synchronized void serializeMoveLog(MoveLog log) {
		try {
			sOutput.writeObject(new ChatMessage(ChatMessage.MOVE_LOG, log));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Deserialize move log.
	 *
	 * @param log the log
	 */
	public synchronized void deserializeMoveLog(MoveLog log) {
		Table.get().setMoveLog(log);
		Table.get().synchronize();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	/* To start the Client in console mode use one of the following command > java
	 * Client > java Client username > java Client username portNumber > java Client
	 * username portNumber serverAddress at the console prompt If the portNumber is
	 * not specified 1500 is used If the serverAddress is not specified "localHost"
	 * is used If the username is not specified "Anonymous" is used > java Client is
	 * equivalent to > java Client Anonymous 1500 localhost are eqquivalent In
	 * console mode, if an error occurs the program simply stops when a GUI id used,
	 * the GUI is informed of the disconnection */
	public static void main(String[] args) {
		// default values
		int portNumber = 1500;
		String serverAddress = "localhost";
		String userName = "Anonymous";

		// depending of the number of arguments provided we fall through
		switch (args.length) {
			// > javac Client username portNumber serverAddr
			case 3:
				serverAddress = args[2];
				// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
				// > javac Client username
			case 1:
				userName = args[0];
				// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
				return;
		}
		// create the Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// test if we can start the connection to the Server
		// if it failed nothing we can do
		if (!client.start())
			return;

		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user
		while (true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if (msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// break to do the disconnect
				break;
			}
			// message WhoIsIn
			else if (msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
			} else { // default to ordinary message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// done disconnect
		scan.close();
		client.disconnect();
	}

	/**
	 * The Class ListenFromServer.
	 */
	/* a class that waits for the message from the server and append them to the
	 * JTextArea if we have a GUI or simply System.out.println() it in console
	 * mode */
	class ListenFromServer extends Thread {

		/* (non-Javadoc)
		 * @see java.lang.Thread#run() */
		public void run() {
			while (true) {
				try {
					Object data = sInput.readObject();
					String msg = null;
					MoveLog log = null;
					try {
						msg = (String) data;
					} catch (ClassCastException ex) {
						log = (MoveLog) data;
					}
					if (msg != null) {
						if (msg.startsWith("#F#")) {
							Table.get().updateByFEN(msg.substring(3));
						} else {
							// if console mode print the message and add back
							// the prompt
							if (cg == null) {
								System.out.println(msg);
								System.out.print("> ");
							} else {
								cg.append(msg);
							}
						}
					} else {
						deserializeMoveLog(log);
					}
				} catch (IOException e) {
					display("Server has close the connection: " + e);
					if (cg != null)
						cg.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch (ClassNotFoundException e2) {}
			}
		}
	}
}