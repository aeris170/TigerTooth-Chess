package network;

import java.util.Date;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;

import com.chess.gui.Table.MoveLog;

/**
 * In-line comments are provided along with Doc-comments on this class due to
 * Socket programming being a hard thing to understand at first glance. This
 * class is the server class. Responsible for sending messages to the clients
 * connected.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class Server {

	/** The unique ýd. */
	// a unique ID for each connection
	private static int uniqueId;

	/** The al. */
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;

	/** The sg. */
	// if I am in a GUI
	private ServerGUI sg;

	/** The sdf. */
	// to display time
	private SimpleDateFormat sdf;

	/** The port. */
	// the port number to listen for connection
	private int port;

	/** The keep going. */
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;

	/**
	 * Instantiates a new server.
	 *
	 * @param port
	 *            the port
	 */
	/*
	 * server constructor that receive the port to listen to for connection as
	 * parameter in console
	 */
	public Server(int port) {
		this(port, null);
	}

	/**
	 * Instantiates a new server.
	 *
	 * @param port
	 *            the port
	 * @param sg
	 *            the sg
	 */
	public Server(int port, ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
	}

	/**
	 * Start.
	 */
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try {
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while (keepGoing) {
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept(); // accept connection
				// if I was asked to stop
				if (!keepGoing)
					break;
				ClientThread t = new ClientThread(socket); // make a thread of
															// it
				al.add(t); // save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for (int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					} catch (IOException ioE) {
						// not much I can do
					}
				}
			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}

	/**
	 * Stop.
	 */
	/*
	 * For the GUI to stop the server
	 */
	@SuppressWarnings("resource")
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		} catch (Exception e) {
			// nothing I can really do
		}
	}

	/**
	 * Display.
	 *
	 * @param msg
	 *            the msg
	 */
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if (sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}

	/**
	 * Broadcast.
	 *
	 * @param message
	 *            the message
	 */
	/*
	 * to broadcast a message to all Clients
	 */
	private synchronized void broadcast(String message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		// display message on console or GUI
		if (sg == null)
			System.out.print(messageLf);
		else
			sg.appendRoom(messageLf); // append in the room window

		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for (int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if (!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	/**
	 * Removes the.
	 *
	 * @param id
	 *            the id
	 */
	// for a client who log-off using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for (int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if (ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	/*
	 * To run as a console application just open a console window and: > java
	 * Server > java Server portNumber If the port number is not specified 1500
	 * is used
	 */
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified
		int portNumber = 1500;
		switch (args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Server [portNumber]");
				return;
			}
		case 0:
			break;
		default:
			System.out.println("Usage is: > java Server [portNumber]");
			return;

		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/**
	 * One instance of this thread will run for each client.
	 */
	class ClientThread extends Thread {

		/** The socket. */
		// the socket where to listen/talk
		Socket socket;

		/** The s ýnput. */
		ObjectInputStream sInput;

		/** The s output. */
		ObjectOutputStream sOutput;

		/** The id. */
		// my unique id (easier for deconnection)
		int id;

		/** The username. */
		// the Username of the Client
		String username;

		/** The cm. */
		// the only type of message a will receive
		ChatMessage cm;

		/** The date. */
		// the date I connect
		String date;

		/**
		 * Instantiates a new client thread.
		 *
		 * @param socket
		 *            the socket
		 */
		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try {
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			} catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while (keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				} catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;
				} catch (ClassNotFoundException e2) {
					break;
				}
				// the message part of the ChatMessage
				String message = cm.getMessage();

				MoveLog log = cm.getLog();

				// Switch on the type of message receive
				switch (cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// scan al the users connected
					for (int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				case ChatMessage.FEN:
					sendFEN(message);
					break;
				case ChatMessage.MOVE_LOG:
					sendLog(log);
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}

		/**
		 * Send log.
		 *
		 * @param message
		 *            the message
		 */
		private synchronized void sendLog(MoveLog message) {
			for (int i = al.size(); --i >= 0;) {
				ClientThread ct = al.get(i);
				// try to write to the Client if it fails remove it from the
				// list
				if (!ct.writeMoveLog(message)) {
					al.remove(i);
					display("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}

		/**
		 * Send FEN.
		 *
		 * @param FEN
		 *            the FEN
		 */
		public synchronized void sendFEN(String FEN) {
			for (int i = al.size(); --i >= 0;) {
				ClientThread ct = al.get(i);
				// try to write to the Client if it fails remove it from the
				// list
				if (!ct.writeMsg("#F#" + FEN)) {
					al.remove(i);
					display("Disconnected Client " + ct.username + " removed from list.");
				}
			}
		}

		/**
		 * Close.
		 */
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if (sOutput != null)
					sOutput.close();
			} catch (Exception e) {
			}
			try {
				if (sInput != null)
					sInput.close();
			} catch (Exception e) {
			}
			;
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
			}
		}

		/**
		 * Write msg.
		 *
		 * @param msg
		 *            the msg
		 * @return true, if successful
		 */
		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if (!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch (IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}

		/**
		 * Write move log.
		 *
		 * @param log
		 *            the log
		 * @return true, if successful
		 */
		private boolean writeMoveLog(MoveLog log) {
			// if Client is still connected send the message to it
			if (!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(log);
			}
			// if an error occurs, do not abort just inform the user
			catch (IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}