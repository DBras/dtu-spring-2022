package secure;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;

public class Main {
    private static final String URL = "ftnk-ctek01.win.dtu.dk";
    private static final int PORT = 1061;
    private static Socket sock;
    private static BufferedInputStream bis;

    /**
     * Calculate fletcher16 checksum of a byte array
     * @param data Byte array to calculate from
     * @return Integer of checksum
     */
    public static int fletcher16(byte[] data) {
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
        return control_sum;
    }

    /**
     * Get payload from the socket. Also checks the FCS field against the self-calculated
     * fletcher16 checksum
     * @param expected_type Integer of the expected type of server response
     * @return Byte array of the payload field in server response
     */
    public static byte[] getPayload(int expected_type) {
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
        tl_payload[0] = (byte) T_field; // Add everything to the T, L & Payload array
        tl_payload[1] = (byte) L_field;
        System.arraycopy(payload_array, 0, tl_payload, 2, L_field - 2);

        String server_fcs = "";
        for (byte b : server_fcs_array) { // Convert server FCS field to hex-string
            server_fcs += Integer.toHexString(b & 0xff); // Convert to unsigned with & 0xff
        }
        String client_fcs = Integer.toHexString(fletcher16(tl_payload)); // Calculate FCS
        if (server_fcs.equals(client_fcs) && expected_type == T_field) {
            return payload_array; // Returns if the type is as expected and the checksum is the same
        } else {
            throw new RuntimeException("FCS or expected return type do not match");
        }
    }

    public static void main(String[] args) {
        byte[] B = {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE};
        System.out.println(Integer.toHexString(fletcher16(B))); // Skal printe værdien 714b

        try {
            sock = new Socket(URL, PORT);
            bis = new BufferedInputStream(sock.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] response = getPayload(3);
        BigInteger first = new BigInteger(Arrays.copyOfRange(response, 0, 16));
        BigInteger second = new BigInteger(Arrays.copyOfRange(response, 16, response.length));
        System.out.println(first);
        System.out.println(second);
    }
}
