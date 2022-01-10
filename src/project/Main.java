package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
	public static final int PORT_NUMBER = 8080; // Variable for port number
    public static final int MAX_PLAYERS = 4;
    public static void main(String[] args) { // Run on start
        ServerSocket server_sock;
        boolean server_active = true;
        ArrayList<Socket> current_sockets = new ArrayList<>();
        try { 
            server_sock = new ServerSocket(PORT_NUMBER); // Open server on port PORT_NUMBER
            while (server_active) { // Run as long as server_active is true
                Socket client_socket = server_sock.accept(); // Accept client connection
                current_sockets.add(client_socket);
                System.out.println("Test");

                if (current_sockets.size() >= MAX_PLAYERS) {
                    Game game = new Game(current_sockets);
                    Thread game_thread = new Thread(game);
                    game_thread.start();
                    current_sockets = new ArrayList<>();
                }
            }
        } catch (IOException e) { // Catch error in opening server socket
            e.printStackTrace();
        }
    }
}
