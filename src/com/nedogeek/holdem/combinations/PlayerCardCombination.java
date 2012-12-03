package com.nedogeek.holdem.combinations;

import com.nedogeek.holdem.gamingStuff.Card;
import com.nedogeek.holdem.gamingStuff.CardValue;

import java.util.Arrays;

/**
 * User: Konstantin Demishev
 * Date: 27.11.12
 * Time: 14:23
 */
public class PlayerCardCombination implements Comparable<PlayerCardCombination> {
    private Card[] cards;

    public PlayerCardCombination(Card... cards) {
        this.cards = cards;
        downSort(cards);
    }

    private void downSort(Card[] cards) {
        for (int n = 0; n < cards.length; n++) {
            for (int i = 0; i < cards.length - n - 1; i++) {
                if (cards[i].compareTo(cards[i + 1]) < 0) {
                    Card tempCard = cards[i];
                    cards[i] = cards[i + 1];
                    cards[i + 1] = tempCard;
                }
            }
        }
    }

    @Override
    public int compareTo(PlayerCardCombination o) {
        return 0;
    }

    public String getCombination() {
        switch (cards.length) {
            case 2:
                return twoCardsCombination();
            case 5:
                return fiveCardsCombination();
            default:
                return null;
        }
    }

    private String fiveCardsCombination() {
        if (hasRoyalFlash()) {
            return Combinations.ROYAL_FLASH.generateMessage();
        }

        if (hasStraightFlash()) {
            return Combinations.STRAIGHT_FLASH.generateMessage(cards[0]);
        }

        if (hasFourOfKind()) {
            return Combinations.FOUR_OF_KIND.generateMessage(cards[2]);
        }

        if (hasStraight()) {
            return Combinations.STRAIGHT.generateMessage(cards[0]);
        }

        if (hasFullHouse()) {
            return (sameCardValues(1, 2)) ? Combinations.FULL_HOUSE.generateMessage(cards[0], cards[4]) :
                    Combinations.FULL_HOUSE.generateMessage(cards[4], cards[0]);
        }

        if (hasSet()) {
            return Combinations.SET.generateMessage(getSetCards());
        }

        if (hasFlash()) {
            return Combinations.FLASH.generateMessage(cards);
        }

        return Combinations.HIGH_CARD.generateMessage(cards);
    }

    private Card[] getSetCards() {
        Card[] setCards = new Card[3];
        setCards[0] = cards[2];

        if (sameCardValues(0, 1, 2)) {
            setCards[1] = cards[3];
            setCards[2] = cards[4];
        }

        if (sameCardValues(1, 2, 3)) {
            setCards[1] = cards[0];
            setCards[2] = cards[4];
        }

        if (sameCardValues(2, 3, 4)) {
            setCards[1] = cards[0];
            setCards[2] = cards[1];
        }

        return setCards;
    }

    private boolean hasSet() {
        return sameCardValues(0, 1, 2) || sameCardValues(1, 2, 3) || sameCardValues(2, 3, 4);
    }

    private boolean hasFourOfKind() {
        return sameCardValues(0, 1, 2, 3) || sameCardValues(1, 2, 3, 4);
    }

    private boolean sameCardValues(int... cardNumbers) {
        CardValue valueNeeded = cards[cardNumbers[0]].getCardValue();
        for (int cardNumber : cardNumbers) {
            if (cards[cardNumber].getCardValue() != valueNeeded) {
                return false;
            }
        }
        return true;
    }

    private boolean hasFullHouse() {
        return sameCardValues(0, 1) && sameCardValues(3, 4) &&
                (sameCardValues(1, 2) || sameCardValues(2, 3));
    }

    private boolean hasRoyalFlash() {
        return hasStraightFlash() && cards[0].getCardValue().equals(CardValue.ACE);
    }

    private boolean hasStraightFlash() {
        return hasFlash() && hasStraight();
    }

    private boolean hasStraight() {
        for (int i = 0; i < cards.length - 1; i++) {
            if (!cards[i].isNear(cards[i + 1])) {
                return false;
            }
        }
        return true;
    }

    private boolean hasFlash() {
        boolean hasFlash = true;
        for (Card card : cards) {
            if (!card.sameSuit(cards[0])) {
                hasFlash = false;
            }
        }
        return hasFlash;
    }

    private String twoCardsCombination() {
        if (cards[0].compareTo(cards[1]) == 0) {
            return Combinations.PAIR_TWO_CARDS.generateMessage(cards[0]);
        }

        return Combinations.HIGH_CARD_TWO_CARDS.generateMessage(cards);
    }

    @Override
    public String toString() {
        return getCombination() + ": " + Arrays.asList(cards);
    }
}
