package managers;

public class CheckSumManager {

    // Attributes.
    public static String generator = "1000100000010001";
    private static String padding = new String(new char[generator.length()]).replace("\0", "0");

    /**
     * Adds padding to the stream of bits and provides its remainder.
     * @param stream Stream of bits representing the payload.
     * @return Stream of bits representing the check sum
     */
     static String computeCheckSum(String stream) {
        String paddedStream = stream + padding;
        return computeReminder(paddedStream);
    }

    /**
     * Checks if the payload of the Frame is valid.
     * @param stream Stream of bits representing the payload + computed_checksum.
     */
    public static boolean isCheckSumValid(String stream) {
        return computeReminder(stream).equals(padding);
    }

    /**
     * Provides the reminder from the division : stream / generator.
     * @param stream Stream of bits representing the payload.
     */
    private static String computeReminder(String stream) {
        String end, remainder, padding, paddedRemainder = stream;
        String bytesStream;
        for(int i = 0; i <= paddedRemainder.length() - generator.length(); i++) {
            // Compute division
            if(paddedRemainder.charAt(i) == '1') {
                bytesStream = paddedRemainder.substring(i, i + generator.length());
                end = stream.substring(i + generator.length());
                remainder = divideBytesByGenerator(bytesStream) + end;
                padding = new String(new char[stream.length() - remainder.length()]).replace("\0", "0");
                paddedRemainder = padding + remainder;
            }
        }
        return paddedRemainder.substring(paddedRemainder.length() - generator.length());
    }

    private static String divideBytesByGenerator(String bytesStream) {
        StringBuilder sb = new StringBuilder();
        String nWindow, gWindow;
        int n, g;
        for (int i = 0; i < 2; i++) {
            nWindow = bytesStream.substring(i * 8, (i+1) * 8);
            gWindow = generator.substring(i * 8, (i+1) * 8);
            n = Integer.parseInt(nWindow, 2);
            g = Integer.parseInt(gWindow, 2);
            sb.append(ConversionManager.convertByteToString((byte)(0xff & (n ^ g))));
        }
        return sb.toString();
    }
}
