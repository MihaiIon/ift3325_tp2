package managers;

import java.nio.charset.StandardCharsets;

public class ConversionManager {

    /**
     * @param data The data that will be sent to the receiver.
     * @return Stream of bits.
     */
    public static String convertDataToBitsStream(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.US_ASCII);
        return convertBytesToString(bytes);
    }

    /**
     * @param stream Stream of bits.
     * @return Provides the data that was originally sent.
     */
    public static String convertBitsStreamToData(String stream) {
        String data = "";
        return data;
    }

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

    /**
     * @param stream Stream of 8 bits.
     * @return Provides a byte representing the stream of bits.
     */
    public static byte convertStringToByte(String stream) {
        if(stream.length() == 8) return Byte.parseByte(stream, 2);
        return 0;
    }

    /**
     * @param stream Stream of bits (multiple of 8).
     * @return Provides an array of bytes representing the stream of bits.
     */
    public static byte[] convertStringToBtyes(String stream) {
        if(stream.length() % 8 == 0) {
            int length = stream.length() / 8;
            byte[] bytes = new byte[length];
            for (int i = 0; i < length; i++) {
                bytes[i] = convertStringToByte(stream.substring(i * 8, (i+1) * 8));
            }
            return bytes;
        }
        return null;
    }

    /**
     * @param stream Stream of bits.
     * @return Provides a string that groups each 8 bits together.
     */
    public static String convertStreamToReadableStream(String stream) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stream.length(); i++){
            sb.append(stream.charAt(i));
            if(i != 0 & i%8 == 7) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
