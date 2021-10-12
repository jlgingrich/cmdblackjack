package blackjack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import terminal_io.TerminalIO;
import java.util.function.Function;

/**
 *
 * @author johnl
 */
public class BlackjackRound {
    private static final List<String> VALID_MOVES = Arrays.asList("hit", "stand", "double", "surrender"); // Will add "split" once basic game is functional
    private static final List<Integer> RANK_VALUES = Arrays.asList(2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, -1); // Contains the values of each card rank
    
    private List<String> availiableMoves = new ArrayList<>(VALID_MOVES);
    private List<PlayingCard> deck = new ArrayList<>(); // The deck dealt from during the round
    private List<PlayingCard> playerHand = new ArrayList<>(); // The player's playerHand
    private List<PlayingCard> dealerHand = new ArrayList<>(); // The dealer's playerHand
    private int N;
    private int currentMoney;
    private int currentBet;
    private boolean roundOver = false;
    private String roundOverReason;
    private int playerHandValue = 0;
    private int dealerHandValue = 0;
    public int winnings = 0;
    
    public BlackjackRound(int nDecks, int roundStartMoney) {
        if (nDecks < 1) throw(new IllegalArgumentException ("Number of decks cannot be 0"));
        if (roundStartMoney < 1) throw(new IllegalArgumentException ("Round starting money cannot be 0"));
        this.N = nDecks;
        this.currentMoney = roundStartMoney;
        getShuffledDecks(); // Shuffles a new set of decks for the round to use
        
        // Ask the player how much they'd like to bet
        askForBet();
        
        // Deal two cards to each player and get their starting hand values
        dealerHand.add(drawTopCard());
        dealerHand.add(drawTopCard());
        playerHand.add(drawTopCard());
        playerHand.add(drawTopCard());
        playerHandValue = getHandValue(playerHand);
        dealerHandValue = getHandValue(dealerHand);
        
        if (playerHandValue == 21) { // If the player was dealt a Blackjack, deal with it here
            roundOver = true;
            if (dealerHandValue == 21) { // If the dealer also got a Blackjack, its a push
                roundOverReason = "push"; 
            } else {
                roundOverReason = "blackjack";
            }
        }
        while (!roundOver) {
            TerminalIO.appendLine("Your hand: " + playerHand);
            TerminalIO.appendLine("Dealer's hand: [" + dealerHand.get(0) + ", ? of ?]");
            TerminalIO.appendLine("");
            askForMove();
            playerHandValue = getHandValue(playerHand);
            if (playerHandValue > 21) { // CATCH ALL GAME OVER CONDITIONS
                roundOver = true;
                roundOverReason = "bust";
            }
        }
        if (roundOverReason.equals("stand")) { // Finalize the round after a player stands
            while (dealerHandValue < 17) {
                dealerHand.add(drawTopCard());
                dealerHandValue = getHandValue(dealerHand);
            }
            if (dealerHandValue > 21) {
                roundOverReason = "dbust"; // This means the player stood and the dealer bust, which is a win
            } else {
                if (dealerHandValue > playerHandValue) {
                    roundOverReason = "loss"; // This means the player stood and the dealer beat them, which is a loss
                } else if (dealerHandValue == playerHandValue) {
                    roundOverReason = "push"; // This means the player stood and the dealer exactly tied them, which is a push
                } else {
                    roundOverReason = "win"; // This means the player stood and they beat the dealer, which is a win
                }
            }
        }      
        TerminalIO.appendLine("Your hand: " + playerHand);
        TerminalIO.appendLine("Dealer's hand: " + dealerHand);
        TerminalIO.appendLine("");
        switch (roundOverReason) {
            
            case "win": // If you win, you gain your bet
                TerminalIO.appendLine("You beat the dealer! You won $" + currentBet + "!");
                this.winnings = currentBet;
                break;
            case "dbust": // If the dealer busts, you gain your bet
                TerminalIO.appendLine("The dealer bust! You won $" + currentBet + "!");
                this.winnings = currentBet;
                break;
            case "blackjack": // If you get an uncontested blackjack, you gain your bet matched 3:2
                TerminalIO.appendLine("You got a Blackjack! You won $" + (int) (currentBet * 1.5) + "!");
                this.winnings = (int) (currentBet * 1.5);
                break;
            case "push": // If you push, you keep your bet
                TerminalIO.appendLine("You pushed. You didn't win anything, but you didn't lose your bet either.");
                this.winnings = 0;
                break;
            case "bust": // If you bust, you lose your bet
                TerminalIO.appendLine("You bust. You lost $" + currentBet + ".");
                this.winnings = -currentBet;
                break;
            case "loss": // If you lose, you lose your bet
                TerminalIO.appendLine("You got beaten by the dealer. You lost $" + currentBet + ".");
                this.winnings = -currentBet;
                break;
            case "surrender": // If you surrender, you lose half your bet
                TerminalIO.appendLine("You surrendered. You lost $" + (int) (currentBet/2.0) + ".");
                this.winnings = -(int) (currentBet/2.0);
                break;
            default: throw(new AssertionError("There should always be a reason that the round is over if its over; check implementation."));
        }
        // Round ends here
    }

