package asyncgui;

public class RectangleRunnable implements Runnable {
    // Field for parent window so the runnable can interact with the GUI
    Main parent;
    int sleep_time;

    /**
     * Constructor called when preparing to run thread
     * @param p Parent GUI to be drawn on
     */
    public RectangleRunnable(Main p, int sleep_time) {
        this.parent = p; // Sets the parent-field to the passed parameter of type Main
        this.sleep_time = sleep_time;
    }

    /**
     * Run-method from Runnable is called when thread is started. Draws random rectangles on the Panel
     * until stopped
     */
    @Override
    public void run() {
        while(true) { // Run until interrupted
            try {
                Thread.sleep(this.sleep_time); // Start by sleeping
            } catch (InterruptedException e) {}
            parent.rectangle_field.drawRandomRectangle(); // Draw a random rectangle
        }
    }
}
