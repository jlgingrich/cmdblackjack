package blackjack;

import terminal_io.TerminalIO;

/**
 *
 * @author johnl
 */
public class Blackjack {

    public static void main(String[] args) {
        TerminalIO.setBorders("|");
        TerminalIO.screenWidth(96);
        BlackjackGame game = new BlackjackGame();
    }
}