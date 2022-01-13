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
    private int current_checks = 0;
    private ArrayList<PlayerRunnable> current_round_players;
    private final int MINIMUMBET = 50;
    private final int STARTING_CASH = 3000;

    /**
     * Class constructor. Accepts an ArrayList of sockets and initialises players
     * @param sockets ArrayList of sockets
     */
    public Game(ArrayList<Socket> sockets) {
        this.sockets = sockets;
        this.players = new ArrayList<>();
    }

    /**
     * Main method. Run when game is initialised from Main.main()
     */
    public void run() {
        Socket sock;
        for (int i = 0; i < this.sockets.size(); i++) { // Go through all sockets and initialise players
            sock = this.sockets.get(i);
            PlayerRunnable new_client = new PlayerRunnable(sock, this.sockets.size(), i, this.STARTING_CASH);
            Thread client_thread = new Thread(new_client);
            client_thread.start(); // Start each player
            this.players.add(new_client); // Add to total list
            System.out.println(sock + " was started");
        }

        // Following code is the actual game logic. The game runs until all but one player
        // has no left-over cash
        while (getAlivePlayers().size() > 1) {
            setAllActive(); // All players get active status (they compete in this round)
            this.can_check = false; // Cannot check on preflop
            this.card_deck = new Deck();
            this.card_deck.initSortedDeck();
            this.card_deck.shuffle(); // Get new deck each game
            current_round_players = getAlivePlayers(); // Returns players with money greater than min bet
            resetBoard(); // Reset player hands and community cards
            broadcastMessage("NEW GAME STARTED");

            dealPlayerCards(2);
            current_round_players.get(0).betMoney(MINIMUMBET / 2);
            current_round_players.get(1).betMoney(MINIMUMBET);
            addToPot(MINIMUMBET / 2 + MINIMUMBET); // All players get dealt 2 cards and blind bets are done
            this.current_call = MINIMUMBET;
            runRound(2); // run round for the rest of the players
            int offset = runPlayer(current_round_players.get(0), 0);
            runPlayer(current_round_players.get(1 + offset), 1 + offset); // Let the 2 blind betters play round

            // Following paragraph is the flop street
            this.current_round_players = getActivePlayers();
            this.can_check = true;
            dealMiddleCards(3);
            runRound(0);
            this.can_check = true; // Checking is enabled after round is over
            current_round_players = getActivePlayers();

            if (current_round_players.size() > 1) { // Stop if only one remains (hasn't folded)
                dealMiddleCards(1); // Add card to community
                this.current_call = this.MINIMUMBET; // Reset minimum bet amount
                runRound(0);
                this.can_check = true;
                current_round_players = getActivePlayers();
            } else { // Find winner if only one remains
                findWinner();
                continue; // Run next game
            }

            if (current_round_players.size() > 1) { // Same as above
                dealMiddleCards(1);
                this.current_call = this.MINIMUMBET;
                runRound(0);
                this.can_check = true;
            } else {
                findWinner();
                continue;
            }
            findWinner(); // Find winner after all streets
        }
    }

    /**
     * Broadcasts a string to all players
     * @param message String to broadcast
     */
    public void broadcastMessage(String message) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).writeToSocket(message);
        }
    }

    /**
     * Deal a number of cards to all players, then message all players their hands
     * @param number number of cards to deal
     */
    public void dealPlayerCards(int number) {
        for (int i = 0; i < number; i++) { // Deal 2 cards to each player
            for (int j = 0; j < players.size(); j++) {
                this.players.get(j).giveCard(this.card_deck.popTopCard());
            }
        }
        for (int i = 0; i < this.players.size(); i++) { // Write the hand of each player
            this.players.get(i).writeHandToSocket();
        }
        System.out.println(String.format("After deal deck size: %d", this.card_deck.getDeckSize())); // Log deck size
    }

    /**
     * Deal middle cards, then write the middle cards to all players. This method also burns cards
     * to limit the efficacy of card counting
     * @param number Number of cards to deal
     */
    public void dealMiddleCards(int number) {
        for (int i = 0; i < number; i++) { // Deal number of cards while burning every other
            this.card_deck.popTopCard();
            this.middle_deck.addCard(this.card_deck.popTopCard());
        }
        for (int i = 0; i < this.players.size(); i++) { // Broadcast middle card to all players
            this.players.get(i).writeCardsToSocket(this.middle_deck, "MIDDLE: ");
        }
        System.out.println(String.format("After middle deal deck size: %d", this.card_deck.getDeckSize()));
    }

    /**
     * Reset all player hands as well as community cards
     */
    public void resetBoard() {
        ArrayList<PlayerRunnable> alive_players = getAlivePlayers();
        for (int i = 0; i < alive_players.size(); i++) { // Empties the hand of all players
            alive_players.get(i).resetHand();
        }
        this.middle_deck = new Deck(); // Reset middle decK
    }

    /**
     * Method run when a specific player has won the game. Adds the pot to their cash and
     * broadcasts their ID
     * @param winner PlayerRunnable object that represents the winner
     */
    public void playerWins(PlayerRunnable winner) {
        broadcastMessage(String.format("PLAYER %d WON THE %d POT", winner.ID, this.pot_cash));
        winner.addMoney(this.pot_cash);
        this.pot_cash = 0; // Broadcast winner, add money to winner and reset pot cash
    }

    /**
     * Add money to the central pot
     * @param money Integer to add
     */
    public void addToPot(int money) {
        this.pot_cash += money;
        broadcastMessage(String.format("POT: %d", this.pot_cash)); // Add cash to pot
    }

    /**
     * Set all player status to active. Used to keep track of players who have folded a round
     */
    public void setAllActive() {
        ArrayList<PlayerRunnable> alive_players = getAlivePlayers();
        for (int i = 0; i < alive_players.size(); i++) {
            alive_players.get(i).setActiveStatus(true); // Go through all players and set their status to active
        }
    }

    /**
     * Find the winner. If only one active player remains, they must be the winner; otherwise
     * calculate worth of player hand + community combination
     * TODO: Calculate hands
     */
    public void findWinner() {
        PlayerRunnable winner = this.current_round_players.get(0); // First player wins by default
        broadcastMessage("FINDING WINNER");
        if (this.current_round_players.size() == 1) { // If only one remains, simply let them win
            playerWins(winner); // First player must win if there is only one
        } else {
            HandEvaluator evaluator = new HandEvaluator();
            int current_high = 0;
            for (PlayerRunnable player : this.current_round_players) { // For each player still playing
                Deck tempdeck = new Deck(); // New temporary deck for middle cards and player hand
                tempdeck.addAll(this.middle_deck);
                tempdeck.addCard(player.getCard(0));
                tempdeck.addCard(player.getCard(1)); // Done manually since player only has 2 cards

                int score = evaluator.evaluateSeven(tempdeck); // Get max value of player hand + middle
                if (score > current_high) {
                    winner = player; // Winner is set to player with new high
                    current_high = score; // New high is set
                }
            }
            playerWins(winner); // "Winner" wins the round
        }

    }

    /**
     * Method for asking player for input and acting on said input. If that input is to remove the player from the
     * round (folding) or moving their turn to the end (checking), returns a lower index to keep track of players
     * in the stack
     * @param player PlayerRunnable object to ask for input
     * @param player_index Index of player in stack
     * @return Integer to keep track of players
     */
    public int runPlayer(PlayerRunnable player, int player_index) {
        String player_option = player.getOption(this.current_call, this.can_check); // Get option from player
        if (player_option.startsWith("RAISE")) { // If player raises, add bet to pot, broadcast message, and update call
            int bet = Integer.parseInt(player_option.split(" ")[1]);
            player.betMoney(bet);
            addToPot(bet);
            this.current_call = bet;
            this.can_check = false; // Player can no longer check
            return player_index;
        } else if (player_option.startsWith("CALL")) { // If player calls, bet the call value
            player.betMoney(this.current_call);
            addToPot(this.current_call);
            this.can_check = false;
            return player_index;
        } else if (player_option.startsWith("FOLD")) { // If player folds, remove from active players
            this.current_round_players.remove(player_index);
            player.setActiveStatus(false);
            this.can_check = false;
            return player_index - 1; // Since player has been removed, the next index of player is one lower
        } else if (player_option.startsWith("CHECK")) { // If player checks, move them to last position
            this.current_round_players.add(this.current_round_players.remove(player_index));
            this.current_checks++;
            return player_index - 1;
        } else { // If nothing fits, do nothing
            return player_index;
        }
    }

    /**
     * Logic for running a round. Ask each player for option (call, raise, fold, check) and stop if
     * only one player remains
     * @param from_index Index to start from. Used in the pre-flop
     */
    public void runRound(int from_index) {
        int max_checks = current_round_players.size(); // If every player checks, start next round
        this.current_checks = 0;
        for (int i = from_index; i < current_round_players.size(); i++) { // Run round for all players from given index
            if (this.current_checks >= max_checks || this.current_round_players.size() <= 1) {
                break; // Break if no players remain or if all players checked
            }
            if (current_round_players.size() > 1) { // If players remain, broadcast the number of active players and run player
                broadcastMessage(String.format("Players: %d", current_round_players.size()));
                i = runPlayer(current_round_players.get(i), i);
            }
        }
    }

    /**
     * Get all players who can actively play the game (cash balance more than minimum bet)
     * @return ArrayList of players that can play
     */
    public ArrayList<PlayerRunnable> getAlivePlayers() { // Returns all players with enough money to bet minimum
        ArrayList<PlayerRunnable> alive_players = new ArrayList<>(); // Initialise empty list of players
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getMoney() > this.MINIMUMBET) {
                alive_players.add(players.get(i)); // Add player if they have money
            }
        }
        return alive_players; // Returns list of all alive players
    }

    /**
     * Get players that have not folded their hand
     * @return ArrayList of players ready to play
     */
    public ArrayList<PlayerRunnable> getActivePlayers() { // Get list of al players with status set to "active"
        ArrayList<PlayerRunnable> active_players = new ArrayList<>();
        ArrayList<PlayerRunnable> alive_players = getAlivePlayers(); // Only check players who are alive
        for (int i = 0; i < alive_players.size(); i++) {
            if (alive_players.get(i).getActiveStatus()) {
                active_players.add(alive_players.get(i));
            }
        }
        return active_players; // Returns all active players
    }
}
