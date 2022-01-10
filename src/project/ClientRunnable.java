package project;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientRunnable implements Runnable{
    private Socket client_socket;
    private Deck player_hand;
    private int number_of_players;

    public ClientRunnable(Socket client_socket, int number_of_players) {
        this.client_socket = client_socket;
        this.player_hand = new Deck();
        this.number_of_players = number_of_players;
    }

    public void run() {
        writeToSocket(String.format("Hello! There are %d players competing", this.number_of_players));

    }

    public void writeToSocket(String message) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(this.client_socket.getOutputStream());
            message += "\r\n";
            bos.write(message.getBytes(StandardCharsets.UTF_8));
            bos.flush();
        } catch (IOException e) {
            //
        }
    }

    public void writeHandToSocket() {
        writeToSocket(this.player_hand.toString());
    }

    public void giveCard(Card c) {
        this.player_hand.addCard(c);
    }
}
