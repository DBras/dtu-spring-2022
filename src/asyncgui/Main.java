package asyncgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    JTextArea prime_area;
    JButton quit_button;
    RectangleFrame rectangle_field; // Frames and elements for the GUI

    /**
     * Constructor for creating GUI-window. Takes no parameters.
     */
    public Main() {
        getContentPane().setLayout(new BorderLayout()); // Set borderlayout for main frame

        quit_button = new JButton("Quit");
        quit_button.addActionListener(this); // Exit button

        prime_area = new JTextArea();
        prime_area.setAlignmentX(Component.CENTER_ALIGNMENT);
        prime_area.setLineWrap(true); // Text area for writing primes

        rectangle_field = new RectangleFrame(1000, 500);
        rectangle_field.setPreferredSize(new Dimension(1000, 500));
        rectangle_field.setLayout(new BorderLayout()); // Extension of JPanel for painting rectangles

        JScrollPane scrollpane = new JScrollPane(prime_area);
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Add scroll bar to prime area

        getContentPane().add(rectangle_field, BorderLayout.NORTH);
        getContentPane().add(scrollpane);
        getContentPane().add(quit_button, BorderLayout.SOUTH); // Add the elements on the main frame
    }

    /**
     * actionPerformed is run when user interacts with window.
     * @param e ActionEvent is passed automatically
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quit_button) {
            System.exit(0); // Exit code with status 0 if "quit"-button is pressed
        }
    }

    /**
     * Static main method to be run.
     */
    public static void main(String[] args) { // Static main method for running
        Main gui = new Main(); // Initialise main window
        gui.setTitle("JavaGUI");
        gui.setSize(1000,800);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true); // Set parameters for window and show

        PrimeRunnable primerunnable = new PrimeRunnable(gui);
        Thread t = new Thread(primerunnable);
        t.start(); // Start thread for writing prime numbers to window

        RectangleRunnable rectangle_runnable = new RectangleRunnable(gui);
        Thread rectangle_thread = new Thread(rectangle_runnable);
        rectangle_thread.start(); // Start thread for painting triangles
    }
}
