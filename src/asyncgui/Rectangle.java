package asyncgui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Rectangle extends JPanel {
    private BufferedImage img;
    private Random r = new Random();
    private int h, w;
    private Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
                                Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
                                Color.YELLOW} ;

    public Rectangle(int w, int h) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.w = w;
        this.h = h;
        img = createImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
    }

    private BufferedImage createImage() {
        img = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setBackground(Color.WHITE);
        g2.clearRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return img;
    }

    public void specificPaint() {
        Graphics g = img.getGraphics();
        g.setColor(colors[r.nextInt(colors.length)]);
        g.fillRect(50, 50, 50, 50);
        g.dispose();
        repaint();
    }
}
