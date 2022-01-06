package asyncgui;

public class RectangleRunnable implements Runnable {
    // Field for parent window so the runnable can interact with the GUI
    Main parent;

    /**
     * Constructor called when preparing to run thread
     * @param p Parent GUI to be drawn on
     */
    public RectangleRunnable(Main p) {
        this.parent = p; // Sets the parent-field to the passed parameter of type Main
    }

    /**
     * Run-method from Runnable is called when thread is started. Draws random rectangles on the Panel
     * until stopped
     */
    public void run() {
        while(true) { // Run until interrupted
            try {
                Thread.sleep(100); // Start by sleeping
            } catch (InterruptedException e) {}
            parent.rectangle_field.drawRandomRectangle(); // Draw a random rectangle
        }
    }
}
