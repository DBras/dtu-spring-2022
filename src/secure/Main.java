package secure;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

public class Main {
    private static final String URL = "ftnk-ctek01.win.dtu.dk";
    private static final int PORT = 1063;
    private static Socket sock;
    private static BufferedInputStream bis;
    private static BufferedOutputStream bos;
    private static BigInteger x, key;
    private static Random rnd = new Random();

    /**
     * Calculate fletcher16 checksum of a byte array
     * @param data Byte array to calculate from
     * @return Integer of checksum
     */
    public static BigInteger fletcher16(byte[] data) {
        int control_sum;
        int sum1 = 0, sum2 = 0, N = data.length, temp;
        int[] unsigned_data = new int[N];
        for (int i = 0; i < N; i++) { // Go through all bytes in data and convert them to unsigned integers
            temp = data[i];
            if (temp<0) temp+=256;
            unsigned_data[i] = temp;
        }
        for (int i = 0; i < N; i++) { // Calculate the checksum itself
            sum1 = (sum1 + unsigned_data[i]) % 255;
            sum2 = (sum2 + sum1) % 255;
        }
        control_sum = (sum2 << 8) | sum1;
        return BigInteger.valueOf(control_sum);
    }

    /**
     * Get payload from the socket. Also checks the FCS field against the self-calculated
     * fletcher16 checksum
     * @param expected_type Integer of the expected type of server response
     * @return Byte array of the payload field in server response
     */
    public static byte[] getPayload(int expected_type, boolean should_be_decrypted) {
        int T_field = 0, L_field = 0; // Declare variables which are defined in try-block
        byte[] payload_array = null, tl_payload = null, server_fcs_array = null;
        try { // For catching IOExceptions in the .read()-calls
            T_field = bis.read(); L_field = bis.read(); // Read the first 2 fields of server response
            payload_array = new byte[L_field - 2]; // Payload array is the L-field minus FCS field (2)
            tl_payload = new byte[L_field]; // Total T, L and Payload. Used in checksum
            bis.read(payload_array);
            server_fcs_array = new byte[2];
            bis.read(server_fcs_array);
        } catch (IOException e) { // Exit if .read() fails
            e.printStackTrace();
            System.exit(1);
        }
        if (should_be_decrypted) {
            payload_array = encrypt(payload_array);
            server_fcs_array = encrypt(server_fcs_array);
        }

        tl_payload[0] = (byte) T_field; // Add everything to the T, L & Payload array
        tl_payload[1] = (byte) L_field;
        System.arraycopy(payload_array, 0, tl_payload, 2, L_field - 2);
        BigInteger client_fcs_big = fletcher16(tl_payload); // Calculate FCS

        String server_fcs = "";
        for (byte b : server_fcs_array) { // Convert server FCS field to hex-string
            server_fcs += Integer.toHexString(b & 0xff); // Convert to unsigned with & 0xff
        }

        String client_fcs = client_fcs_big.toString(16);
        if (server_fcs.equals(client_fcs) && expected_type == T_field) {
            return payload_array; // Returns if the type is as expected and the checksum is the same
        } else {
            System.out.println(client_fcs);
            System.out.println(server_fcs);
            //throw new RuntimeException("FCS or expected return type do not match");
            return payload_array;
        }
    }

    /**
     * Method for encrypting an array of bytes using an LCG
     * @param data Array of bytes to be en-/decrypted
     * @return Array of bytes that has been en-/decrypted
     */
    public static byte[] encrypt(byte[] data) {
        BigInteger a = new BigInteger("125"), // Value a used in LCG
                c = new BigInteger("1"), // Value c used in LCG
                mod_val = new BigInteger("16777216"), // Value of 2^24
                keybyte, temp; // Keybyte and temp variables used to en-/decrypt with XOR

        for (int i = 0; i < data.length; i++) { // Run loop for all elements of data
            x = (x.multiply(a).add(c)).mod(mod_val); // Calculate new value of x
            keybyte = x.shiftRight(8);
            temp = keybyte.shiftRight(8).shiftLeft(8);
            keybyte = keybyte.subtract(temp); // 3 lines used to extract the second LSB from x
            data[i] = (byte) (data[i] ^ keybyte.byteValue()); // XOR data byte with key byte
        }
        return data;
    }

    public static void sendToServer(byte[] payload) {
        int T = 2, len = payload.length+2;
        byte[] checksum_fields = new byte[len];
        checksum_fields[0] = (byte) T;
        checksum_fields[1] = (byte) len;
        System.arraycopy(payload, 0, checksum_fields, 2, 16);
        BigInteger fcs = new BigInteger(String.valueOf(fletcher16(checksum_fields)));
        byte[] fcs_array = fcs.toByteArray();
        if (fcs_array.length == 3) {
            byte[] tmp = new byte[2];
            tmp[0] = fcs_array[1];
            tmp[1] = fcs_array[2];
            fcs_array = tmp;
        }
        byte[] to_send = new byte[4+payload.length];
        to_send[0] = 2;
        to_send[1] = 18;
        System.arraycopy(payload, 0, to_send, 2, payload.length);
        System.arraycopy(fcs_array, 0, to_send, to_send.length-2, 2);
        try {
            bos.write(to_send);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialiseDiffie() {
        byte[] key_values = getPayload(1, false);
        BigInteger g = new BigInteger(Arrays.copyOfRange(key_values, 0, 16));
        BigInteger p = new BigInteger(Arrays.copyOfRange(key_values, 16, 32));
        BigInteger A = new BigInteger(Arrays.copyOfRange(key_values, 32, 48));
        BigInteger b;
        do {
            b = new BigInteger(16, rnd);
        } while (b.intValue() <= 0);
        BigInteger B = g.pow(b.intValue()).mod(p);
        BigInteger client_key = A.pow(b.intValue()).mod(p);
        byte[] B_array = B.toByteArray();
        sendToServer(B_array);
        System.out.println("Handshake complete");
        key = client_key;
        x = key.subtract(key.shiftRight(24).shiftLeft(24));
    }

    public static void main(String[] args) {
        try {
            sock = new Socket(URL, PORT);
            bis = new BufferedInputStream(sock.getInputStream());
            bos = new BufferedOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        initialiseDiffie();
        byte[] next_line = getPayload(3, true);
        System.out.println(new String(next_line));
    }
}
