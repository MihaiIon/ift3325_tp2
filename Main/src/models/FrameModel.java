package models;

import factories.TypeFactory;
import managers.CheckSumManager;
import managers.ConversionManager;
import managers.DataManager;

import static models.TypeModel.Type;
import static models.RequestFrameModel.RequestType.OPEN_CONNEXION;
import static models.RequestFrameModel.RequestType.CLOSE_CONNEXION;

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
                return CheckSumManager.isFrameContentValid(frameContent);
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
                return null;
            default:
                return null;
        }
    }

    // ------------------------------------------------------------------------
    // Frame Model

    // Attributes
    private TypeModel type;
    private String metadata;
    private String data;
    private String checkSum;

    /**
     * Default constructor.
     * @param type Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(Type type, String metadata, String data) {
        this.type = TypeFactory.createTypeModel(type);
        this.metadata = metadata;
        this.data = data;
        this.checkSum = CheckSumManager.computeCheckSum(this.type.getBinaryValue(), metadata, data);
    }

    /**
     * Default constructor (+ computed checksum).
     * @param type Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(Type type, String metadata, String data, String checkSum) {
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
    public FrameModel(Type type, String metadata) {
        this.type = TypeFactory.createTypeModel(type);
        this.metadata = metadata;
        this.data = "";
        this.checkSum = CheckSumManager.computeCheckSum(this.type.getBinaryValue(), metadata, data);
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
        StringBuilder sb = new StringBuilder();
        String output = "Frame :";
        output += "\n\ttype : " + getType();
        output += "\n\ttype value : " + type.getBinaryValue();
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
    public TypeModel getTypeModel() { return type; }
    public String getMetadata() {
        return metadata;
    }
    public String getData() { return data; }
    public String getCheckSum() {
        return checkSum;
    }
    public String getFrameContent() {
        return type.getBinaryValue() + metadata + data + checkSum;
    }
}
