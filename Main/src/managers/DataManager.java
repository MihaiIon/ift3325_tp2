package managers;


import models.PacketModel;
import models.PayloadModel;

public class DataManager {

    // Used for the bits stuffing.
    public static byte FLAG = (byte) 126;

    // Sets the size (in bytes) of each payloads.
    public static int PAYLOAD_SIZE = 4;

    /**
     * Splits the data in smaller payloads. Each packet contains one (1) payload.
     * @param data The data to be sent to the receiver.
     */
    public static PayloadModel[] splitDataToPayloads(String data) {
        // Transform data into a stream of bits
        String stream = ConversionManager.convertDataToBitsStream(data);
        stream = encodeBitsStuffing(stream);
        // Add zeros at the end of the stream until we have a multiple of 32 (bits).
        String paddedStream = stream;
        int payloadSize = PAYLOAD_SIZE * 8;
        int remainder = stream.length() % payloadSize;
        if(remainder != 0) {
            int expectedLength = payloadSize * ((int) Math.ceil((double) stream.length() / payloadSize));
            String format = "%1$-"+ expectedLength +"s";
            paddedStream = String.format(format, stream).replace(' ', '0');
        }
        // Create payloads
        int length = (int) Math.floor((double) paddedStream.length() / payloadSize);
        PayloadModel[] payloads = new PayloadModel[length];
        for (int i = 0; i < length; i++) {
            payloads[i] = new PayloadModel(paddedStream.substring(i * payloadSize, (i+1) * payloadSize));
        }
        return payloads;
    }

    /**
     * Provides the original data.
     * @param packets The received packets.
     * @return
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
        String stream = "";
        for (int i = 0; i < packets.length; i++) {
            stream += packets[i].getPayload().toString();
        }
        return decodeBitsStuffing(stream);
    }

    /**
     *
     * @param stream
     */
    private static String encodeBitsStuffing(String stream) {
        String sequence = ConversionManager.convertByteToString(FLAG);
        return stream.replace(sequence, sequence + sequence);
    }

    /**
     *
     * @param stream
     */
    private static String decodeBitsStuffing(String stream) {
        String sequence = ConversionManager.convertByteToString(FLAG);
        return stream.replace(sequence + sequence, sequence);
    }
}
