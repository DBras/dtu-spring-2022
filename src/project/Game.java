package project;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable{
    private ArrayList<Socket> sockets;
    private ArrayList<PlayerRunnable> players;
    private Deck card_deck;
    private Deck middle_deck = new Deck();
    private int pot_cash = 0;
    private int current_call = 0;
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

        ArrayList<PlayerRunnable> current_round_players = new ArrayList<>(this.players);
        current_round_players.get(0).betMoney(MINIMUMBET / 2);
        current_round_players.get(1).betMoney(MINIMUMBET);
        addToPot(MINIMUMBET / 2 + MINIMUMBET);
        this.current_call = MINIMUMBET;

        dealPlayerCards(2);
        dealMiddleCards(3);
        runRound(current_round_players, 2);
        if (current_round_players.size() > 1) {
            dealMiddleCards(1);
            runRound(current_round_players, 0);
        } else {
            current_round_players.get(0).addMoney(this.pot_cash);
            this.pot_cash = 0;
        }
        if (current_round_players.size() > 1) {
            dealMiddleCards(1);
            runRound(current_round_players, 0);
        } else {
            current_round_players.get(0).addMoney(this.pot_cash);
            this.pot_cash = 0;
        }
        if (current_round_players.size() > 1) {
            broadcastMessage("TALLYING CARDS");
        } else {
            current_round_players.get(0).addMoney(this.pot_cash);
            this.pot_cash = 0;
        }
    }

    public void broadcastMessage(String message) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).writeToSocket(message);
        }
    }

    public void dealPlayerCards(int number) {
        for (int i = 0; i < number; i++) {
            for (int j = 0; j < players.size(); j++) {
                this.players.get(j).giveCard(this.card_deck.popTopCard());
            }
        }
        for (int i = 0; i < this.players.size(); i++) {
            this.players.get(i).writeHandToSocket();
        }
        System.out.println(String.format("After deal deck size: %d", this.card_deck.getDeckSize()));
    }

    public void dealMiddleCards(int number) {
        for (int i = 0; i < number; i++) {
            this.card_deck.popTopCard();
            this.middle_deck.addCard(this.card_deck.popTopCard());
        }
        for (int i = 0; i < this.players.size(); i++) {
            this.players.get(i).writeCardsToSocket(this.middle_deck, "MIDDLE: ");
        }
        System.out.println(String.format("After middle deal deck size: %d", this.card_deck.getDeckSize()));
    }

    public void addToPot(int money) {
        this.pot_cash += money;
        broadcastMessage(String.format("POT: %d", this.pot_cash));
    }

    public void runRound(ArrayList<PlayerRunnable> current_round_players, int from_index) {
        for (int i = from_index; i < current_round_players.size(); i++) {
            if (current_round_players.size() > 1) {
                String player_option = current_round_players.get(i).getOption(this.current_call);
                if (player_option.startsWith("RAISE")) {
                    int bet = Integer.parseInt(player_option.split(" ")[1]);
                    current_round_players.get(i).betMoney(bet);
                    addToPot(bet);
                    this.current_call = bet;
                } else if (player_option.startsWith("CALL")) {
                    current_round_players.get(i).betMoney(this.current_call);
                    addToPot(this.current_call);
                } else if (player_option.startsWith("FOLD")) {
                    current_round_players.remove(i);
                    i--;
                }
            }
        }
    }
}
