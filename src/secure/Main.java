package secure;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Tictactoegui gamegui = new Tictactoegui();
        gamegui.setTitle("JavaGUI");
        gamegui.setSize(450, 450);
        gamegui.setResizable(false);
        gamegui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamegui.setVisible(true); // Initialise the game board. Only does this once, as it is reused if
        // player chooses to replay game

        gamegui.runGame(); // Run the main game loop
    }
}
