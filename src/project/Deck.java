package project;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck_of_cards;

    public Deck(ArrayList<Card> cards) {
        this.deck_of_cards = cards;
    }

    public Deck() {
        this.deck_of_cards = new ArrayList<Card>();
    }

    public void initSortedDeck() {
        String[] suits = {"Diamonds", "Clubs", "Hearts", "Spades"};
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < 13; j++) {
                deck_of_cards.add(new Card(suits[i], j+1));
            }
        }
    }

    public void addCard(Card c) {
        this.deck_of_cards.add(c);
    }

    public Card popTopCard() {
        return this.deck_of_cards.remove(0);
    }

    public int getDeckSize() {
        return this.deck_of_cards.size();
    }

    public void shuffle() {
        Collections.shuffle(this.deck_of_cards);
    }

    public String toString() {
        String out_string = "";
        for (int i = 0; i < this.deck_of_cards.size(); i++) {
            out_string += this.deck_of_cards.get(i) + " ";
        }
        return out_string;
    }
}
