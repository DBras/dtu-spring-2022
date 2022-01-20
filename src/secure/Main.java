package secure;

public class Main {
    public static int fletcher16(byte[] data) {
        int control_sum;
        int sum1 = 0, sum2 = 0, N = data.length, temp;
        int[] unsigned_data = new int[N];
        for (int i = 0; i < N; i++) {
            temp = data[i];
            if (temp<0) temp+=256;
            unsigned_data[i] = temp;
        }
        for (int i = 0; i < N; i++) {
            sum1 = (sum1 + unsigned_data[i]) % 255;
            sum2 = (sum2 + sum1) % 255;
        }
        if (sum1 < 0) sum1 += 256;
        if (sum2 < 0) sum2 += 256;
        control_sum = (sum2 << 8) | sum1;
        return control_sum;
    }

    public static void main(String[] args) {
        byte[] B = {(byte)0x12, (byte)0x34, (byte)0x56, (byte)0x78, (byte)0x9A, (byte)0xBC, (byte)0xDE};
        System.out.println(Integer.toHexString(fletcher16(B))); // Skal printe værdien 714b
    }
}
