package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
	public static final int PORT_NUMBER = 8080; // Variable for port number
    public static void main(String[] args) { // Run on start
        Deck test_deck = new Deck();
        System.out.println(test_deck);
        test_deck.shuffle();
        System.out.println(test_deck);


        ServerSocket server_sock = null;
        boolean server_active = true;
        int connections_started = 0;
        ArrayList<Socket> current_sockets = new ArrayList<Socket>();
        try { 
            server_sock = new ServerSocket(PORT_NUMBER); // Open server on port PORT_NUMBER
            while (server_active) { // Run as long as server_active is true
                Socket client_socket = server_sock.accept(); // Accept client connection

                //ClientRunnable new_client = new ClientRunnable(client_socket);
                //Thread client_thread = new Thread(new_client);
                //client_thread.start(); // Start new thread when client connects
                connections_started++;
                current_sockets.add(client_socket);

                if (connections_started >= 2) {
                    server_active = false;
                    Game game = new Game(current_sockets);
                    Thread game_thread = new Thread(game);
                    game_thread.start();
                }
            }

        } catch (IOException e) { // Catch error in opening server socket
            e.printStackTrace();
        }
    }
}
