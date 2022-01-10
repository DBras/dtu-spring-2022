package project;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PlayerRunnable implements Runnable{
    private final Socket client_socket;
    private final Deck player_hand;
    private final int number_of_players;
    private int cash;

    public PlayerRunnable(Socket client_socket, int number_of_players) {
        this.client_socket = client_socket;
        this.player_hand = new Deck();
        this.number_of_players = number_of_players;
        this.cash = 300;
    }

    public void run() {
        writeToSocket(String.format("Hello! There are %d players competing", this.number_of_players));
        writeToSocket(String.format("CASH BALANCE: %d", this.cash));

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

    public void betMoney(int money) {
        this.cash = this.cash - money;
        writeToSocket(String.format("BET %d", money));
        writeToSocket(String.format("CASH BALANCE: %d", this.cash));
    }

    public void subtractMoney(int money) {
        this.cash = this.cash - money;
        writeToSocket(String.format("CASH BALANCE: %d", this.cash));
    }

    public String getOption() {
        this.writeToSocket("YOUR TURN");
        Scanner user_input = null;
        String line = "";
        boolean option_received = false;
        try {
            user_input = new Scanner(this.client_socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            option_received = true;
        }
        while (!option_received) {
            line = user_input.nextLine();
            String command = line.split(" ")[0];
            System.out.println(line);
            if (command.equals("RAISE")) {
                if (Integer.parseInt(line.split(" ")[1]) > this.cash) {
                    writeToSocket("INVALID BET");
                } else {
                    return line;
                }
            }
            else if (command.equals("CALL") || command.equals("FOLD")) {
                return line;
            } else {
                writeToSocket("INVALID OPTION");
            }
        }
        return null;
    }
}
