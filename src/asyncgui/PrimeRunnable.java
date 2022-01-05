package asyncgui;

public class PrimeRunnable implements Runnable {
    Main parent;

    public PrimeRunnable(Main p) {
        parent = p;
    }

    public void run() {

        int number = 2;
        boolean is_prime;
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //
            }
            is_prime = true;
            if (number%2 == 0) {
                number++;
                continue;
            }
            for (int i = 3; i <= number / 2; i+= 2) {
                if (number%i == 0) {
                    is_prime = false;
                    break;
                }
            }
            if (is_prime) {
                parent.prime_area.append(Integer.toString(number) + " ");
            }
            number++;
        }

    }

}
