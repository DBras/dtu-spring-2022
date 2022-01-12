package project;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck_of_cards;

    /**
     * Initialise deck from ArrayList of Card objects
     * @param cards ArrayList of cards
     */
    public Deck(ArrayList<Card> cards) {
        this.deck_of_cards = cards;
    }

    /**
     * Initialise empty deck
     */
    public Deck() {
        this.deck_of_cards = new ArrayList<Card>();
    }

    /**
     * Initialise a sorted deck. Goes diamonds 1 to 13, clubs 1 to 13, hearts 1 to 13 and spades 1 to 13
     */
    public void initSortedDeck() {
        String[] suits = {"Diamonds", "Clubs", "Hearts", "Spades"};
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < 13; j++) {
                deck_of_cards.add(new Card(suits[i], j+1));
            }
        }
    }

    /**
     * Add card to deck
     * @param c Card object to add to deck
     */
    public void addCard(Card c) {
        this.deck_of_cards.add(c);
    }

    /**
     * Pops the top card of the deck and returns it
     * @return Card object
     */
    public Card popTopCard() {
        return this.deck_of_cards.remove(0);
    }

    /**
     * Get the size of the deck
     * @return Integer representing size of deck
     */
    public int getDeckSize() {
        return this.deck_of_cards.size();
    }

    /**
     * Shuffle the deck with Collections.shuffle method
     */
    public void shuffle() {
        Collections.shuffle(this.deck_of_cards);
    }

    /**
     * Returns the deck in String format. Utilises the Card.toString-method
     * @return String representing deck
     */
    public String toString() {
        String out_string = "";
        for (int i = 0; i < this.deck_of_cards.size(); i++) {
            out_string += this.deck_of_cards.get(i) + " ";
        }
        return out_string;
    }
}
