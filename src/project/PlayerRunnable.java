package project;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlayerRunnable implements Runnable{
    private Socket client_socket;
    private Deck player_hand;
    private int number_of_players;

    public PlayerRunnable(Socket client_socket, int number_of_players) {
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

    public void writeCardsToSocket() { // Method overloading for easier use
        writeCardsToSocket(this.player_hand);
    }

    public void writeCardsToSocket(Deck cards) {
        writeToSocket(cards.toString());
    }

    public void giveCard(Card c) {
        this.player_hand.addCard(c);
    }
}
