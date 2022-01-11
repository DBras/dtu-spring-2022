package project;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PlayerRunnable implements Runnable{
    private final Socket client_socket;
    private Deck player_hand;
    private final int number_of_players;
    private int cash;
    private boolean player_active;
    public int ID;

    public PlayerRunnable(Socket client_socket, int number_of_players, int ID) {
        this.client_socket = client_socket;
        this.player_hand = new Deck();
        this.number_of_players = number_of_players;
        this.cash = 300;
        this.ID = ID;
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

    public void writeHandToSocket() {
        writeCardsToSocket(this.player_hand, "HAND: ");
    }

    public void writeCardsToSocket(Deck cards, String message) {
        writeToSocket(message + cards.toString());
    }

    public void giveCard(Card c) {
        this.player_hand.addCard(c);
    }

    public void betMoney(int money) {
        writeToSocket(String.format("BET %d", money));
        this.subtractMoney(money);
    }

    public void subtractMoney(int money) {
        if (this.cash < money) {
            throw new RuntimeException("Player does not have enough cash");
        } else {
            this.cash = this.cash - money;
            writeToSocket(String.format("CASH BALANCE: %d", this.cash));
        }
    }

    public void addMoney(int money) {
        this.cash += money;
        writeToSocket(String.format("CASH BALANCE: %d", this.cash));
    }

    public int getMoney() {
        return this.cash;
    }

    public void resetHand() {
        this.player_hand = new Deck();
    }

    public boolean getActiveStatus() {
        return this.player_active;
    }

    public void setActiveStatus(boolean status) {
        this.player_active = status;
    }

    public String getOption(int current_call, boolean can_call) {
        this.writeToSocket("YOUR TURN");
        Scanner user_input = null;
        String line = "";
        try {
            user_input = new Scanner(this.client_socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            line = user_input.nextLine();
            String command = line.split(" ")[0];
            if (command.equals("RAISE")) {
                int bet = Integer.parseInt(line.split(" ")[1]);
                if (bet > this.cash || bet < current_call) {
                    writeToSocket("INVALID BET");
                } else {
                    return line;
                }
            }
            else if (command.equals("CALL")) {
                if (current_call > this.cash) {
                    writeToSocket("INVALID BET");
                }
                else {
                    return line;
                }
            }
            else if (command.equals("FOLD")) {
                return line;
            }
            else if (command.equals("CHECK") && can_call) {
                return line;
            } else {
                writeToSocket("INVALID OPTION");
            }
        }
    }
}
