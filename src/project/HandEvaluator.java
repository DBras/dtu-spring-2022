package project;

import java.util.ArrayList;
import java.util.List;

public class HandEvaluator {
    public HandEvaluator() {} // Empty constructor

    /**
     * Method for evaluating the best possible combination in a seven-card combination. First calculates all
     * possible 5-hand combinations and then finds the best combination in those
     * @param cards Seven-card deck
     * @return Integer representing best score
     */
    public int evaluateSeven(Deck cards) {
        int max_score = 0;
        List<int[]> combinations = generate(7, 5); // Use generate()-method to create permutations
        for (int[] combination : combinations) {
            // For every combination, initialise new Deck and add 5 cards to it
            Deck five_hand = new Deck();
            for (int i = 0; i < combination.length; i++) {
                five_hand.addCard(cards.getCard(combination[i]));
            }
            five_hand.sort(); // Sort the five cards
            int score = evaluateFive(five_hand);
            max_score = Math.max(score, max_score); // Set max_score to max of either new or old score
        }
        return max_score; // Return the best possible score
    }

    /**
     * Recursive method for generating all possible combinations of indeces
     * For usage of method, see generate()
     * @param combinations List object of integer arrays representing indeces
     * @param data Dummy integer array to write combination
     * @param start Start index
     * @param end End index
     * @param index index
     */
    public void combinationRecurse(List<int[]> combinations, int data[], int start, int end, int index) {
        if (index == data.length) {
            int[] combination = data.clone();
            combinations.add(combination); // Add finished combination
        } else if (start <= end) {
            data[index] = start;
            combinationRecurse(combinations, data, start + 1, end, index + 1);
            combinationRecurse(combinations, data, start + 1, end, index); // Two recursive calls
        }
    }

    /**
     * Method that returns all possible combinations of numbers from 0 to n of length r
     * Eg. n=3 and r=2 returns [0,1], [0,2] and [1,2]
     * @param n Number to generate from
     * @param r Length of combination
     * @return Returns a List of int arrays containing all combinations
     */
    public List<int[]> generate (int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        combinationRecurse(combinations, new int[r], 0, n-1, 0); // Populates the combinations-array
        return combinations;
    }


    /**
     * Evaluates the value of a five-card hand
     * @param cards Deck of five cards
     * @return Integer representing value of hand
     */
    public int evaluateFive(Deck cards) {
        int score = 0;
        for (int i = 0; i < 5; i++) {
            score += cards.getCard(i).getCardValue(); // Score is sum of cards
        }

        // Thousands are added to make sure that a low straight flush is never valued lower than a high four-of-a-kind
        if (containsRoyalFlush(cards)) {
            score += 9000;
            return score;
        } else if (containsStraightFlush(cards)) {
            score += 8000;
            return score;
        } else if (containsFourOfKind(cards)) {
            score += 7000;
            return score;
        } else if (containsFullHouse(cards)) {
            score += 6000;
            return score;
        } else if (containsFlush(cards)) {
            score += 5000;
            return score;
        } else if (containsStraight(cards)) {
            score += 4000;
            return score;
        } else if (containsThreeOfKind(cards)) {
            score += 3000;
            return score;
        } else if (containsTwoPairs(cards)) {
            score += 2000;
            return score;
        } else if (containsOnePair(cards)) {
            score += 1000;
            return score;
        }
        return score; // Returns score, minimum is sum of cards
    }

    public boolean containsRoyalFlush(Deck cards) { // Cards must be 10, jack, queen, king and ace of same suit
        if (cards.getCard(4).getCardValue()==1
                && cards.getCard(3).getCardValue()==13
                && cards.getCard(2).getCardValue()==12
                && cards.getCard(1).getCardValue()==11
                && cards.getCard(0).getCardValue()==10
                && allCardsMatch(cards)) {
            return true;
        }
        return false; // Return true as base case
    }

    public boolean containsStraightFlush(Deck cards) { // Test to see if all cards are a flush
        boolean contains = true; // Base case is true
        for (int i = 1; i < 5; i++) {
            if (cards.getCard(i-1).getCardValue() != cards.getCard(i).getCardValue()-1
                || !cards.getCard(i-1).getSuit().equals(cards.getCard(i).getSuit())) {
                contains = false;
                break; // Break if two cards are not directly following
            }
        }
        return contains;
    }

    public boolean containsFourOfKind(Deck cards) {
        return containsSameCards(cards, 4); // Use containsSameCards to find 4 of the same
    }

    public boolean containsFullHouse(Deck cards) {
        // Two cases: either 3 first and 2 last are same cards or 2 first and 3 last are same cards.
        // Returns false if house is built of same cards (should be caught in the containsFourOfKind-method call,
        // but better safe than sorry)
        if (cards.getCard(0).getCardValue() == cards.getCard(1).getCardValue()
            && cards.getCard(1).getCardValue() == cards.getCard(2).getCardValue()) {
            return cards.getCard(3).getCardValue() == cards.getCard(4).getCardValue();
        }

        if (cards.getCard(0).getCardValue() == cards.getCard(1).getCardValue()) {
            return cards.getCard(2).getCardValue() == cards.getCard(3).getCardValue()
                    && cards.getCard(3).getCardValue() == cards.getCard(4).getCardValue();
        }

        return false; // base case false
    }

    public boolean containsFlush(Deck cards) {
        return allCardsMatch(cards); // A flush is merely 5 of same suit, so return allCardsMatch()
    }

    public boolean containsStraight(Deck cards) {
        int card_diff = 0;
        for (int i = 0; i < 4; i++) {
            card_diff = cards.getCard(i+1).getCardValue() - cards.getCard(i).getCardValue();
            if (card_diff != 1) {
                return false; // If more than 1 between two cards, it is not a straight
            }
        }
        return true; // If all diffs are 1, return true
    }

    public boolean containsThreeOfKind(Deck cards) {
        return containsSameCards(cards, 3); // Use containsSameCards with parameter 3
    }

    public boolean containsTwoPairs(Deck cards) {
        int pair_counter = 0; // Counter for pairs
        for (int i = 0; i < 4; i++) {
            // Checks pairwise and skips 1 forward if pair is found (should not be necessary with
            // 3-of-a-kind method prior, however the code is more robust)
            if (cards.getCard(i).getCardValue() == cards.getCard(i+1).getCardValue()) {
                pair_counter++;
                i++;
            }
        }
        return pair_counter == 2; // If 2 pairs are found, return true
    }

    public boolean containsOnePair(Deck cards) {
        return containsSameCards(cards, 2); // Pair is just 2 of the same cards
    }

    public boolean containsSameCards(Deck cards, int number) { // True if there are "number" amount of equal cards
        for (int i = 0; i < 6-number; i++) {
            boolean contains = true;
            for (int j = 0; j < number-1; j++) {
                if (cards.getCard(j+i).getCardValue() != cards.getCard(j+i+1).getCardValue()) {
                    contains = false;
                }
            }
            if (contains) {return true;} // After each pass-through, check return if same cards are found
        }
        return false;
    }

    public boolean allCardsMatch(Deck cards) { // True if all cards are same suit
        boolean return_value = true;
        for (int i = 0; i < 4; i++) {
            return_value = cards.getCard(i).getSuit().equals(cards.getCard(i+1).getSuit()) && return_value;
        }
        return return_value;
    }
}
