package asyncgui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RectangleFrame extends JPanel {
    // Initialise fields to be used. Colors are stored in a list to be accessed randomly
    private BufferedImage img;
    private Random r = new Random();
    private int h, w;
    private Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY, Color.GREEN,
                                Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,
                                Color.YELLOW} ;

    /**
     * Constructor for the Panel. Initialises a border and an image to draw on
     * @param w Width of the frame
     * @param h Height of the frame
     */
    public RectangleFrame(int w, int h) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.w = w;
        this.h = h;
        img = createImage();
    }

    /**
     * Run when Panel is drawn. Simply draws the image
     * @param g Graphics passed when drawing the image
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Necessary for drawing component
        g.drawImage(img, 0, 0, this); // Draws image
    }

    /**
     * Method for constructing the image to be drawn on. Called by class constructor
     * @return Returns a BufferedImage which serves as canvas to be drawn on
     */
    private BufferedImage createImage() {
        img = new BufferedImage(this.w, this.h, BufferedImage.TYPE_INT_ARGB); // Constructs image with set height and width
        Graphics2D g2 = img.createGraphics();
        g2.setBackground(Color.WHITE); // Set background to white
        g2.clearRect(0, 0, img.getWidth(), img.getHeight());
        g2.dispose();
        return img; // Return the image
    }

    /**
     * Method for drawing a random rectangle. Draws it within the height and width of the window with a random color
     */
    public void drawRandomRectangle() {
        Graphics g = img.getGraphics();
        int randomX = r.nextInt(this.h);
        int randomY = r.nextInt(this.w); // Random starting positions within limits of the canvas
        int randomHeight = r.nextInt(this.h - randomX);
        int randomWidth = r.nextInt(this.w - randomY); // Random height and width within limits of remaining canvas
        g.setColor(colors[r.nextInt(colors.length)]); // Use a random color
        g.fillRect(randomX, randomY, randomWidth, randomHeight);
        g.dispose();
        repaint(); // Repaint the window to show the changes
    }
}
