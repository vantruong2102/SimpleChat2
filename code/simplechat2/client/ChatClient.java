// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package simplechat2.client;

import ocsf.client.*;
import simplechat2.common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	private String loginId;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

//	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
//		super(host, port); // Call the superclass constructor
//		this.clientUI = clientUI;
//		openConnection();
//	}

	public ChatClient(String host, int port, String loginId, ChatIF clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		this.loginId = loginId;
		openConnection();
	}
	
	@Override
	protected void connectionEstablished()
	{
	    try {
	        sendToServer("#login " + loginId);
	    }
	    catch (IOException e) {
	        clientUI.display("Cannot send login ID: " + e.getMessage());
	    }
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(String message) {
		if (message == null) {
			quit();
			return;
		}

		String[] parts = message.trim().split("\\s+", 2);
		String command = parts[0].toLowerCase();
		String argument = parts.length > 1 ? parts[1].trim() : "";

		try {
			switch (command) {

			case "#quit":
				quit();
				break;

			case "#logoff":
				if (isConnected()) {
					closeConnection();
					clientUI.display("Disconnected from server.");
				} else {
					clientUI.display("Already disconnected.");
				}
				break;

			case "#login":
				if (!isConnected()) {
					openConnection();
					clientUI.display("Connected to server.");
				} else {
					clientUI.display("Already connected.");
				}
				break;

			case "#sethost":
				if (isConnected()) {
					clientUI.display("Please logoff first.");
				} else if (argument.isEmpty()) {
					clientUI.display("Usage: #sethost <host>");
				} else {
					setHost(argument);
					clientUI.display("Host changed to " + getHost());
				}
				break;

			case "#setport":
				if (isConnected()) {
					clientUI.display("Please logoff first.");
				} else {
					try {
						int port = Integer.parseInt(argument);
						if (port < 1 || port > 65535) {
							clientUI.display("Invalid port.");
						} else {
							setPort(port);
							clientUI.display("Port changed to " + getPort());
						}
					} catch (NumberFormatException e) {
						clientUI.display("Invalid port number.");
					}
				}
				break;

			case "#gethost":
				clientUI.display("Host: " + getHost());
				break;

			case "#getport":
				clientUI.display("Port: " + getPort());
				break;

			default:
				if (message.startsWith("#")) {
					clientUI.display("Unknown command.");
				} else if (isConnected()) {
					sendToServer(message);
				} else {
					clientUI.display("Not connected. Use #login.");
				}
				break;
			}
		} catch (IOException e) {
			clientUI.display("Connection error: " + e.getMessage());
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}
//End of ChatClient class
