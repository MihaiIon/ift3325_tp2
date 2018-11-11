package managers;

public class ConversionManager {

    /**
     * @param b Byte to be converted to String.
     * @return Provides a String of bits representing the byte.
     */
    public static String convertByteToString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    /**
     * @param bs Array of bytes.
     * @return Provides a String of bits representing each byte.
     */
    public static String convertBytesToString(byte[] bs) {
        String str = "";
        for (int i = 0; i < bs.length; i++){
            str += convertByteToString(bs[i]);
        }
        return str;
    }
}
