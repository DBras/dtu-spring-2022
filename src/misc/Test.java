package misc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Test extends JFrame implements ActionListener{

    public final int btn_x = 100, btn_y = 30;

    public JButton b1, b2, b3;
    public JLabel l1, l2, l3;
    public JTextField in1, in2, in3;

    public Test() {
        getContentPane().setLayout(new BorderLayout());
        Dimension btnsize = new Dimension(btn_x, btn_y);

        b1 = new JButton("Button 1");
        b2 = new JButton("Button 2");
        b3 = new JButton("Button 3");
        JButton[] buttons = {b1, b2, b3};

        l1 = new JLabel("Decimal");
        l2 = new JLabel("Binary");
        l3 = new JLabel("Hexadecimal");
        JLabel[] labels = {l1, l2, l3};

        in1 = new JTextField();
        in2 = new JTextField();
        in3 = new JTextField();
        JTextField[] inputs = {in1, in2, in3};

        for (int i = 0; i < 3; i++) {
            labels[i].setMaximumSize(btnsize);
            labels[i].setAlignmentX(Component.CENTER_ALIGNMENT);

            inputs[i].setMinimumSize(new Dimension(400, 30));
            inputs[i].setMaximumSize(new Dimension(400, 30));
            inputs[i].setAlignmentX(Component.CENTER_ALIGNMENT);

            buttons[i].setMaximumSize(btnsize);
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].addActionListener(this);
        }

        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
        p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
        p3.setLayout(new BoxLayout(p3, BoxLayout.PAGE_AXIS));

        for (int i = 0; i < 3; i++) {
            p2.add(Box.createRigidArea(new Dimension(100, 5)));
            p2.add(labels[i]);

            p3.add(Box.createRigidArea(new Dimension(100, 5)));
            p3.add(inputs[i]);

            p1.add(Box.createRigidArea(new Dimension(100, 5)));
            p1.add(buttons[i]);
        }

        getContentPane().add(p1, BorderLayout.EAST);
        getContentPane().add(p2, BorderLayout.WEST);
        getContentPane().add(p3, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            try {
                int number = Integer.parseInt(in1.getText());
                in2.setText(Integer.toBinaryString(number));
                in3.setText(Integer.toHexString(number));
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        }
        else if (e.getSource() == b2) {
            try {
                int number = Integer.parseInt(in2.getText(), 2);
                in1.setText(Integer.toString(number));
                in3.setText(Integer.toHexString(number));
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        }
        else if (e.getSource() == b3) {
            try {
                int number = Integer.parseInt(in3.getText(), 16);
                in1.setText(Integer.toString(number));
                in2.setText(Integer.toBinaryString(number));
            } catch (NumberFormatException error) {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
        }

    }

    public static void main(String[] args) {
        Test gui = new Test();

        gui.setTitle("JavaGUI");
        gui.setSize(600,150);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }
}
