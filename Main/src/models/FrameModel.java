package models;

import managers.CheckSumManager;
import managers.ConversionManager;
import managers.DataManager;

import static models.FrameModel.Type.INFORMATION;

public class FrameModel {

    // ------------------------------------------------------------------------
    // Static

    /**
     * Used in the constructor to specifiy the type (role)
     * of the packet that is sent.
     */
    public enum Type {
        INFORMATION,
        CONNECTION_REQUEST, FRAME_RECEPTION, REJECTED_FRAME,
        ENDING_CONNECTION,
        P_BITS
    }

    /**
     * Checks if the frame is damaged.
     * @param stream Stream of bits representing the Frame.
     * @return Returns TRUE is the Frame is valid.
     */
    public static boolean isFrameValid(String stream) {
        return true;
    }

    /**
     * Converts the provided binary data to a FrameModel Object.
     * @param stream Stream of bits representing the Frame.
     * @return FrameModel Object.
     */
    public static FrameModel convertToFrame(String stream) {
        // Save lengths
        int streamLength = stream.length();
        int generatorLength = CheckSumManager.generator.length();
        // Parse type
        Type type = parseType(stream.substring(0, 8));
        // Parse Frame
        switch (type) {
            case INFORMATION:
                byte id = ConversionManager.convertStringToByte(stream.substring(8, 16));
                String data = stream.substring(16, streamLength - generatorLength);
                String checkSum = stream.substring(streamLength - generatorLength);
                return new FrameModel(id, type, new PayloadModel(data, checkSum));
            case CONNECTION_REQUEST:
                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            case FRAME_RECEPTION:
                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            case REJECTED_FRAME:
                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            case ENDING_CONNECTION:
                return new FrameModel((byte)0, type, new PayloadModel("", ""));
            default:
                return new FrameModel((byte)0, type, new PayloadModel("", ""));
        }
    }

    /**
     * @param type The type of the Frame.
     * @return Encodes the type of the Frame on 8 bits (byte).
     */
    private static byte convertTypeToByte(Type type) {
        switch (type) {
            case INFORMATION:
                return (byte) 'I';
            case CONNECTION_REQUEST:
                return (byte) 'C';
            case FRAME_RECEPTION:
                return (byte) 'A';
            case REJECTED_FRAME:
                return (byte) 'R';
            case ENDING_CONNECTION:
                return (byte) 'F';
            default:
                return (byte) 'P';
        }
    }

    /**
     *
     * @param byteStream
     * @return
     */
    private static Type parseType (String byteStream) {
        return Type.INFORMATION;
    }

    // ------------------------------------------------------------------------
    // Packet Model

    // Attributes
    private byte id;
    private byte type;
    private PayloadModel payload;

    /**
     * Information Frame constructor.
     * @param id Identifies the Frame (0-7).
     * @param type Identifies the type of the Frame (see class Type).
     * @param payload Frame's data.
     */
    public FrameModel(byte id, Type type, PayloadModel payload) {
        this.id = id;
        this.type = FrameModel.convertTypeToByte(type);
        this.payload = payload;
    }

    /**
     * Converts FrameModel object to binary number (String representation).
     */
    public String toBinary() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConversionManager.convertByteToString(DataManager.FLAG));
        sb.append(ConversionManager.convertByteToString(type));
        switch (getType()) {
            case INFORMATION:
                sb.append(ConversionManager.convertByteToString(id));
                sb.append(payload.toString());
                break;
            case FRAME_RECEPTION:
                break;
            case REJECTED_FRAME:
                break;
            case ENDING_CONNECTION:
                break;
            default:

        }
        sb.append(ConversionManager.convertByteToString(DataManager.FLAG));
        return sb.toString();
    }

    // ------------------------------------------------------------------------
    // Getters

    /**
     * @return Provides the identifier of the Frame.
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return Provides the type of the Frame.
     */
    public Type getType() {
        char type = (char) (this.type & 0xFF);
        switch (type) {
            case 'I':
                return INFORMATION;
            case 'C':
                return Type.CONNECTION_REQUEST;
            case 'A':
                return Type.FRAME_RECEPTION;
            case 'R':
                return Type.REJECTED_FRAME;
            case 'F':
                return Type.ENDING_CONNECTION;
            default:
                return Type.P_BITS;
        }
    }

    public PayloadModel getPayload() { return payload; }
    public String getData() { return payload.getData(); }
    public String getCheckSum() {
        return payload.getCheckSum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Frame : ");
        sb.append("\n\ttype : ").append(getType());
        switch (getType()) {
            case INFORMATION:
                sb.append("\n\tid : ").append(getId());
                sb.append("\n\tdata : ").append(ConversionManager.convertStreamToReadableStream(getData()));
                sb.append("\n\tcheckSum : ").append(ConversionManager.convertStreamToReadableStream(getCheckSum()));
                break;
            case FRAME_RECEPTION:
                break;
            case REJECTED_FRAME:
                break;
            case ENDING_CONNECTION:
                break;
            default:
        }
        sb.append("\n\tbinary : " + toBinary());
        return sb.toString();
    }
}
