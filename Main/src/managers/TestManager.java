package managers;

import models.FrameModel;
import models.InformationFrameModel;

public class TestManager {

    public static void testMessageTransmission(String message) {
        System.out.println("\n==================================================================");
        System.out.println("Testing Data : " + message);
        System.out.println("==================================================================");

        // Build payloads
        String[] payloads = DataManager.splitMessageIntoPayloads(message);

        // Create Frames
        System.out.println("== Sending Frames ==");
        FrameModel[] framesSent = new FrameModel[payloads.length];
        for (int i = 0; i < framesSent.length; i++) {
            framesSent[i] = new InformationFrameModel(i, payloads[i]);
            System.out.println("== Frame Created ==");
            System.out.println(framesSent[i]);
        }

        // Create Stream
        System.out.println("\n== Receiving Frames ==");
        FrameModel[] framesReceived = new FrameModel[framesSent.length];
        for (int i = 0; i < framesReceived.length; i++) {
            framesReceived[i] = FrameModel.convertStreamToFrame(framesSent[i].toBinary());
            System.out.println("== Frame Created ==");
            System.out.println(framesReceived[i]);
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
