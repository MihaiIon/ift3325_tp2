package managers;

import models.FrameModel;
import models.InformationFrameModel;

public class TestManager {

    /**
     *
     * @param message
     */
    public static void testMessageTransmission(String message) {
        System.out.println("\n==================================================================");
        System.out.println("Testing Data : " + message);
        System.out.println("==================================================================");

        // Build payloads
        String[] payloads = DataManager.splitMessageToPayloads(message);

        // Create Frames
        FrameModel[] packetsSent = new FrameModel[payloads.length];
        for (int i = 0; i < packetsSent.length; i++) {
            packetsSent[i] = new InformationFrameModel(i, payloads[i]);
            System.out.println("== Frame Created ==");
            System.out.println(packetsSent[i].toString());
        }

        // Create Stream
        FrameModel[] packetsReceived = new FrameModel[packetsSent.length];
        for (int i = 0; i < packetsReceived.length; i++) {
            packetsReceived[i] = FrameModel.convertStreamToFrame(packetsSent[i].toBinary());

        }
    }

    public static void testChecksum() {
        System.out.println("\n==================================================================");
        System.out.println("Testing CheckSumManager.computeCheckSum()");
        System.out.println("==================================================================");

        // Test 1
        String stream = "1000100000010001";
        System.out.println("\nInput : " + ConversionManager.convertStreamToReadableStream(stream));
        System.out.println("Expected : " + ConversionManager.convertStreamToReadableStream("0000000000000000"));
        System.out.println("Computed : " + ConversionManager.convertStreamToReadableStream(CheckSumManager.computeCheckSum(stream)));
        // Test 2
        stream = "10001000000100010000000000000000";
        System.out.println("\nInput : " + ConversionManager.convertStreamToReadableStream(stream));
        System.out.println("Expected : " + ConversionManager.convertStreamToReadableStream("0000000000000000"));
        System.out.println("Computed : " + ConversionManager.convertStreamToReadableStream(CheckSumManager.computeCheckSum(stream)));
        // Test 3
        stream = "0100010111001000";
        System.out.println("\nInput : " + ConversionManager.convertStreamToReadableStream(stream));
        System.out.println("Expected : " + ConversionManager.convertStreamToReadableStream("0110100000011010"));
        System.out.println("Computed : " + ConversionManager.convertStreamToReadableStream(CheckSumManager.computeCheckSum(stream)));
        // Test 3
        stream = "01000101110010000110100000011010";
        System.out.println("\nInput : " + ConversionManager.convertStreamToReadableStream(stream));
        System.out.println("Expected : " + ConversionManager.convertStreamToReadableStream("0000000000000000"));
        System.out.println("Computed : " + ConversionManager.convertStreamToReadableStream(CheckSumManager.computeCheckSum(stream)));
    }
}
