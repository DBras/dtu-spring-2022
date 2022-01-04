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

    static PrintWriter pw;
    public static JButton[] buttons = new JButton[9]; // Define variables to be used by other methods

    public Tictactoegui() { // Renders the game board
        createButtonPage();
    }

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

    public static void renderButtons(String values) { // Method for rendering the board with x, o or .
        for (int i = 0; i < 9; i++) {
            buttons[i].setText(Character.toString(values.charAt(i)));
        }
    }

    public void createButtonPage() { // Creates all the buttons and fills them with empties / .
        getContentPane().setLayout(new BorderLayout());

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton(".");
            buttons[i] = button;
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

    public void actionPerformed(ActionEvent e) { // Method for handling all button presses
        int button_pressed = 0;

        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i]) {
                button_pressed = i+1;
            }
        }
        pw.print(Integer.toString(button_pressed) + "\r\n"); // Writes the number of the pressed button to the server
        pw.flush();
    }

    public static void showRepeat() { // Method for repeating the game
        int output = JOptionPane.showConfirmDialog(null
        , "Want to run game again?"
        , "Repeat"
        , JOptionPane.YES_NO_OPTION
        , JOptionPane.INFORMATION_MESSAGE);

        if (output == JOptionPane.YES_OPTION) { // Renders the board with empties and restarts connection to server
            renderButtons(".........");
            runGame();
        } else if (output == JOptionPane.NO_OPTION) {
            // Empty, only used for error handling
        }
        else {
            throw new RuntimeException("Wrong option returned from yes/no dialog"); // Something must be wrong...
        }
    }

    public static void runGame() {
        String server_URL = "ftnk-ctek01.win.dtu.dk"; // Define server address
        int server_port = 1060;
        Socket sock = null;

        try {
            sock = new Socket(server_URL, server_port);
            BufferedReader bir = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            pw = new PrintWriter(sock.getOutputStream()); // Define writers and streams from connection
            String nextLine = "";
            boolean game_running = true; // Runs the following loop as long as the game is defined as running

            while(game_running) {
                while(!nextLine.equals("YOUR TURN")) {
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

    public static void main(String[] args) {
        Tictactoegui gamegui = new Tictactoegui();
        gamegui.setTitle("JavaGUI");
        gamegui.setSize(150, 150);
        gamegui.setResizable(false);
        gamegui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamegui.setVisible(true); // Initialise the game board. Only does this once, as it is reused if
                                    // player chooses to replay game

        runGame(); // Run the main game loop
    }
}
