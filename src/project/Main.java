package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	public static final int PORT_NUMBER = 8080; // Variable for port number
    public static void main(String[] args) { // Run on start
        ServerSocket server_sock = null;
        boolean server_active = true;
        try { 
            server_sock = new ServerSocket(PORT_NUMBER); // Open server on port PORT_NUMBER
            while (server_active) { // Run as long as server_active is true
                Socket client_socket = server_sock.accept(); // Accept client connection

                ClientRunnable new_client = new ClientRunnable(client_socket);
                Thread client_thread = new Thread(new_client);
                client_thread.start(); // Start new thread when client connects
            }

        } catch (IOException e) { // Catch error in opening server socket
            e.printStackTrace();
        }
    }
}
