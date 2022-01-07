package guitest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Tictactoegui extends JFrame implements ActionListener{

    private static final String SERVER_URL = "ftnk-ctek01.win.dtu.dk";
    private static final int SERVER_PORT = 1060;

    static PrintWriter pw;
    public static JButton[] buttons = new JButton[9]; // Define variables to be used by other methods

    public Tictactoegui() { // Renders the game board
        createButtonPage();
    }

    /**
     * Method for getting the next line received by the server
     * @param bir BufferedReader from which to receive server messages
     * @return String: next line received from server
     */
    public static String getNextLine(BufferedReader bir) { // Method for getting next line from server
        String line = "";
        while (true) { // Try until readLine() returns a value
            try {
                line = bir.readLine();
                break;
                } catch (Exception e) { // Catch exception if the server hasn't sent a full line.
                // Simply runs the loop again
                continue;
                }
            }
        return line; // Return the full line received from server
        }

    /**
     * Render all buttons with correct text specified by parameter.
     * @param values String of format used by server (eg. "..x.x.o.o")
     */
    public static void renderButtons(String values) { // Method for rendering the board with x, o or .
        for (int i = 0; i < 9; i++) {
            buttons[i].setText(Character.toString(values.charAt(i)));
        }
    }

    /**
     * Creates the buttons in a grid layout and fill their text with dots, which specify empty fields
     */
    public void createButtonPage() {
        getContentPane().setLayout(new BorderLayout()); // Set layout to BorderLayout

        for (int i = 0; i < 9; i++) { // Initialise 9 buttons with text .
            JButton button = new JButton(".");
            button.setFont(new Font("Serif",Font.BOLD,30)); // Change font for readability
            buttons[i] = button; // Add to array
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].addActionListener(this); // Centers all buttons and adds action listeners
        }

        JPanel buttons_panel = new JPanel();
        buttons_panel.setLayout(new GridLayout(3, 3)); // Define new panel with 3x3 grid

        for (int i = 0; i < 9; i++) {
            buttons_panel.add(buttons[i]); // Adds buttons to grid
        }
        getContentPane().add(buttons_panel, BorderLayout.CENTER);
    }

    /**
     * Method for handling user interaction, i.e. a button is pressed
     * @param e ActionEvent passed to method when button is pressed
     */
    public void actionPerformed(ActionEvent e) {
        int button_pressed = 0;

        for (int i = 0; i < 9; i++) { // Loop though buttons to see which one is pressed
            if (e.getSource() == buttons[i]) {
                button_pressed = i+1; // Add one since server expects 1-indexed numbers
            }
        }
        pw.print(Integer.toString(button_pressed) + "\r\n");
        pw.flush(); // Write the button press to server
    }

    /**
     * Method for showing the "Repeat Game" text box. Method is run when a game is completed
     */
    public static void showRepeat() {
        int output = JOptionPane.showConfirmDialog(null
        , "Want to run game again?"
        , "Repeat"
        , JOptionPane.YES_NO_CANCEL_OPTION
        , JOptionPane.INFORMATION_MESSAGE); // Show yes or no options with text and title

        if (output == JOptionPane.YES_OPTION) { // Renders the board with empties and restarts connection to server
            renderButtons(".........");
            runGame();
        } else if (output == JOptionPane.NO_OPTION) {
            System.exit(0);
        } else if (output == JOptionPane.CANCEL_OPTION) {
            // Empty, conserve window
        }
        else {
            throw new RuntimeException("Wrong option returned from yes/no dialog"); // Unknown option is given
        }
    }

    /**
     * Main method for running a game. Is run by main() after board is initialised
     */
    public static void runGame() {
        String server_URL = SERVER_URL; // Define server address
        int server_port = SERVER_PORT;
        Socket sock = null;

        try {
            sock = new Socket(server_URL, server_port);
            BufferedReader bir = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            pw = new PrintWriter(sock.getOutputStream()); // Define writers and streams from connection
            String nextLine = "";
            boolean game_running = true; // Runs the following loop as long as the game is defined as running

            while(game_running) { // Run until game is over
                while(!nextLine.equals("YOUR TURN")) { // Run until the user is prompted
                    nextLine = getNextLine(bir); // Get line written by server
                    if (nextLine.substring(0,8).equals("BOARD IS")) { // In this case, render the board
                        renderButtons(nextLine.substring(9,18));
                    }
                    else if (nextLine.equals("ILLEGAL MOVE")) { // Show alert because of illegal move
                        JOptionPane.showMessageDialog(null, nextLine, "Alert", JOptionPane.WARNING_MESSAGE);
                    }
                    else if (nextLine.endsWith("WINS")) { // Game should end
                        game_running = false;
                        JOptionPane.showMessageDialog(null, nextLine); // Show who won
                        showRepeat(); // Ask player to repeat game
                        break;
                    }
                }
                nextLine = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Static method main for running the game. Creates the game window and starts the game
     * @param args Args supplied if run by command line. Not used
     */
    public static void main(String[] args) {
        Tictactoegui gamegui = new Tictactoegui();
        gamegui.setTitle("JavaGUI");
        gamegui.setSize(450, 450);
        gamegui.setResizable(false);
        gamegui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamegui.setVisible(true); // Initialise the game board. Only does this once, as it is reused if
                                    // player chooses to replay game

        runGame(); // Run the main game loop
    }
}
