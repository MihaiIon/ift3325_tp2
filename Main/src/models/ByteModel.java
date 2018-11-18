package models;

import managers.ConversionManager;

public class ByteModel {

    private byte value;
    private String binary;

    public ByteModel (byte b) {
        value = b;
        binary = ConversionManager.convertByteToString(b);
    }

    public ByteModel (int i) {
        value = (byte)i;
        binary = ConversionManager.convertByteToString(value);
    }

    public byte getValue() { return value; }
    public String toBinary() { return binary; }
    public int toInt() { return (int) value; }
}
