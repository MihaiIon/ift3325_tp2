package managers;


import models.PacketModel;
import models.PayloadModel;

public class DataManager {

    // Used for the bits stuffing.
    public static byte FLAG = (byte) 126;

    // Sets the size (in bytes) of each payloads.
    public static int PAYLOAD_MAX_LENGTH = 256;

    /**
     * Splits the data in smaller payloads. Each packet contains one (1) payload.
     * @param message The message to be sent to the receiver.
     */
    public static PayloadModel[] splitMessageToPayloads(String message) {
        // Transform data into a stream of bits
        String stream = ConversionManager.convertDataToBitsStream(message);
        stream = encodeBitsStuffing(stream);
        // Create payloads
        String data;
        int length = (int) Math.ceil((double) stream.length() / PAYLOAD_MAX_LENGTH);
        PayloadModel[] payloads = new PayloadModel[length];
        for (int i = 0, start, end, remaining; i < length; i++) {
            start = i * PAYLOAD_MAX_LENGTH;
            remaining = stream.length() - start;
            end = remaining < PAYLOAD_MAX_LENGTH ? stream.length() : (i+1) * PAYLOAD_MAX_LENGTH;
            data = stream.substring(start, end);
            payloads[i] = new PayloadModel(data, CheckSumManager.computeCheckSum(data));
        }
        return payloads;
    }

    /**
     * Provides the original data.
     * @param packets The received packets.
     * @return Original data.
     */
    public static String extractDataFromPackets(PacketModel[] packets) {
        String stream = getDataStream(packets);
        return ConversionManager.convertBitsStreamToData(stream);
    }

    /**
     * Provides the original stream (without bits stuffing).
     * @param packets The received packets.
     */
    private static String getDataStream(PacketModel[] packets) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < packets.length; i++) {
            sb.append(packets[i].getPayload());
        }
        return decodeBitsStuffing(sb.toString());
    }

    // --------------------------------------------------------------------

    // Bits stuffing sequence.
    private static String stuffingSequence = "11111";

    /**
     * Adds a '0' after each occurrence of "11111".
     * @param stream Stream of bits.
     */
    private static String encodeBitsStuffing(String stream) {
        return stream.replace(stuffingSequence, stuffingSequence + "0");
    }

    /**
     * Reverts the bits stuffing (see `encodeBitsStuffing`).
     * @param stream Stream of bits.
     */
    private static String decodeBitsStuffing(String stream) {
        return stream.replace(stuffingSequence + "0", stuffingSequence);
    }
}
