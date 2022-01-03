package guitest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Tictactoegui extends JFrame implements ActionListener{

    public JButton[] buttons = new JButton[9];

    public Tictactoegui() {
        createButtonPage();
    }

    public void createButtonPage() {
        getContentPane().setLayout(new BorderLayout());
        Dimension btnsize = new Dimension(50, 50);

        for (int i = 0; i < 9; i++) {
            JButton button = new JButton(".");
            buttons[i] = button;
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].addActionListener(this);
        }

        JPanel buttons_panel = new JPanel();
        buttons_panel.setLayout(new GridLayout(3, 3));

        for (int i = 0; i < 9; i++) {
            buttons_panel.add(buttons[i]);
        }

        getContentPane().add(buttons_panel, BorderLayout.CENTER);

    }

    public void actionPerformed(ActionEvent e) {
        int button_pressed = 0;

        for (int i = 0; i < 9; i++) {
            if (e.getSource() == buttons[i]) {
                button_pressed = i+1;
            }
        }
        System.out.println(button_pressed);
    }

    public static void main(String[] args) {
        Tictactoegui gamegui = new Tictactoegui();
        gamegui.setTitle("JavaGUI");
        gamegui.setSize(150, 150);
        gamegui.setResizable(false);
        gamegui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamegui.setVisible(true);

    }
}
