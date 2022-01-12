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

    /**
     * Runnable constructor. Takes a socket, a number of players in the game, an ID number and
     * a starting amount of cash
     * @param client_socket Socket to communicate with player
     * @param number_of_players Number of players in the game
     * @param ID ID number of this player
     * @param cash Starting cash
     */
    public PlayerRunnable(Socket client_socket, int number_of_players, int ID, int cash) {
        this.client_socket = client_socket;
        this.player_hand = new Deck();
        this.number_of_players = number_of_players;
        this.cash = cash;
        this.ID = ID;
    }

    /**
     * Run the player runnable. Writes number of players in the game to the player
     * as well as starting cash balance
     */
    public void run() {
        writeToSocket(String.format("Hello! There are %d players competing", this.number_of_players));
        writeToSocket(String.format("CASH BALANCE: %d", this.cash));

    }

    /**
     * Method for writing a message to the player. Converts message to string object and adds carriage return
     * @param message String of message
     */
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

    /**
     * Write players' hand to socket
     */
    public void writeHandToSocket() {
        writeCardsToSocket(this.player_hand, "HAND: ");
    }

    /**
     * Write a specified deck of cards to socket
     * @param cards Deck of cards
     * @param message Some preceding message (eg. "MIDDLE DECK: ")
     */
    public void writeCardsToSocket(Deck cards, String message) {
        writeToSocket(message + cards.toString());
    }

    /**
     * Give a card to a player. Calls the addCard() method
     * @param c Card to give to player
     */
    public void giveCard(Card c) {
        this.player_hand.addCard(c);
    }

    /**
     * Make the player bet money. Subtracts bet from player balance and writes bet to socket
     * @param money Money to bet
     */
    public void betMoney(int money) {
        writeToSocket(String.format("BET %d", money));
        this.subtractMoney(money);
    }

    /**
     * Subtract money from player if possible. Throws error if player would go negative.
     * Actual check of input validity (RAISE above cash balance) is handled in another method
     * @param money Money to subtract
     */
    public void subtractMoney(int money) {
        if (this.cash < money) {
            throw new RuntimeException("Player does not have enough cash");
        } else {
            this.cash = this.cash - money;
            writeToSocket(String.format("CASH BALANCE: %d", this.cash));
        }
    }

    /**
     * Add money to player. Then writes the new cash balance to player socket
     * @param money Money to add to player
     */
    public void addMoney(int money) {
        this.cash += money;
        writeToSocket(String.format("CASH BALANCE: %d", this.cash));
    }

    /**
     * Return the players' cash balance
     * @return Integer representing player cash balance
     */
    public int getMoney() {
        return this.cash;
    }

    /**
     * Resets player hand by setting hand to new, empty Deck
     */
    public void resetHand() {
        this.player_hand = new Deck();
    }

    /**
     * Gets the active status of player (used when folding)
     * @return
     */
    public boolean getActiveStatus() {
        return this.player_active;
    }

    /**
     * Sets the active status of player
     * @param status Boolean representing active status
     */
    public void setActiveStatus(boolean status) {
        this.player_active = status;
    }

    /**
     * Get option from player. Option is either RAISE, CALL, FOLD or CHECK. Since check is not always possible, get
     * boolean of if player can or not. Since raise must be above last call, get int of last call
     * @param current_call Integer of last call or raise value
     * @param can_check Boolean if player can check or not
     * @return Return command string
     */
    public String getOption(int current_call, boolean can_check) {
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
            else if (command.equals("CHECK") && can_check) {
                return line;
            } else {
                writeToSocket("INVALID OPTION");
            }
        }
    }
}
