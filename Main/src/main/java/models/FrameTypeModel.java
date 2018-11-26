package models;

import managers.ConversionManager;

public class FrameTypeModel {

    /**
     * Used in the constructor to specify the frameType (role)
     * of the packet that is sent.
     */
    public enum FrameType {
        INFORMATION,
        CONNECTION_REQUEST,
        FRAME_RECEPTION,
        REJECTED_FRAME,
        TERMINATE_CONNECTION_REQUEST,
        P_BITS,
        BAD_FRAME
    }

    /**
     * Provides the FrameType represented by the provided stream of bits.
     * @param byteStream Stream of bits
     */
    public static FrameType parseFrameType(String byteStream) {
        byte type = ConversionManager.convertStringToByte(byteStream);
        switch (type) {
            case (byte)'I':
                return FrameType.INFORMATION;
            case (byte)'C':
                return FrameType.CONNECTION_REQUEST;
            case (byte)'A':
                return FrameType.FRAME_RECEPTION;
            case (byte)'R':
                return FrameType.REJECTED_FRAME;
            case (byte)'F':
                return FrameType.TERMINATE_CONNECTION_REQUEST;
            case (byte) 'P':
                return FrameType.P_BITS;
            default:
                return FrameType.BAD_FRAME;
        }
    }

    // ------------------------------------------------------------------------
    // Constructor

    // Attributes
    private FrameType frameType;
    private char character;
    private byte value;

    public FrameTypeModel(FrameType frameType, char character){
        this.frameType = frameType;
        this.character = character;
        this.value = (byte) character;
    }

    // ------------------------------------------------------------------------
    // Getters

    public FrameType getType() { return frameType; }
    public byte getValue() { return value; }
    public char getCharacter() { return character; }
    public String toBinary() { return ConversionManager.convertByteToString(value); }
}
