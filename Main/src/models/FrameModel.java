package models;

import factories.TypeFactory;
import managers.CheckSumManager;
import managers.ConversionManager;
import managers.DataManager;

import static models.TypeModel.Type;

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
                String frameContent = stream.substring(fLength, length - fLength);
                return CheckSumManager.isFramContentValid(frameContent);
            }
            return false;
        }
        return false;
    }

    /**
     * Converts the provided binary data to a FrameModel Object.
     * @param stream Stream of bits representing the Frame.
     * @return FrameModel Object.
     */
    public static FrameModel convertStreamToFrame(String stream) {
        // Save lengths
        int length = stream.length();
        int fLength = ConversionManager.convertByteToString(DataManager.FLAG).length();
        int gLength = CheckSumManager.generator.length();
        // Parse info
        Type type = TypeModel.parseType(stream.substring(fLength, fLength + 8));
        byte metadata = ConversionManager.convertStringToByte(stream.substring(fLength + 8, fLength + 16));
        String checkSum = stream.substring(length - gLength - fLength, length - fLength);
        // Parse Frame
        switch (type) {
            case INFORMATION:
                String data = stream.substring(fLength + 16, length - gLength - fLength);
                return new InformationFrameModel(metadata, data, checkSum);
            case CONNECTION_REQUEST:
                return new ConnexionRequestFrame(metadata, checkSum);
            case FRAME_RECEPTION:
//                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            case REJECTED_FRAME:
//                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            case ENDING_CONNECTION:
//                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            default:
                return null;
        }
    }

    // ------------------------------------------------------------------------
    // Frame Model

    // Attributes
    private byte metadata;
    private TypeModel type;
    private String data;
    private String checkSum;

    /**
     * Default constructor.
     * @param type Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(Type type, byte metadata, String data) {
        this.type = TypeFactory.createTypeModel(type);
        this.metadata = metadata;
        this.data = data;
        this.checkSum = CheckSumManager.computeCheckSum(this.type.getValue(), metadata, data);
    }

    /**
     * Default constructor (+ computed checksum).
     * @param type Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(Type type, byte metadata, String data, String checkSum) {
        this.type = TypeFactory.createTypeModel(type);
        this.metadata = metadata;
        this.data = data;
        this.checkSum = checkSum;
    }

    /**
     * No data constructor.
     * @param type Frame's metadata.
     * @param metadata Frame's metadata.
     */
    public FrameModel(Type type, byte metadata) {
        this.type = TypeFactory.createTypeModel(type);
        this.metadata = metadata;
        this.data = "";
        this.checkSum = CheckSumManager.computeCheckSum(this.type.getValue(), metadata, data);
    }

    // ------------------------------------------------------------------------
    // Methods

    /**
     * Converts FrameModel object to binary number (String representation).
     */
    public String toBinary() {
        String flag = ConversionManager.convertByteToString(DataManager.FLAG);
        String frameContent = ConversionManager.convertByteToString(type.getValue());
        frameContent += ConversionManager.convertByteToString(metadata);
        frameContent += data + checkSum;
        frameContent = DataManager.addBitsStuffing(frameContent);
        return flag + frameContent + flag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String output = "Frame :";
        output += "\n\ttype : " + getType();
        output += "\n\tmetadata : " + metadata;
        output += "\n\tdata : " + ConversionManager.convertStreamToReadableStream(data);
        output += "\n\tcheckSum : " + ConversionManager.convertStreamToReadableStream(checkSum);
        output += "\n\tbinary : " + toBinary();
        output += "\n\tisValid : " + isFrameValid(toBinary());
        return output;
    }

    // ------------------------------------------------------------------------
    // Getters

    public Type getType() { return type.getType(); }
    public byte getMetadata() {
        return metadata;
    }
    public String getData() { return data; }
    public String getCheckSum() {
        return checkSum;
    }
}
