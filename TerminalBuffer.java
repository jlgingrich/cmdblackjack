package blackjack;

import java.io.IOException;
import java.util.Scanner;

public final class TerminalBuffer {

    public static final Scanner KEYBOARD = new Scanner(System.in); // Scanner for keyboard inputs
    private static final StringBuffer contents = new StringBuffer(); // Used to buffer the content displayed in the terminal so it can be refreshed

    private TerminalBuffer() {
    } // prevents instances

    static <T> void addLine(T obj) {
        /**
         * Appends the given object to the next line on the buffer
         */
        contents.append(obj.toString()).append("\n");
    }
    
    static <T> void addText(T obj) {
        /**
         * Appends the given object to the current line on the buffer
         */
        contents.append(obj.toString());
    }

    static void addPromptLine(String msg) {
        /**
         * Appends the given message as a prompt to the next line on the buffer
         */
        addLine("<" + msg + ">");
    }

    static void renderBuffer() {
        /**
         * Clears the terminal, them dumps the entire terminal buffer into the
         * terminal DOES NOT EMPTY THE BUFFER
         */
        renderBlank();
        System.out.print(contents);
    }

    static void renderConfirm() {
        /**
         * Appends a prompt to press 'Enter' to the buffer,
         * then displays the buffer until the user presses 'Enter',
         * after which it clears the buffer
         */
        String command;
        addPromptLine("Press 'Enter' to continue");
        do {
            renderBuffer();
            command = KEYBOARD.nextLine();
        } while (!command.isEmpty());
        empty();
    }

    static void renderBlank() {
        /**
         * Clears the terminal
         *
         * From https://www.delftstack.com/howto/java/java-clear-console/
         */
        try {
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system

            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        }
    }

    static void empty() {
        /**
         * Clears the buffer
         */
        contents.delete(0, contents.length());
    }
}
