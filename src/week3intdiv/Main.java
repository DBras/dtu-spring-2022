package week3intdiv;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        String d = "11000111010";
        String g = "1001";
        int data_length = d.length(), gen_length = g.length();

        BigInteger D = new BigInteger(d,2);
        BigInteger G = new BigInteger(g, 2);

        BigInteger D_placeholder = new BigInteger(d, 2);
        BigInteger G_placeholder = null;

        for (int i = 0; i < data_length; i++) {
            int shift = data_length - gen_length - i;
            G_placeholder = G.shiftLeft(shift);
            System.out.println(D_placeholder.toString(2));
            System.out.println(G_placeholder.toString(2));
        }
    }
}
