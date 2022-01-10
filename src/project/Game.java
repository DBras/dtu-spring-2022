package project;

import java.net.Socket;
import java.util.ArrayList;

public class Game implements Runnable{
    private ArrayList<Socket> sockets;
    private ArrayList<ClientRunnable> players;
    private Deck card_deck;
    private Deck middle_deck = new Deck();

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
            ClientRunnable new_client = new ClientRunnable(sock, this.sockets.size());
            Thread client_thread = new Thread(new_client);
            client_thread.start(); // Start new thread when client connects
            players.add(new_client);
            System.out.println(sock + " was started");
        }
        dealPlayerCards(2);
        dealMiddleCards(3);
    }

    public void dealPlayerCards(int number) {
        ClientRunnable player;
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < players.size(); j++) {
                player = players.get(i);
                player.giveCard(this.card_deck.popTopCard());
            }
        }
        for (int i = 0; i < this.players.size(); i++) {
            player = players.get(i);
            player.writeHandToSocket();
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
            players.get(i).writeToSocket(middle_card_string);
        }
        System.out.println(String.format("After middle deal deck size: %d", this.card_deck.getDeckSize()));
    }
}
