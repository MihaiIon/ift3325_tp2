package managers;

import factories.FrameFactory;
import models.FrameModel;
import models.FrameWindowModel;
import models.InformationFrameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataManager {

    // Used for the bits stuffing.
    public static byte FLAG = (byte) 126;

    // Sets the size (in bytes) of each payloads.
    private static int CHARACTER_SIZE = 8;
    private static int PAYLOAD_MAX_LENGTH = CHARACTER_SIZE * 32;

    /**
     * Splits the provided message into Information Frames.
     * @param message The message to be sent to the receiver.
     */
    public static InformationFrameModel[] splitMessageIntoFrames(String message) {
        String[] payloads = splitMessageIntoPayloads(message);
        InformationFrameModel[] frames = new InformationFrameModel[payloads.length];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = FrameFactory.createInformationFrame(i, payloads[i]);
        }
        return frames;
    }

    /**
     * Splits the data in smaller payloads. Each Frame contains one (1) payload.
     * @param message The message to be sent to the receiver.
     */
    private static String[] splitMessageIntoPayloads(String message) {
        // Transform data into a stream of bits
        String stream = ConversionManager.convertMessageToStream(message);
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
     * Get the message from the windows and the frames contained by the windows
     * @param windows the list a windows to extract the message from
     * @return the message
     */
    public static String extractMessageFromFrames(ArrayList<FrameWindowModel> windows) {
        StringBuilder message = new StringBuilder();
        for (FrameWindowModel window : windows) {
            for (FrameModel frame : window.getFrames()) {
                if (frame == null) break;
                message.append(ConversionManager.convertStreamToMessage(frame.getData()));
            }
        }
        return message.toString();
    }

    /**
     *   https://www.journaldev.com/709/java-read-file-line-by-line
     *   Lit un fichier ligne par ligne et les envoie
     *   @param filepath le path du fichier
     * @return an array of information frame model for the specified message
     */
    public static InformationFrameModel[] readFile(String filepath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return splitMessageIntoFrames(sb.toString());
        } catch (IOException e) {
            System.out.println("Reader could not open : " + filepath);
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
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
