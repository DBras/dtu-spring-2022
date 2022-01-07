package asyncgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    private static final int PRIME_SLEEP_TIME = 100; // Set amount of sleep time in ms for writing primes
    private static final int RECTANGLE_SLEEP_TIME = 100; // Set amount of sleep time in ms for drawing rectangles
    private static final int WINDOW_WIDTH = 1000;
    private static final int RECTANGLE_FIELD_HEIGHT = 500;
    private static final int PRIME_TEXT_AREA_HEIGHT = 800; // Note: changes the amount of primes displayed
    private static final int SCROLL_PANE_HEIGHT = 300;
    private static final int QUIT_BUTTON_HEIGHT = 50; // Change window dimensions

    JTextArea prime_area;
    JButton quit_button;
    RectanglePanel rectangle_field; // Frames and elements for the GUI
    JScrollPane scrollpane;

    /**
     * Constructor for creating GUI-window. Takes no parameters.
     */
    public Main() {
        getContentPane().setLayout(new BorderLayout()); // Set borderlayout for main frame

        quit_button = new JButton("Quit");
        quit_button.setPreferredSize(new Dimension(WINDOW_WIDTH, QUIT_BUTTON_HEIGHT));
        quit_button.addActionListener(this); // Exit button

        prime_area = new JTextArea();
        prime_area.setAlignmentX(Component.CENTER_ALIGNMENT);
        prime_area.setPreferredSize(new Dimension(WINDOW_WIDTH, PRIME_TEXT_AREA_HEIGHT));
        prime_area.setLineWrap(true); // Text area for writing primes

        rectangle_field = new RectanglePanel(WINDOW_WIDTH, RECTANGLE_FIELD_HEIGHT);
        rectangle_field.setPreferredSize(new Dimension(WINDOW_WIDTH, RECTANGLE_FIELD_HEIGHT));
        rectangle_field.setLayout(new BorderLayout()); // Extension of JPanel for painting rectangles

        scrollpane = new JScrollPane(prime_area);
        scrollpane.setPreferredSize(new Dimension(WINDOW_WIDTH, 1000));
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
     * @param args Args if run by command line. Not used.
     */
    public static void main(String[] args) { // Static main method for running
        int total_window_height = RECTANGLE_FIELD_HEIGHT + SCROLL_PANE_HEIGHT + QUIT_BUTTON_HEIGHT;
        Main gui = new Main(); // Initialise main window
        gui.setTitle("JavaGUI");
        gui.setSize(WINDOW_WIDTH,total_window_height);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true); // Set parameters for window and show

        PrimeRunnable prime_runnable = new PrimeRunnable(gui, PRIME_SLEEP_TIME);
        Thread t = new Thread(prime_runnable);
        t.start(); // Start thread for writing prime numbers to window

        RectangleRunnable rectangle_runnable = new RectangleRunnable(gui, RECTANGLE_SLEEP_TIME);
        Thread rectangle_thread = new Thread(rectangle_runnable);
        rectangle_thread.start(); // Start thread for painting triangles
    }
}