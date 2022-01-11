package project;

import java.net.Socket;
import java.util.ArrayList;

public class Game implements Runnable{
    private ArrayList<Socket> sockets;
    private ArrayList<PlayerRunnable> players;
    private Deck card_deck;
    private Deck middle_deck = new Deck();
    private int pot_cash = 0;
    private int current_call = 0;
    private boolean can_check;
    private ArrayList<PlayerRunnable> current_round_players;
    private final int MINIMUMBET = 50;

    public Game(ArrayList<Socket> sockets) {
        this.sockets = sockets;
        this.players = new ArrayList<>();
    }

    public void run() {
        System.out.println(this.card_deck);

        Socket sock;
        for (int i = 0; i < this.sockets.size(); i++) {
            sock = this.sockets.get(i);
            PlayerRunnable new_client = new PlayerRunnable(sock, this.sockets.size(), i);
            Thread client_thread = new Thread(new_client);
            client_thread.start(); // Start new thread when client connects
            this.players.add(new_client);
            System.out.println(sock + " was started");
        }

        while (getAlivePlayers().size() > 1) {
            setAllActive();
            this.can_check = false;
            this.card_deck = new Deck();
            this.card_deck.initSortedDeck();
            this.card_deck.shuffle();
            current_round_players = getAlivePlayers();

            broadcastMessage("NEW GAME STARTED");
            resetBoard();
            dealPlayerCards(2);
            current_round_players.get(0).betMoney(MINIMUMBET / 2);
            current_round_players.get(1).betMoney(MINIMUMBET);
            addToPot(MINIMUMBET / 2 + MINIMUMBET);
            this.current_call = MINIMUMBET;
            runRound(2);
            int offset = runPlayer(current_round_players.get(0), 0);
            System.out.println(String.format("Next player index: %d",  1 + offset));
            runPlayer(current_round_players.get(1 + offset), 1 + offset);

            this.current_round_players = getActivePlayers();
            this.can_check = true;
            dealMiddleCards(3);
            runRound(0);


//            dealMiddleCards(3);
//            this.can_call = true;
//            if (current_round_players.size() > 1) {
//                dealMiddleCards(1);
//                runRound(current_round_players, 0);
//                current_round_players = getActivePlayers();
//            } else {
//                playerWins();
//            }
//            if (current_round_players.size() > 1) {
//                dealMiddleCards(1);
//                runRound(current_round_players, 0);
//            } else {
//                playerWins();
//            }
//            if (current_round_players.size() > 1) {
//                broadcastMessage("TALLYING CARDS");
//            } else {
//                playerWins();
//            }
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

    public void resetBoard() {
        ArrayList<PlayerRunnable> alive_players = getAlivePlayers();
        for (int i = 0; i < alive_players.size(); i++) {
            alive_players.get(i).resetHand();
        }
        this.middle_deck = new Deck();
    }

    public void playerWins() {
        current_round_players.get(0).addMoney(this.pot_cash);
        this.pot_cash = 0;
    }

    public void addToPot(int money) {
        this.pot_cash += money;
        broadcastMessage(String.format("POT: %d", this.pot_cash));
    }

    public void setAllActive() {
        ArrayList<PlayerRunnable> alive_players = getAlivePlayers();
        for (int i = 0; i < alive_players.size(); i++) {
            alive_players.get(i).setActiveStatus(true);
        }
    }

    public int runPlayer(PlayerRunnable player, int player_index) {
        String player_option = player.getOption(this.current_call, this.can_check);
        if (player_option.startsWith("RASIE")) {
            int bet = Integer.parseInt(player_option.split(" ")[1]);
            player.betMoney(bet);
            addToPot(bet);
            this.current_call = bet;
            return player_index;
        } else if (player_option.startsWith("CALL")) {
            player.betMoney(this.current_call);
            addToPot(this.current_call);
            return player_index;
        } else if (player_option.startsWith("FOLD")) {
            this.current_round_players.remove(player_index);
            player.setActiveStatus(false);
            return player_index - 1;
        } else if (player_option.startsWith("CHECK")) {
            this.current_round_players.add(this.current_round_players.remove(player_index));
            return player_index - 1;
        } else {
            return player_index;
        }
    }

    public void runRound(int from_index) {
        for (int i = from_index; i < current_round_players.size(); i++) {
            if (current_round_players.size() > 1) {
                i = runPlayer(current_round_players.get(i), i);
            }
        }
    }

    public ArrayList<PlayerRunnable> getAlivePlayers() {
        ArrayList<PlayerRunnable> alive_players = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getMoney() > 0) {
                alive_players.add(players.get(i));
            }
        }
        return alive_players;
    }

    public ArrayList<PlayerRunnable> getActivePlayers() {
        ArrayList<PlayerRunnable> active_players = new ArrayList<>();
        ArrayList<PlayerRunnable> alive_players = getAlivePlayers();
        for (int i = 0; i < alive_players.size(); i++) {
            if (alive_players.get(i).getActiveStatus()) {
                active_players.add(alive_players.get(i));
            }
        }
        return active_players;
    }
}
