package project;

public class HandEvaluator {
    public HandEvaluator() {}

    public int evaluateSeven(Deck cards) {
        //return evaluateFive(cards.)
        return 0;
    }

    public int evaluateFive(Deck cards) {
        int score = 0;
        for (int i = 0; i < 5; i++) {
            score += cards.getCard(i).getCardValue();
        }

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
        return score;
    }

    public boolean containsRoyalFlush(Deck cards) {
        if (cards.getCard(4).getCardValue()==1
                && cards.getCard(3).getCardValue()==13
                && cards.getCard(2).getCardValue()==12
                && cards.getCard(1).getCardValue()==11
                && cards.getCard(0).getCardValue()==10
                && allCardsMatch(cards)) {
            return true;
        }
        return false;
    }

    public boolean containsStraightFlush(Deck cards) {
        boolean contains = true;
        for (int i = 1; i < 5; i++) {
            if (cards.getCard(i-1).getCardValue() != cards.getCard(i).getCardValue()-1
                || !cards.getCard(i-1).getSuit().equals(cards.getCard(i).getSuit())) {
                contains = false;
                break;
            }
        }
        return contains;
    }

    public boolean containsFourOfKind(Deck cards) {
        return containsSameCards(cards, 4);
    }

    public boolean containsFullHouse(Deck cards) {
        if (cards.getCard(0).getCardValue() == cards.getCard(1).getCardValue()
            && cards.getCard(1).getCardValue() == cards.getCard(2).getCardValue()) {
            return cards.getCard(3).getCardValue() == cards.getCard(4).getCardValue();
        }

        if (cards.getCard(0).getCardValue() == cards.getCard(1).getCardValue()) {
            return cards.getCard(2).getCardValue() == cards.getCard(3).getCardValue()
                    && cards.getCard(3).getCardValue() == cards.getCard(4).getCardValue();
        }

        return false;
    }

    public boolean containsFlush(Deck cards) {
        return allCardsMatch(cards);
    }

    public boolean containsStraight(Deck cards) {
        int card_diff = 0;
        for (int i = 0; i < 4; i++) {
            card_diff = cards.getCard(i+1).getCardValue() - cards.getCard(i).getCardValue();
            if (card_diff != 1) {
                return false;
            }
        }
        return true;
    }

    public boolean containsThreeOfKind(Deck cards) {
        return containsSameCards(cards, 3);
    }

    public boolean containsTwoPairs(Deck cards) {
        int pair_counter = 0;
        for (int i = 0; i < 4; i++) {
            if (cards.getCard(i).getCardValue() == cards.getCard(i+1).getCardValue()) {
                pair_counter++;
                i++;
            }
        }
        if (pair_counter == 2) {
            return true;
        }
        return false;
    }

    public boolean containsOnePair(Deck cards) {
        return containsSameCards(cards, 2);
    }

    public boolean containsSameCards(Deck cards, int number) {
        for (int i = 0; i < 6-number; i++) {
            boolean contains = true;
            for (int j = 0; j < number-1; j++) {
                if (cards.getCard(j+i).getCardValue() != cards.getCard(j+i+1).getCardValue()) {
                    contains = false;
                }
            }
            if (contains) {return true;}
        }
        return false;
    }

    public boolean allCardsMatch(Deck cards) {
        boolean return_value = false;
        for (int i = 0; i < 4; i++) {
            return_value = cards.getCard(i).getSuit() == cards.getCard(i+1).getSuit();
        }
        return return_value;
    }
}
