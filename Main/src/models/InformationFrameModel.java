package models;

import managers.ConversionManager;

public class InformationFrameModel extends FrameModel {

    /**
     * Default constructor.
     * @param id Identifies the Frame (0-7).
     * @param data Frame's data.
     */
    public InformationFrameModel(int id, String data) {
        super(TypeModel.Type.INFORMATION, ConversionManager.convertByteToString((byte)(id%8)), data);
    }

    /**
     * Default constructor (+ computed checksum).
     * @param id Identifies the type of the Frame (see class Type).
     * @param data Frame's data.
     * @param checksum Frame's checkSum.
     */
    public InformationFrameModel(byte id, String data, String checksum) {
        super(TypeModel.Type.INFORMATION, ConversionManager.convertByteToString(id), data, checksum);
    }

    // ----------------------------------------------------------------
    // Getters

    public int getId() { return (int) ConversionManager.convertStringToByte(getMetadata()); }
}
