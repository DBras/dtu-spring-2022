package asyncgui;

public class RectangleRunnable implements Runnable {
    Main parent;

    public RectangleRunnable(Main p) {
        this.parent = p;
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
            parent.rectangle_field.drawRandomRectangle();
        }
    }
}
