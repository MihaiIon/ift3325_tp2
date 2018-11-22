package models;

import factories.TypeFactory;
import managers.CheckSumManager;
import managers.ConversionManager;
import managers.DataManager;

import static models.TypeModel.Type;
import static models.RequestFrameModel.RequestType.OPEN_CONNEXION;
import static models.RequestFrameModel.RequestType.CLOSE_CONNEXION;

import java.util.Arrays;

public class FrameModel {

    /**
     * Checks if the frame is damaged.
     * @param stream Stream of bits representing the Frame.
     * @return Returns TRUE is the Frame is valid.
     */
    public static boolean isFrameValid(String stream) {
        // Must be at least 32 bits of length.
        String flag = ConversionManager.convertByteToString(DataManager.FLAG);
        int length = stream.length();
        int fLength = flag.length();
        int gLength = CheckSumManager.generator.length();
        int minLength = (2 * fLength) + 16 + gLength;
        if (length >= minLength) {
            String start = stream.substring(0, fLength);
            String end = stream.substring(length - fLength);
            // Starts and ends with a Flag.
            if(flag.equals(start) && flag.equals(end)){
                String frameContent = DataManager.removedBitsStuffing(stream.substring(fLength, length - fLength));
                return CheckSumManager.isFrameContentValid(frameContent);
            }
        }
        return false;
    }

    /**
     * Converts the provided binary data to a FrameModel Object.
     * @param stream Stream of bits representing the Frame.
     * @return FrameModel Object.
     */
    private static FrameModel convertStreamToFrame(String stream) {
        // Save lengths
        int fLength = ConversionManager.convertByteToString(DataManager.FLAG).length();
        int gLength = CheckSumManager.generator.length();
        // Remove stuffed Bits
        String frameContent = DataManager.removedBitsStuffing(stream.substring(fLength, stream.length() - fLength));
        int length = frameContent.length();
        // Parse info
        Type type = TypeModel.parseType(frameContent.substring(0, 8));
        byte metadata = ConversionManager.convertStringToByte(frameContent.substring(8, 16));
        String checkSum = frameContent.substring(length - gLength);
        // Parse Frame
        switch (type) {
            case INFORMATION:
                String data = frameContent.substring(16, length - gLength);
                return new InformationFrameModel(metadata, data, checkSum);
            case CONNECTION_REQUEST:
                return new RequestFrameModel(OPEN_CONNEXION, checkSum);
            case FRAME_RECEPTION:
                return new ReceptionFrameModel(metadata, checkSum);
            case REJECTED_FRAME:
                return new RejectionFrameModel(metadata, checkSum);
            case TERMINATE_CONNECTION_REQUEST:
                return new RequestFrameModel(CLOSE_CONNEXION, checkSum);
            case P_BITS:
                return new PBitFrameModel(metadata, checkSum);
            default:
                return null;
        }
    }

    /**
     * Converts a the provided binary data to a list of FrameModel Object.
     * @param stream Stream of bits representing one or more Frames.
     */
    public static ArrayList<FrameModel> convertStreamToFrames(String stream) {
        // Convert to iynary string.
        String binaryFlag = ConversionManager.convertByteToString(DataManager.FLAG);
        // Frames parsed.
        ArrayList<FrameModel> frames = new ArrayList<>();
        // True if we've found the first flag of a frame.
        boolean startFlagfound = false; 
        // True if we matched a flag. 
        boolean match = false;
        // Saved position of the start of a frame.
        int saved = 0;

        for(int i = 0; i < stream.length(); i++) {
            if (stream.charAt(i) == binaryFlag.charAt(0)) {

                // Match the flag.                
                match = true;
                for(int j = 1; j < binaryFlag.length(); j++) {
                    if(stream.charAt(i+j) != binary.charAt(j)) {
                        match = false;
                        break;
                    }
                }

                // We've found the start flag.
                if (match && !startFlagfound) {
                    startFlagfound = true;
                }

                // We've found the end flag.
                else if (match && startFlagfound) {
                    startFlagfound = false;
                    // Extract Frame.
                    i = i+binaryFlag.length();
                    frames.add(FrameModel.convertStreamToFrame(stream.substring(saved, i)))
                    saved = i;
                }

                // Reset
                match = false;
            }
        }


        return frames;
    }

    // ------------------------------------------------------------------------
    // Frame Model

    // Attributes
    private TypeModel type;
    private ByteModel metadata;
    private String data;
    private String checkSum;

    /**
     * Default constructor.
     * @param t Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(Type t, ByteModel metadata, String data) {
        this.type = TypeFactory.createTypeModel(t);
        this.metadata = metadata;
        this.data = data;
        this.checkSum = CheckSumManager.computeCheckSum(type.toBinary(), metadata.toBinary(), data);
    }

    /**
     * Default constructor (+ computed checksum).
     * @param t Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(Type t, ByteModel metadata, String data, String checkSum) {
        this.type = TypeFactory.createTypeModel(t);
        this.metadata = metadata;
        this.data = data;
        this.checkSum = checkSum;
    }

    /**
     * No data constructor.
     * @param t Frame's metadata.
     * @param metadata Frame's metadata.
     */
    public FrameModel(Type t, ByteModel metadata) {
        this.type = TypeFactory.createTypeModel(t);
        this.metadata = metadata;
        this.data = "";
        this.checkSum = CheckSumManager.computeCheckSum(type.toBinary(), metadata.toBinary(), data);
    }

    // ------------------------------------------------------------------------
    // Methods

    public boolean hasErrors() {
        return !CheckSumManager.isFrameContentValid(getFrameContent());
    }

    /**
     * Converts FrameModel object to binary number (String representation).
     */
    public String toBinary() {
        String flag = ConversionManager.convertByteToString(DataManager.FLAG);
        String content = DataManager.addBitsStuffing(getFrameContent());
        return flag + content + flag;
    }

    @Override
    public String toString() {
        String output = "Frame :";
        output += "\n\ttype : " + getType();
        output += "\n\ttype (binary) : " + type.toBinary();
        output += "\n\tmetadata : " + metadata.getValue();
        output += "\n\tmetadata (binary) : " + metadata.toBinary();
        output += "\n\tdata : " + ConversionManager.convertStreamToReadableStream(data);
        output += "\n\tcheckSum : " + ConversionManager.convertStreamToReadableStream(checkSum);
        output += "\n\tbinary : " + toBinary();
        output += "\n\tisValid : " + isFrameValid(toBinary());
        return output;
    }

    // ------------------------------------------------------------------------
    // Getters

    public Type getType() { return type.getType(); }
    public TypeModel getTypeModel() { return type; }
    public ByteModel getMetadata() { return metadata; }
    public String getData() { return data; }
    public String getCheckSum() { return checkSum; }
    public String getFrameContent() {
        return type.toBinary() + metadata.toBinary() + data + checkSum;
    }
}
