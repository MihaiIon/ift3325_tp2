package models;

import factories.BadFrameFactory;
import factories.TypeFactory;
import managers.CheckSumManager;
import managers.ConversionManager;
import managers.DataManager;

import static models.FrameTypeModel.FrameType;
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
                String frameContent = DataManager.removedBitsStuffing(stream.substring(fLength, length - fLength));
                // Checksum is valid
                if(!CheckSumManager.isFrameContentValid(frameContent)) return false;
                // Prepare variables
                String info = TypeFactory.createTypeModel(FrameType.INFORMATION).toBinary();
                String rec = TypeFactory.createTypeModel(FrameTypeModel.FrameType.FRAME_RECEPTION).toBinary();
                String rej = TypeFactory.createTypeModel(FrameType.REJECTED_FRAME).toBinary();
                String type = frameContent.substring(0, 8);

                // Check if information frame has valid data (length).
                int dataLength = frameContent.length() - 16 - gLength;
                if(type.equals(info) && (dataLength == 0 || dataLength % 8 != 0)) return false;

                // Check if type is valid.
                FrameTypeModel.FrameType tm = FrameTypeModel.parseFrameType(type);
                if(tm == FrameType.BAD_FRAME) return false;

                // Check if id is valid
                if(tm == FrameTypeModel.FrameType.INFORMATION || tm == FrameType.FRAME_RECEPTION|| tm == FrameTypeModel.FrameType.REJECTED_FRAME) {
                    ByteModel id = new ByteModel(ConversionManager.convertStringToByte(frameContent.substring(8, 16)));
                    return id.getValue() >= 0 && id.getValue() < 8;
                }
                return true;
            }
        }
        return false;
    }

    protected FrameModel() {}

    /**
     * Converts the provided binary data to a FrameModel Object.
     * @param stream Stream of bits representing the Frame.
     * @return FrameModel Object.
     */
    public static FrameModel convertStreamToFrame(String stream) {
        if(isFrameValid(stream)) {
            // Save lengths
            int fLength = ConversionManager.convertByteToString(DataManager.FLAG).length();
            int gLength = CheckSumManager.generator.length();
            // Remove stuffed Bits
            String frameContent = DataManager.removedBitsStuffing(stream.substring(fLength, stream.length() - fLength));
            int length = frameContent.length();
            // Parse info
            FrameType frameType = FrameTypeModel.parseFrameType(frameContent.substring(0, 8));
            byte metadata = ConversionManager.convertStringToByte(frameContent.substring(8, 16));
            String checkSum = frameContent.substring(length - gLength);
            // Parse Frame
            switch (frameType) {
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
                    break;
            }
        }
        return BadFrameFactory.createSimpleBadFrame();
    }

    // ------------------------------------------------------------------------
    // Frame Model

    // Attributes
    private String flag;
    private FrameTypeModel type;
    private ByteModel metadata;
    private String data;
    private String checkSum;

    /**
     * Do not use this constructor unless you know what you're doing.
     * @param flag, Custom Flag.
     * @param t Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(String flag, FrameType t, ByteModel metadata, String data, String checkSum) {
        this.flag = flag;
        this.type = TypeFactory.createTypeModel(t);
        this.metadata = metadata;
        this.data = data;
        this.checkSum = checkSum.equals("-1") ? CheckSumManager.computeCheckSum(type.toBinary(), metadata.toBinary(), data) : checkSum;
    }

    /**
     * Default constructor.
     * @param t Frame's metadata.
     * @param metadata Frame's metadata.
     * @param data Frame's data.
     */
    public FrameModel(FrameType t, ByteModel metadata, String data) {
        this.flag = ConversionManager.convertByteToString(DataManager.FLAG);
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
    public FrameModel(FrameType t, ByteModel metadata, String data, String checkSum) {
        this.flag = ConversionManager.convertByteToString(DataManager.FLAG);
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
    public FrameModel(FrameTypeModel.FrameType t, ByteModel metadata) {
        this.flag = ConversionManager.convertByteToString(DataManager.FLAG);
        this.type = TypeFactory.createTypeModel(t);
        this.metadata = metadata;
        this.data = "";
        this.checkSum = CheckSumManager.computeCheckSum(type.toBinary(), metadata.toBinary(), data);
    }

    // ------------------------------------------------------------------------
    // Methods

    /**
     * Converts FrameModel object to binary number (String representation).
     */
    public String toBinary() {
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

    public FrameTypeModel.FrameType getType() { return type.getType(); }
    public FrameTypeModel getTypeModel() { return type; }
    public ByteModel getMetadata() { return metadata; }
    public String getData() { return data; }
    public String getCheckSum() { return checkSum; }
    public String getFrameContent() {
        return type.toBinary() + metadata.toBinary() + data + checkSum;
    }

    /**
     * DANGER
     * @param id
     */
    public void setId(int id) {
        metadata = new ByteModel(id);
    }
}
