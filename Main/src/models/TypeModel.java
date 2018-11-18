package models;

import managers.ConversionManager;

public class TypeModel {

    /**
     * Used in the constructor to specify the type (role)
     * of the packet that is sent.
     */
    public enum Type {
        INFORMATION,
        CONNECTION_REQUEST,
        FRAME_RECEPTION,
        REJECTED_FRAME,
        TERMINATE_CONNECTION_REQUEST,
        P_BITS
    }

    /**
     * Provides the Type represented by the provided stream of bits.
     * @param byteStream Stream of bits
     */
    static Type parseType (String byteStream) {
        byte type = ConversionManager.convertStringToByte(byteStream);
        switch (type) {
            case (byte)'I':
                return Type.INFORMATION;
            case (byte)'C':
                return Type.CONNECTION_REQUEST;
            case (byte)'A':
                return Type.FRAME_RECEPTION;
            case (byte)'R':
                return Type.REJECTED_FRAME;
            case (byte)'F':
                return Type.TERMINATE_CONNECTION_REQUEST;
            default:
                return Type.P_BITS;
        }
    }

    // ------------------------------------------------------------------------
    // Constructor

    // Attributes
    private Type type;
    private char character;
    private byte value;

    public TypeModel(Type type, char character){
        this.type = type;
        this.character = character;
        this.value = (byte) character;
    }

    // ------------------------------------------------------------------------
    // Getters

    public Type getType() { return type; }
    public byte getValue() { return value; }
    public char getCharacter() { return character; }
}
