package managers;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import factories.BadFrameFactory;
import models.BadFrame;
import models.FrameModel;
import models.FrameWindowModel;

import java.util.ArrayList;

public class TestManager {

    public static void testMessageTransmission(String message) {
        System.out.println(ansi().fg(BLUE).a("\n\n\n=================================================================="));
        System.out.println(ansi().fg(BLUE).a("\tTesting Transmission"));
        System.out.println(ansi().fg(BLUE).a("=================================================================="));

        // Message
        System.out.println("\n***** Encoding message ****");
        System.out.println("== Message sent : " + message + " ==");
        FrameModel[] framesSent = DataManager.splitMessageIntoFrames(message);

        // Sending
        System.out.println("\n***** Sending Frames ****");
        for (FrameModel frame : framesSent) {
            System.out.println("== Frame Created ==");
            System.out.println(frame);
        }

        // Receiving
        System.out.println("\n***** Receiving Frames  ****");
        ArrayList<FrameWindowModel> windows = new ArrayList<>();
        FrameWindowModel window = new FrameWindowModel();
        FrameModel receivedFrame = null;
        for (FrameModel frame : framesSent) {
            // Adjust window
            if (window.isFull()) {
                windows.add(window);
                window = new FrameWindowModel();
            }
            // Parse binary data
            System.out.println("== Frame Created ==");
            try {
                receivedFrame = FrameModel.convertStreamToFrame(frame.toBinary());
            } catch (Exception e) {

            }
            if (receivedFrame != null && !receivedFrame.hasErrors()) {
                if(window.addFrame(receivedFrame)){
                    System.out.println(receivedFrame);
                }
            } else {
                System.out.println("Something went wrong");
                System.exit(0);
            }
        }
        windows.add(window);

        // Extracting
        System.out.println("\n***** Extracting message ****");
        String receivedMessage = DataManager.extractMessageFromFrames(windows);
        System.out.println("== Message received : " + receivedMessage + " ==");
    }

    public static void testChecksum() {
        System.out.println(ansi().fg(BLUE).a("\n\n\n==================================================================").reset());
        System.out.println(ansi().fg(BLUE).a("\tTesting CheckSumManager.computeCheckSum()").reset());
        System.out.println(ansi().fg(BLUE).a("==================================================================").reset());

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

    public static void testErrors() {
        System.out.println(ansi().fg(BLUE).a("\n\n\n==================================================================").reset());
        System.out.println(ansi().fg(BLUE).a("\tTesting Errors : Is Valid?").reset());
        System.out.println(ansi().fg(BLUE).a("==================================================================").reset());

        int i = 0;
        StringBuilder sb = new StringBuilder();
        FrameModel badFrame;
        for (BadFrame.BadFrameType type : BadFrame.BadFrameType.values()) {
            badFrame = BadFrameFactory.createBadFrame(type);
            sb.append("\n\nTest Case ").append(i).append(" ").append(type).append(".");
            sb.append("\nInput : ").append(badFrame.toBinary());
            sb.append("\nExpected : false");
            sb.append("\nComputed : ").append(FrameModel.isFrameValid(badFrame.toBinary()));
            i++;
        }
        System.out.println(sb.toString());
    }
}
