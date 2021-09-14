package blackjack;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author johnl
 */
public class BlackjackGame {

        private final int N = 2; // The number of decks [1-8] to be shuffled together by the dealer
        private final int INITIAL_MONEY = 100; // The currentMoney the player has at the start of the game
        private final List<String> VALID_COMMANDS = Arrays.asList("continue", "retire"); // All valid commands for after each round

        private boolean gameOver = false;
        private String gameOverReason;
        private int currentRound = 0;
        private int currentMoney = 0;

        public BlackjackGame() { // When called, starts the game
            
            currentMoney = INITIAL_MONEY;
            TerminalBuffer.addLine("BLACKJACK - An Object-Oriented command-line game written in Java by Liam Gingrich");
            TerminalBuffer.renderConfirm();
            do {
                BlackjackRound lastRound = new BlackjackRound(N, currentMoney);
                currentMoney += lastRound.winnings; // Updates the running money count with the results of the last round
                TerminalBuffer.renderConfirm();
                currentRound++;
                if (currentMoney == 0) { // CATCH ALL GAME OVER CONDITIONS
                    gameOver = true;
                    gameOverReason = "lost";
                } else {
                    askToContinue();
                }
            } while (!gameOver);
            switch (gameOverReason) {
                case "lost":
                    TerminalBuffer.addLine("Game Over: you lost all of your after round " + currentRound + " and left broke.");
                    break;
                case "retired":
                    TerminalBuffer.addLine("Game Over: you retired after round " + currentRound + " and left with $" + currentMoney + "!");
                    break;
                default: throw(new AssertionError("Game should only end once out of money or retired; check implementation."));
            }
            TerminalBuffer.renderConfirm();
        }
        
        private void askToContinue() {
            /** 
             * Asks the user if they'd like to continue or end the game
             */
            String command;
            TerminalBuffer.addLine("What would you like to do?: " + VALID_COMMANDS);
            do {
                TerminalBuffer.renderBuffer();
                command = TerminalBuffer.KEYBOARD.nextLine().trim();
            } while (!VALID_COMMANDS.contains(command));
            if (command.equals("retire")) {
                gameOver = true;
                gameOverReason = "retired";
            }
            TerminalBuffer.empty();
        }
    }