    private void getShuffledDecks() {
        /**
         * Populates the game's deck with the cards from N randomly shuffled decks
         */
        for (int i = 0; i < N; i++) {
            PlayingCard.allSuits().forEach(suit -> {
                PlayingCard.allRanks().forEach(rank -> {
                    deck.add(new PlayingCard(rank, suit));
                });
            });
        }
        Collections.shuffle(deck);
    }
    
    private PlayingCard drawTopCard() {
        /**
         * Efficiently pops the last card off of the deck and returns it
         */
        if(deck.isEmpty()) throw(new AssertionError("Deck should never run out of cards during a round; check implementation."));
        PlayingCard draw = deck.get(deck.size() - 1); // Gets the last card in the list to minimize cycles
        deck.remove(deck.size() - 1); // Because this is the last index, no other indices are affected
        return draw;
    }
    
    private void askForMove() {
        /** 
         * Asks the user what move they'd like to make
         */
        if (currentBet > currentMoney) { // Ensures that the player can ony double if they can afford it
            availiableMoves.remove("double");
        }
        if (playerHand.size() > 2) { // Ensures that the player can ony double or surrender if they haven't played any other moves yet
            availiableMoves.remove("double");
            availiableMoves.remove("surrender");
        }
        
        String move = TerminalIO.getResponse("Which move would you like to take?: " + availiableMoves, availiableMoves);
        switch (move) {
            case "hit": // Adds another card and continues the round
                playerHand.add(drawTopCard());
                TerminalIO.appendLine("You drew a " + playerHand.get(playerHand.size() - 1));
                break;
            case "stand": // Ends the round after the dealer draws
                roundOver = true;
                roundOverReason = "stand";
                break;
            case "double": // Doubles the bet, adds a card, and stands
                playerHand.add(drawTopCard());
                currentBet *= 2;
                roundOver = true;
                roundOverReason = "stand";
                break;
            case "surrender": // Ends the round automatically and forfeits half of the bet
                roundOver = true;
                roundOverReason = "surrender";
                break;
            default: throw(new AssertionError("Failed to correctly verify move as legal; check implementation."));
        }
        TerminalIO.clear();
    }
    
    private static int getHandValue(List<PlayingCard> hand) {
        /**
         * Returns the value of the specified list of PlayingCards
         */
        int value = 0;
        value = hand.stream().filter(c -> !"Ace".equals(c.getRank())).map(card -> RANK_VALUES.get(PlayingCard.allRanks().indexOf(card.getRank()))).reduce(value, Integer::sum); // Sums the non-ace cards
        for (PlayingCard card: hand.stream().filter(c -> "Ace".equals(c.getRank())).collect(Collectors.toList())) { // Sums Aces with "aim for no more than 21" rule
            if (value + 11 > 21) {
                value += 1;
            } else {
                value += 11;
            }
        }
        return value;
    }

    private void askForBet() {
        /** 
         * Asks the user how much they'd like to bet for the coming round
         */
        Function<String, Boolean> validIntStr = s -> {
            try {
                int i = Integer.parseInt(s);
                return (0 < i && i <= currentMoney);
            } catch (NumberFormatException e) {
                return false;
            }
        };
        int betAmount = Integer.parseInt(TerminalIO.getResponse("Out of $" + currentMoney + ", how much would you like to bet?", validIntStr));
        currentBet = betAmount;
        currentMoney -= betAmount;
        TerminalIO.clear();
    }
}
