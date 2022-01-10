package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
	public static final int PORT_NUMBER = 8080; // Variable for port number
    public static void main(String[] args) { // Run on start
        ServerSocket server_sock = null;
        boolean server_active = true;
        ArrayList<Socket> current_sockets = new ArrayList<Socket>();
        try { 
            server_sock = new ServerSocket(PORT_NUMBER); // Open server on port PORT_NUMBER
            while (server_active) { // Run as long as server_active is true
                Socket client_socket = server_sock.accept(); // Accept client connection
                current_sockets.add(client_socket);
                System.out.println(current_sockets.size());

                if (current_sockets.size() >= 2) {
                    Game game = new Game(current_sockets);
                    Thread game_thread = new Thread(game);
                    game_thread.start();
                    current_sockets = new ArrayList<Socket>();
                }
            }
        } catch (IOException e) { // Catch error in opening server socket
            e.printStackTrace();
        }
    }
}
