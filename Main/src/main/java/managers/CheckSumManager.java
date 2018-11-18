package managers;

public class CheckSumManager {

    // Attributes.
    public static String generator = "1000100000010001";
    private static String padding = new String(new char[generator.length()]).replace("\0", "0");

    /**
     * @param stream Stream of bits representing the payload.
     * @return Stream of bits representing the checksum.
     */
    public static String computeCheckSum(String stream) {
        return computeReminder(stream + padding);
    }

    /**
     * Computes the checksum for the <type>, <metadata> and <data>.
     * @param type Type of the Frame.
     * @param metadata Metadata of the Frame.
     * @param data Data of the Frame.
     * @return Stream of bits representing the check sum
     */
     public static String computeCheckSum(String type, String metadata, String data) {
         StringBuilder sb = new StringBuilder();
         sb.append(type);
         sb.append(metadata);
         sb.append(data);
         sb.append(padding);
        return computeReminder(sb.toString());
    }

    /**
     * Checks if the Frame's content is valid.
     * @param frameContent Stream of bits representing the Frame's content.
     */
    public static boolean isFrameContentValid(String frameContent) {
        return computeReminder(frameContent).equals(padding);
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
