package blackjack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author johnl
 *
 * Lightweight and convenient representation of a French-suited playing card
 */
public class PlayingCard {

    // Verification arrays, static since they're the same for every instance
    private static final List<String> suits = Arrays.asList("Clubs", "Diamonds", "Hearts", "Spades"); // All card allSuits
    private static final List<String> ranks = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"); // All card ranks
    
    // Fields
    private final String rank;
    private final String suit;

    // Constructer
    public PlayingCard(String r, String s) {
        if (!ranks.contains(r.trim())) throw(new IllegalArgumentException("Invalid card rank for new PlayingCard"));
        if (!suits.contains(s.trim())) throw(new IllegalArgumentException("Invalid card suit for new PlayingCard"));
        this.rank = r;
        this.suit = s;
    }

    // Getters
    public final String getRank() {
        return this.rank;
    }

    public final String getSuit() {
        return this.suit;
    }

    public final static List<String> allRanks() {
        /**
         * Returns a locked view of the list of card ranks
         */
        return Collections.unmodifiableList(ranks);
    }

    public final static List<String> allSuits() {
        /**
         * Returns a locked view of the list of card suits
         */
        return Collections.unmodifiableList(suits);
    }

    @Override
    public final String toString() {
        /**
         * Override of toString() for easy printing
         */
        return this.rank + " of " + this.suit;
    }
}