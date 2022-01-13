package project;

import java.util.Arrays;

public class Card implements Comparable<Card>{
    private String suit;
    private int card_value;
    private final String[] ACCEPTED_SUITS = {"Diamonds", "Clubs", "Hearts", "Spades"};

    /**
     * Constructor method. Initialises new card if parameters are valid
     * @param suit Suit of card
     * @param card_value Integer value from 1 to 13
     */
    public Card(String suit, int card_value) {
        if (is_valid(suit, card_value)) {
            this.suit = suit;
            this.card_value = card_value;
        } else {
            throw new IllegalArgumentException("Card parameters not valid");
        }
    }

    /**
     * Method for checking validity of card parameters. Checks if suit is in the allowed list and value is 1 to 13
     * @param suit String representing card suit
     * @param card_value Integer representing card value
     * @return Boolean representing validity (true is valid)
     */
    private boolean is_valid(String suit, int card_value) {
        return Arrays.asList(ACCEPTED_SUITS).contains(suit) && card_value > 0 && card_value <=13;
    }

    /**
     * Compare to other card. Judges based on numbers only
     * @param c Other card to compare to
     * @return Integer representing lower, equal or higher (-1, 0 or 1)
     */
    public int compareTo(Card c) {
        if (this.card_value == 1 || c.card_value == 1) {
            return c.card_value - this.card_value;
        } else if (this.card_value > c.card_value) {
            return 1;
        } else if (this.card_value < c.card_value) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Getter for card value
     * @return Integer representing card value
     */
    public int getCardValue() {
        return this.card_value;
    }

    /**
     * Getter for suit field
     * @return String representing card suit
     */
    public String getSuit() {
        return this.suit;
    }

    /**
     * Returns the card in readable string (eg. queen of diamonds = D12)
     * @return Card string
     */
    public String toString() {
        return String.format("%s%02d", this.suit.charAt(0), this.card_value);
    }
}
