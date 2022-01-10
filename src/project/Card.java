package project;

import java.util.Arrays;

public class Card implements Comparable<Card>{
    private String suit;
    private int card_value;
    private final String[] ACCEPTED_SUITS = {"Diamonds", "Clubs", "Hearts", "Spades"};

    public Card(String suit, int card_value) {
        if (is_valid(suit, card_value)) {
            this.suit = suit;
            this.card_value = card_value;
        } else {
            throw new IllegalArgumentException("Card parameters not valid");
        }
    }

    private boolean is_valid(String suit, int card_value) {
        return Arrays.asList(ACCEPTED_SUITS).contains(suit) && card_value > 0 && card_value <=13;
    }

    public int compareTo(Card c) {
        if (this.card_value > c.card_value) {
            return 1;
        } else if (this.card_value < c.card_value) {
            return -1;
        } else {
            return 0;
        }
    }

    public String toString() {
        return String.format("%s%02d", this.suit.charAt(0), this.card_value);
    }
}
