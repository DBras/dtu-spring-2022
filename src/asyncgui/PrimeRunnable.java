package asyncgui;

public class PrimeRunnable implements Runnable {
    Main parent; // Field for parent GUI

    /**
     * Constructor for runnable. Run by the Main.main-method.
     * @param p Takes the main GUI as argument so it can interact.
     */
    public PrimeRunnable(Main p) {
        parent = p; // Sets the parent-field to the passed parameter
    }

    /**
     * run-method from Runnable is run when the thread is started. Calculates prime numbers and writes them to
     * the parent.prime_area text area.
     */
    public void run() {
        parent.prime_area.append(2 + " "); // Start by writing 2 to parent.prime_area
        int number = 3;
        boolean is_prime; // Start at 3 and initialise is_prime boolean
        while(true) { // Run until interrupted
            try {
                Thread.sleep(100); // Start by sleeping
            } catch (InterruptedException e) {}

            is_prime = true; // Assume number is prime
            for (int i = 3; i <= number / 2; i+= 2) { // Try to divide by all odd numbers up to half of number
                if (number%i == 0) {
                    is_prime = false; // Break the loop if the number is not prime
                    break;
                }
            }
            if (is_prime) { // If the number is prime, add it to the text_area
                parent.prime_area.append(Integer.toString(number) + " ");
            }
            number+=2; // Check next odd number
        }
    }
}
