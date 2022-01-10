package project;

import java.net.Socket;
import java.util.ArrayList;

public class Game implements Runnable{
    private ArrayList<Socket> sockets;
    private ArrayList<PlayerRunnable> players;
    private Deck card_deck;
    private Deck middle_deck = new Deck();
    private int pot_cash = 0;
    private final int MINIMUMBET = 50;

    public Game(ArrayList<Socket> sockets) {
        this.sockets = sockets;
        this.players = new ArrayList<>();
        this.card_deck = new Deck();
        this.card_deck.initSortedDeck();
        this.card_deck.shuffle();
    }

    public void run() {
        System.out.println(this.card_deck);

        Socket sock;
        for (int i = 0; i < this.sockets.size(); i++) {
            sock = this.sockets.get(i);
            PlayerRunnable new_client = new PlayerRunnable(sock, this.sockets.size());
            Thread client_thread = new Thread(new_client);
            client_thread.start(); // Start new thread when client connects
            this.players.add(new_client);
            System.out.println(sock + " was started");
        }

        dealPlayerCards(2);
        dealMiddleCards(3);
        this.players.get(0).betMoney(MINIMUMBET / 2);
        this.players.get(1).betMoney(MINIMUMBET);
        this.players.get(3).subtractMoney(150);
        addToPot(MINIMUMBET / 2 + MINIMUMBET);
        int current_call = MINIMUMBET;
        for (int i = 2; i < this.players.size(); i++) {
            String player_option = this.players.get(i).getOption(current_call);
            if (player_option.startsWith("RAISE")) {
                int bet = Integer.parseInt(player_option.split(" ")[1]);
                this.players.get(i).betMoney(bet);
                addToPot(bet);
                current_call = bet;
            }
            else if (player_option.startsWith("CALL")) {
                this.players.get(i).betMoney(current_call);
                addToPot(current_call);
            }
            else if (player_option.startsWith("FOLD")) {
                //
            }
        }
    }

    public void dealPlayerCards(int number) {
        PlayerRunnable player;
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < players.size(); j++) {
                player = this.players.get(j);
                player.giveCard(this.card_deck.popTopCard());
            }
        }
        for (int i = 0; i < this.players.size(); i++) {
            player = this.players.get(i);
            player.writeCardsToSocket();
        }
        System.out.println(String.format("After deal deck size: %d", this.card_deck.getDeckSize()));
    }

    public void dealMiddleCards(int number) {
        for (int i = 0; i < number; i++) {
            this.card_deck.popTopCard();
            this.middle_deck.addCard(this.card_deck.popTopCard());
        }
        String middle_card_string = this.middle_deck.toString();
        for (int i = 0; i < this.players.size(); i++) {
            this.players.get(i).writeToSocket(middle_card_string);
        }
        System.out.println(String.format("After middle deal deck size: %d", this.card_deck.getDeckSize()));
    }

    public void addToPot(int money) {
        this.pot_cash += money;
        for (int i = 0; i < this.players.size(); i++) {
            this.players.get(i).writeToSocket(String.format("POT: %d", this.pot_cash));
        }
    }
}
