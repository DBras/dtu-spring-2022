package project;

import java.net.Socket;
import java.util.ArrayList;

public class Game implements Runnable{
    private ArrayList<Socket> sockets;
    public Game(ArrayList<Socket> sockets) {
        this.sockets = sockets;
    }

    public void run() {
        Socket sock;
        for (int i = 0; i < this.sockets.size(); i++) {
            sock = this.sockets.get(i);
            ClientRunnable new_client = new ClientRunnable(sock);
            Thread client_thread = new Thread(new_client);
            client_thread.start(); // Start new thread when client connects
            System.out.println(sock + " was started");
        }
    }
}
