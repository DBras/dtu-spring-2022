package asyncgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    JTextArea prime_area;
    JButton quit_button;
    RectangleFrame rectangle_field;

    public Main() {
        getContentPane().setLayout(new BorderLayout());

        quit_button = new JButton("Quit");
        quit_button.addActionListener(this);

        prime_area = new JTextArea();
        prime_area.setAlignmentX(Component.CENTER_ALIGNMENT);
        prime_area.setLineWrap(true);

        rectangle_field = new RectangleFrame(1000, 500);
        rectangle_field.setPreferredSize(new Dimension(1000, 500));
        rectangle_field.setLayout(new BorderLayout());

        JScrollPane scrollpane = new JScrollPane(prime_area);
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(rectangle_field, BorderLayout.NORTH);
        getContentPane().add(scrollpane);
        getContentPane().add(quit_button, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quit_button) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        Main gui = new Main();
        gui.setTitle("JavaGUI");
        gui.setSize(1000,800);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);

        PrimeRunnable primerunnable = new PrimeRunnable(gui);
        Thread t = new Thread(primerunnable);
        t.start();

        RectangleRunnable rectangle_runnable = new RectangleRunnable(gui);
        Thread rectangle_thread = new Thread(rectangle_runnable);
        rectangle_thread.start();
    }
}
