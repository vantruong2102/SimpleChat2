package simplechat2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole {
	private EchoServer server;

    public ServerConsole(int port) {
        server = new EchoServer(port);

        try {
            server.listen();
        } catch (IOException e) {
            System.out.println("Cannot start server: " + e.getMessage());
        }
    }

    public void accept() {
        try {
            BufferedReader fromConsole =
                new BufferedReader(new InputStreamReader(System.in));

            String message;

            while ((message = fromConsole.readLine()) != null) {

                if (message.equalsIgnoreCase("#quit")) {
                    server.close();
                    System.exit(0);
                }
                else if (message.equalsIgnoreCase("#stop")) {
                    server.stopListening();
                }
                
                else if (message.toLowerCase().startsWith("#setport ")) {
                    try {
                        int newPort = Integer.parseInt(
                            message.substring(9).trim()
                        );

                        if (server.isListening()
                                || server.getNumberOfClients() > 0) {
                            System.out.println(
                                "Please close the server before changing port."
                            );
                        }
                        else if (newPort < 1 || newPort > 65535) {
                            System.out.println("Invalid port!");
                        }
                        else {
                            server.setPort(newPort);
                            System.out.println(
                                "Server port changed to " + newPort
                            );
                        }
                    }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid port number!");
                    }
                }
                
                else if (message.equalsIgnoreCase("#start")) {
                    if (!server.isListening()) {
                        server.listen();
                    }
                    else {
                        System.out.println("Server is already listening.");
                    }
                }
                else if (message.equalsIgnoreCase("#close")) {
                    server.close();
                }
                else {
                    server.sendToAllClients("SERVER MSG> " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Server console error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = EchoServer.DEFAULT_PORT;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        ServerConsole console = new ServerConsole(port);
        console.accept();
    }

}
