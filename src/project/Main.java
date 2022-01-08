package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket server_sock = null;
        boolean server_active = true;
        try {
            server_sock = new ServerSocket(8080);
            while (server_active) {
                Socket client_socket = server_sock.accept();

                ClientRunnable new_client = new ClientRunnable();
                Thread client_thread = new Thread(new_client);
                client_thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
