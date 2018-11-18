package managers;


import models.FrameModel;

public class DataManager {

    // Used for the bits stuffing.
    public static byte FLAG = (byte) 126;

    // Sets the size (in bytes) of each payloads.
    private static int PAYLOAD_MAX_LENGTH = 256;

    /**
     * Splits the data in smaller payloads. Each Frame contains one (1) payload.
     * @param message The message to be sent to the receiver.
     */
    public static String[] splitMessageIntoPayloads(String message) {
        // Transform data into a stream of bits
        String stream = ConversionManager.convertDataToBitsStream(message);
        // Create payloads
        String data;
        int length = (int) Math.ceil((double) stream.length() / PAYLOAD_MAX_LENGTH);
        String[] payloads = new String[length];
        for (int i = 0, start, end, remaining; i < length; i++) {
            start = i * PAYLOAD_MAX_LENGTH;
            remaining = stream.length() - start;
            end = remaining < PAYLOAD_MAX_LENGTH ? stream.length() : (i+1) * PAYLOAD_MAX_LENGTH;
            data = stream.substring(start, end);
            payloads[i] = data;
        }
        return payloads;
    }

    /**
     *
     * @param frames
     */
    public static String extractMessageFromFrames(FrameModel[] frames) {
        return "TODO";
    }

    // --------------------------------------------------------------------

    // Bits stuffing sequence.
    private static String stuffingSequence = "11111";

    /**
     * Adds a '0' after each occurrence of "11111".
     * @param stream Stream of bits.
     */
    public static String addBitsStuffing(String stream) {
        return stream.replace(stuffingSequence, stuffingSequence + "0");
    }

    /**
     * Reverts the bits stuffing (see `encodeBitsStuffing`).
     * @param stream Stream of bits.
     */
    public static String removedBitsStuffing(String stream) {
        return stream.replace(stuffingSequence + "0", stuffingSequence);
    }
}
