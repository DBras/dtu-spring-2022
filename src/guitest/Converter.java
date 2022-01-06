package guitest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Converter extends JFrame implements ActionListener{

    public final int btn_x = 100, btn_y = 30;

    public JButton b1, b2, b3;
    public JLabel l1, l2, l3;
    public JTextField in1, in2, in3; // Define fields to be shown

    /**
     * Class constructor. Creates the main gui
     */
    public Converter() {
        getContentPane().setLayout(new BorderLayout()); // Set the layout to BorderLayout
        Dimension btnsize = new Dimension(btn_x, btn_y); // Dimension for the size of the buttons

        b1 = new JButton("Convert");
        b2 = new JButton("Convert");
        b3 = new JButton("Convert");
        JButton[] buttons = {b1, b2, b3}; // Creates the different buttons

        l1 = new JLabel("Decimal");
        l2 = new JLabel("Binary");
        l3 = new JLabel("Hexadecimal");
        JLabel[] labels = {l1, l2, l3}; // Creates text labels

        in1 = new JTextField();
        in2 = new JTextField();
        in3 = new JTextField();
        JTextField[] inputs = {in1, in2, in3}; // Creates input fields

        for (int i = 0; i < 3; i++) { // Apply same options to all buttons, fields and labels
            labels[i].setMaximumSize(btnsize);
            labels[i].setAlignmentX(Component.CENTER_ALIGNMENT); // Center labels and use button-size

            inputs[i].setMinimumSize(new Dimension(400, 30));
            inputs[i].setMaximumSize(new Dimension(400, 30));
            inputs[i].setAlignmentX(Component.CENTER_ALIGNMENT); // Center input fields and define dimensions

            buttons[i].setMaximumSize(btnsize);
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].addActionListener(this); // Center buttons and add action listener
        }

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
        p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
        p3.setLayout(new BoxLayout(p3, BoxLayout.PAGE_AXIS)); // Create 3 panels and set their layouts to box

        for (int i = 0; i < 3; i++) { // Add all buttons, fields and labels to the panels
            p2.add(Box.createRigidArea(new Dimension(100, 5)));
            p2.add(labels[i]);

            p3.add(Box.createRigidArea(new Dimension(100, 5)));
            p3.add(inputs[i]);

            p1.add(Box.createRigidArea(new Dimension(100, 5)));
            p1.add(buttons[i]);
        }

        getContentPane().add(p1, BorderLayout.EAST);
        getContentPane().add(p2, BorderLayout.WEST);
        getContentPane().add(p3, BorderLayout.CENTER); // Add panels to main GUI
    }

    /**
     * Method called whenever user interacts with GUI
     * @param e ActionEvent passed automatically when button is pressed
     */
    public void actionPerformed(ActionEvent e) {
        // Check which button is pressed to see what to convert from
        if (e.getSource() == b1) { // Decimal button
            try {
                int number = Integer.parseInt(in1.getText()); // Get text / number from input field
                in2.setText(Integer.toBinaryString(number)); // Convert to binary and set correct field
                in3.setText(Integer.toHexString(number));  // Convert to hex and set correct field
            } catch (NumberFormatException error) {
                // Show alert box if error occurred in conversion / incorrect input was detected
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        }
        else if (e.getSource() == b2) { // Binary button
            // Same logic as with previous if-statement is used
            try {
                int number = Integer.parseInt(in2.getText(), 2); // Read as binary number
                in1.setText(Integer.toString(number));
                in3.setText(Integer.toHexString(number));
            } catch (NumberFormatException error) { // Catch invalid input
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        }
        else if (e.getSource() == b3) { // Hex button
            try {
                int number = Integer.parseInt(in3.getText(), 16); // Read as hex number
                in1.setText(Integer.toString(number));
                in2.setText(Integer.toBinaryString(number));
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        }

    }

    /**
     * Static main method which creates GUI and makes it visible
     * @param args Args if run by GUI. Not used
     */
    public static void main(String[] args) {
        Converter gui = new Converter(); // Initialise main GUI

        gui.setTitle("JavaGUI");
        gui.setSize(600,150);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true); // Set options and show the converter GUI
    }
}
